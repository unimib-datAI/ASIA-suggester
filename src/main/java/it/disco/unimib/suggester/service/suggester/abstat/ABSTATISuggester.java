package it.disco.unimib.suggester.service.suggester.abstat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import it.disco.unimib.suggester.configuration.ConfigProperties;
import it.disco.unimib.suggester.configuration.SuggesterConfiguration;
import it.disco.unimib.suggester.model.suggestion.Suggestion;
import it.disco.unimib.suggester.service.suggester.ISuggester;
import it.disco.unimib.suggester.service.suggester.abstat.domain.Datasets;
import it.disco.unimib.suggester.service.suggester.abstat.domain.Suggestions;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.CollectionUtils.isEmpty;

@Service
public class ABSTATISuggester implements ISuggester {

    private static Pattern notAlphanumeric = Pattern.compile("[^a-z0-9]");
    private static Pattern spaces = Pattern.compile("\\s+");
    private final Gson gson = new Gson();
    private final OkHttpClient client;
    @Getter
    @Setter
    private boolean test = false;
    @Getter
    private List<String> preferredSummaries;
    private ConfigProperties properties;
    private SuggesterConfiguration.DistanceCalculator distanceCalculator;

    public ABSTATISuggester(ConfigProperties properties, OkHttpClient client, SuggesterConfiguration.DistanceCalculator distanceCalculator) {
        this.properties = properties;
        this.client = client;
        this.distanceCalculator = distanceCalculator;
    }

    private static String stringPreprocessing(String str) {
        Matcher m = notAlphanumeric.matcher(str);
        str = m.replaceAll(" ");
        m = spaces.matcher(str);
        str = m.replaceAll(" ");
        //   String[] words = str.split(" ");
        //    for (int i = 1; i < words.length; i++)
        //        words[i] = words[i].substring(0, 1).toUpperCase() + words[i].substring(1);
        return str;
    }

    private static String filterURI(String URI) {
        if (URI.startsWith("http")) {
            int slashIdx = URI.lastIndexOf('/') + 1;
            int hashIdx = URI.lastIndexOf('#');
            int colonIdx = URI.lastIndexOf(':');
            URI = URI.substring(Math.max(slashIdx, Math.max(hashIdx, colonIdx)));
        }
        return URI;
    }

    // This function prettifies the json response.
    private static String prettify(String json_text) {
        JsonParser parser = new JsonParser();
        JsonElement json = parser.parse(json_text);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(json);
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
        String url = properties.getSummarizer().getFullSuggestEndpoint();

        HttpUrl.Builder urlBuilder = requireNonNull(HttpUrl.parse(url)).newBuilder();
        if (!Objects.equals(keyword, "")) {
            urlBuilder.addQueryParameter("qString", keyword);
            urlBuilder.addQueryParameter("qPosition", position.getValue());
            urlBuilder.addQueryParameter("rows", "15");
            urlBuilder.addQueryParameter("start", "0");

            if (!isEmpty(preferredSummaries)) // check nullity and emptiness
                urlBuilder.addQueryParameter("dataset", String.join(",", preferredSummaries));
            return getString(urlBuilder);

        }
        return "";
    }

    private List<Suggestion> abstatListSuggestions(String keyword, Position position) {
        keyword = filterURI(keyword);

        try {
            String suggestions = this.abstatSuggestions(keyword, position);
            List<Suggestion> suggestionList = gson.fromJson(suggestions, Suggestions.class).getSuggestions();
            String finalKeyword = keyword;
            suggestionList.forEach(suggestion -> suggestion.setSearchedKeyword(finalKeyword));
            suggestionList.forEach(suggestion -> suggestion.setPositionDataset(!isEmpty(preferredSummaries) ?
                    preferredSummaries.indexOf(suggestion.getDataset()) : -1));
            suggestionList.forEach(suggestion -> suggestion.setEntityName(filterURI(suggestion.getSuggestion())));
            //Math.max(finalKeyword.length(),suggestion.getEntityName().length());
            suggestionList.forEach(suggestion -> {
                int max = Math.max(finalKeyword.length(), suggestion.getEntityName().length());
                int min = Math.min(finalKeyword.length(), suggestion.getEntityName().length());
                suggestion.setRatioIndex((double) min / max);
            });

            suggestionList.forEach(suggestion -> suggestion.setDistances(
                    distanceCalculator.apply(finalKeyword.toLowerCase(), suggestion.getEntityName().toLowerCase())));

            return suggestionList;
        } catch (IOException e) {
            e.printStackTrace();
            return emptyList();
        }
    }

    private Datasets summaries() throws IOException {
        String url = properties.getSummarizer().getFullDatasetsEndpoint();
        HttpUrl.Builder urlBuilder = requireNonNull(HttpUrl.parse(url)).newBuilder();
        String strDatasets = getString(urlBuilder);

        return gson.fromJson(strDatasets, Datasets.class);

    }

    private String getString(HttpUrl.Builder urlBuilder) throws IOException {
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder().url(url).get().build();
        Response response = client.newCall(request).execute();
        assert response.body() != null;
        String bodyString = response.body().string();
        if (test) System.out.println(prettify(bodyString));
        return bodyString;
    }

    private List<List<Suggestion>> listSuggestionsMultipleKeywords(@NonNull List<String> keywords, @NonNull Position position) {
        return keywords.stream()
                .map(k -> abstatListSuggestions(k, position))
                .collect(toList());
    }

    enum Position {
        PRED("pred"), SUBJ("subj"), OBJ("obj");
        private String value;

        Position(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }


}
