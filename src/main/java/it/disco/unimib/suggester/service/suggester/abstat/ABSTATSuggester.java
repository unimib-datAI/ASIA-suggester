package it.disco.unimib.suggester.service.suggester.abstat;

import com.google.gson.Gson;
import it.disco.unimib.suggester.configuration.ConfigProperties;
import it.disco.unimib.suggester.configuration.SuggesterConfiguration;
import it.disco.unimib.suggester.model.suggestion.Suggestion;
import it.disco.unimib.suggester.service.suggester.ISuggester;
import it.disco.unimib.suggester.service.suggester.SuggesterUtils;
import it.disco.unimib.suggester.service.suggester.abstat.domain.Authentication;
import it.disco.unimib.suggester.service.suggester.abstat.domain.Summaries;
import it.disco.unimib.suggester.service.suggester.abstat.domain.Suggestions;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import okhttp3.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.xml.ws.http.HTTPException;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;

import static it.disco.unimib.suggester.service.suggester.SuggesterUtils.extractEntityNameFromURI;
import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.CollectionUtils.isEmpty;

@Service
public class ABSTATSuggester implements ISuggester {

    private final Gson gson = new Gson();

    @Getter
    @Setter
    private boolean test = false;
    @Getter
    private List<String> preferredSummaries;
    private final ConfigProperties properties;

    private final SuggesterUtils suggesterUtils;

    private String authToken = null;

    public ABSTATSuggester(ConfigProperties properties,
                           SuggesterConfiguration.DistanceCalculator distanceCalculator,
                           SuggesterUtils suggesterUtils) {
        this.properties = properties;
        this.suggesterUtils = suggesterUtils;
    }

    private static String stringPreprocessing(String str) {
        Matcher m = SuggesterUtils.notAlphanumeric.matcher(str);
        str = m.replaceAll(" ");
        m = SuggesterUtils.spaces.matcher(str);
        str = m.replaceAll(" ");
        //   String[] words = str.split(" ");
        //    for (int i = 1; i < words.length; i++)
        //        words[i] = words[i].substring(0, 1).toUpperCase() + words[i].substring(1);
        return str;
    }

    @Override
    public void setPreferredSummaries(List<String> preferredSummaries) {
        this.preferredSummaries = preferredSummaries;
    }

    @Override
    public List<Suggestion> propertySuggestions(@NonNull String keyword) {
        return abstatListSuggestions(keyword, Position.PRED);
    }

    @Override
    public List<Suggestion> typeSuggestions(@NonNull String keyword) {
        return abstatListSuggestions(keyword, Position.SUBJ);
    }

    @Override
    public List<Suggestion> objectSuggestions(@NonNull String keyword) {
        return abstatListSuggestions(keyword, Position.OBJ);
    }

    @Override
    public List<List<Suggestion>> objectSuggestionsMultipleKeywords(@NonNull List<String> keywords) {
        return listSuggestionsMultipleKeywords(keywords, Position.OBJ);
    }

    @Override
    public List<List<Suggestion>> propertySuggestionsMultipleKeywords(@NonNull List<String> keywords) {
        return listSuggestionsMultipleKeywords(keywords, Position.PRED);
    }

    @Override
    public List<List<Suggestion>> typeSuggestionsMultipleKeywords(@NonNull List<String> keywords) {
        return listSuggestionsMultipleKeywords(keywords, Position.SUBJ);
    }

    @Override
    public List<String> getSummaries() {
        try {
            return summaries().getDatasetsNames();
        } catch (IOException e) {
            e.printStackTrace();
            return emptyList();
        }
    }

    private String abstatSuggestions(@NonNull String keyword, @NonNull Position position) throws IOException {
        String url = properties.getAbstat().getFullSuggestEndpoint();

        HttpUrl.Builder urlBuilder = requireNonNull(HttpUrl.parse(url)).newBuilder();
        if (!StringUtils.isEmpty(keyword)) {
            urlBuilder.addQueryParameter("qString", keyword);
            urlBuilder.addQueryParameter("qPosition", position.getValue());
            urlBuilder.addQueryParameter("rows", "15");
            urlBuilder.addQueryParameter("start", "0");

            if (!isEmpty(preferredSummaries)) // check nullity and emptiness
                urlBuilder.addQueryParameter("dataset", String.join(",", preferredSummaries));
            return performAuthenticatedRequest(urlBuilder);

        }
        return "";
    }

    private List<Suggestion> abstatListSuggestions(String keyword, Position position) {
        keyword = extractEntityNameFromURI(keyword);

        try {
            String suggestions = this.abstatSuggestions(keyword, position);
            List<Suggestion> suggestionList = gson.fromJson(suggestions, Suggestions.class).getSuggestions();
            String finalKeyword = keyword;

            suggestionList.stream()
                    .map(suggestion -> SuggesterUtils.updateWithSearchedkeyword(suggestion, finalKeyword))
                    .map(suggestion -> SuggesterUtils.updateWithDatabasePosition(suggestion, preferredSummaries))
                    .map(SuggesterUtils::updateWithEntityName)
                    .map(SuggesterUtils::updateWithRatioIndex)
                    .forEach(suggesterUtils::updateWithDistanceVector);

            return suggestionList;
        } catch (IOException e) {
            e.printStackTrace();
            return emptyList();
        }
    }

    private Summaries summaries() throws IOException {
        String url = properties.getAbstat().getFullSummariesEndpoint();
        HttpUrl.Builder urlBuilder = requireNonNull(HttpUrl.parse(url)).newBuilder()
                .addQueryParameter("indexed", "true");
        String strDatasets = performAuthenticatedRequest(urlBuilder);
        if (test) System.out.println(SuggesterUtils.prettify(strDatasets));
        return gson.fromJson(strDatasets, Summaries.class);

    }

    private String performAuthenticatedRequest(HttpUrl.Builder urlBuilder) throws IOException {
        if (authToken == null) {
            authenticate();
        }

        try {
            return suggesterUtils.performGETRequest(urlBuilder, new Headers.Builder().add("Authorization",
                    "Bearer " + authToken));
        } catch (HTTPException e) {
            if (e.getStatusCode() == 401) {
                authenticate();
                return suggesterUtils.performGETRequest(urlBuilder, new Headers.Builder().add("Authorization",
                        "Bearer " + authToken));
            }
            throw e;
        }
    }

    private void authenticate() throws IOException {
        String url = properties.getAbstat().getAuthEndpoint();
        HttpUrl.Builder urlBuilder = requireNonNull(HttpUrl.parse(url)).newBuilder();
        String credentials = Credentials.basic(properties.getAbstat().getAuthUsername(),
                properties.getAbstat().getAuthPassword());
        RequestBody requestBody = new FormBody.Builder()
                .add("grant_type", "password")
                .add("username", properties.getAbstat().getUsername())
                .add("password", properties.getAbstat().getPassword())
                .build();
        String strAuth = suggesterUtils.performPOSTRequest(
                urlBuilder,
                new Headers.Builder().add("Authorization", credentials),
                requestBody);
        this.authToken =  gson.fromJson(strAuth, Authentication.class).getAccess_token();
    }

    private List<List<Suggestion>> listSuggestionsMultipleKeywords(@NonNull List<String> keywords, @NonNull Position position) {
        return keywords.stream()
                .map(k -> abstatListSuggestions(k, position))
                .collect(toList());
    }

    public enum Position {
        PRED("pred"), SUBJ("subj"), OBJ("obj");
        private final String value;

        Position(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }


}
