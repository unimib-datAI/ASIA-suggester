package it.disco.unimib.suggester.service.suggester.lov;

import com.google.gson.Gson;
import it.disco.unimib.suggester.configuration.ConfigProperties;
import it.disco.unimib.suggester.configuration.SuggesterConfiguration;
import it.disco.unimib.suggester.model.suggestion.Suggestion;
import it.disco.unimib.suggester.service.suggester.ISuggester;
import it.disco.unimib.suggester.service.suggester.SuggesterUtils;
import it.disco.unimib.suggester.service.suggester.lov.domain.LOVSearchResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import okhttp3.HttpUrl;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static it.disco.unimib.suggester.service.suggester.SuggesterUtils.*;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.CollectionUtils.isEmpty;

@Service
public class LOVSuggester implements ISuggester {
    private final Gson gson = new Gson();
    private final ConfigProperties properties;
    private final SuggesterConfiguration.DistanceCalculator distanceCalculator;
    private final SuggesterUtils suggesterUtils;
    private boolean test;
    private List<String> preferredSummaries;


    public LOVSuggester(ConfigProperties properties, SuggesterConfiguration.DistanceCalculator distanceCalculator, SuggesterUtils suggesterUtils) {
        this.properties = properties;
        this.distanceCalculator = distanceCalculator;
        this.suggesterUtils = suggesterUtils;
    }

    private static List<String> fileStreamUsingBufferedReader(File file) {
        try {

            BufferedReader br = Files.newBufferedReader(Paths.get(file.getPath()));
            Stream<String> lines = br.lines();
            List<String> collect = lines.map(l -> l.split("\\s")[0]).collect(toList());
            lines.close();
            return collect;
        } catch (IOException io) {
            io.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Suggestion> propertySuggestions(@NonNull String keyword) {
        return getListOfSuggestions(keyword, TypeLOV.PROPERTY);
    }

    @Override
    public List<Suggestion> typeSuggestions(@NonNull String keyword) {
        return getListOfSuggestions(keyword, TypeLOV.CLASS);
    }

    @Override
    public List<Suggestion> objectSuggestions(@NonNull String keyword) {
        return getListOfSuggestions(keyword, TypeLOV.CLASS);
    }

    @Override
    public List<List<Suggestion>> objectSuggestionsMultipleKeywords(@NonNull List<String> keywords) {
        return keywords.stream().map(this::objectSuggestions).collect(toList());
    }

    @Override
    public List<List<Suggestion>> propertySuggestionsMultipleKeywords(@NonNull List<String> keywords) {
        return keywords.stream().map(this::propertySuggestions).collect(toList());
    }

    @Override
    public List<List<Suggestion>> typeSuggestionsMultipleKeywords(@NonNull List<String> keywords) {
        return keywords.stream().map(this::typeSuggestions).collect(toList());
    }

    @Override
    public boolean isTest() {
        return this.test;
    }

    @Override
    public void setTest(boolean test) {
        this.test = test;
    }

    @Override
    public void setPreferredSummaries(List<String> preferredSummaries) {

        this.preferredSummaries = preferredSummaries;
    }

    @Override
    public List<String> getSummaries() {
/*        String url = "https://lov.linkeddata.es/dataset/lov/api/v2/vocabulary/list"; // TODO: 2019-08-04 fix this
        HttpUrl.Builder urlBuilder = requireNonNull(HttpUrl.parse(url)).newBuilder();
        try {
            String datasets = suggesterUtils.performGETRequest(urlBuilder);
            if (test) System.out.println(datasets);
            Type listType = new TypeToken<ArrayList<Summary>>() {
            }.getType();
            List<Summary> summaryList = gson.fromJson(datasets, listType);
            if (test) System.out.println(summaryList);
            List<String> summaryCollection = summaryList.stream().map(Summary::getPrefix).collect(toList());
            if (test) System.out.println(summaryCollection);
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;*/
// this does not work. I could not find the way to retrieve the actual vocabulary names

        String fName = "vocabulary-list.txt";

        try {
            File file = ResourceUtils.getFile("classpath:" + fName);
            return new ArrayList<>(requireNonNull(fileStreamUsingBufferedReader(file)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }


    }

    private String getLOVSuggestions(@NonNull String keyword, @NonNull TypeLOV typeLOV, String summary) {

        String url = properties.getLov().getFullSuggestEndpoint();
        //"https://lov.linkeddata.es/dataset/lov/api/v2/term/search";

        HttpUrl.Builder urlBuilder = requireNonNull(HttpUrl.parse(url)).newBuilder();
        if (!StringUtils.isEmpty(keyword)) {
            urlBuilder.addQueryParameter("q", keyword);
            urlBuilder.addQueryParameter("type", typeLOV.getValue());
            if (!StringUtils.isEmpty(summary)) // check nullity and emptiness
                urlBuilder.addQueryParameter("vocab", summary);
            try {
                return suggesterUtils.performGETRequest(urlBuilder);
            } catch (IOException e) {
                e.printStackTrace();
                return "";
            }
        }
        return "";

    }

    private List<Suggestion> getListOfSuggestions(@NonNull String keyword,
                                                  @NonNull TypeLOV typeLOV) {

        List<Pair<String, String>> results;
        if (!isEmpty(preferredSummaries)) {

            results = preferredSummaries
                    .stream()
                    .map(summary -> Pair.of(getLOVSuggestions(keyword, typeLOV, summary), summary))
                    .collect(toList());
        } else {
            String res = getLOVSuggestions(keyword, typeLOV, null);
            results = Collections.singletonList(Pair.of(res, ""));

        }
        List<LOVSearchResult> mappedResult = results
                .stream()
                .map(r -> Pair.of(gson.fromJson(r.getFirst(), LOVSearchResult.class), r.getSecond()))
                .map(p -> p.getFirst().setDataset(p.getSecond()))
                .collect(toList());


        return mappedResult.stream()
                .map(LOVSearchResult::getResults)
                .flatMap(Collection::stream)
                .map(this::mapFromResultToSuggestion)
                .map(suggestion -> updateWithSearchedkeyword(suggestion, keyword))
                .map(suggestion -> updateWithDatabasePosition(suggestion, preferredSummaries))
                .map(SuggesterUtils::updateWithEntityName)
                .map(SuggesterUtils::updateWithRatioIndex)
                .map(suggesterUtils::updateWithDistanceVector)
                .collect(toList());
    }


    private Suggestion mapFromResultToSuggestion(LOVSearchResult.Result result) {
        Suggestion suggestion = new Suggestion();
        suggestion.setPrefix(result.getVocabularyPrefix()[0]);
        suggestion.setOccurrence(Long.valueOf(result.getOccurrencesInDatasets()[0]));
        suggestion.setSuggestion(result.getUri()[0]);
        suggestion.setNamespace(extractNamespaceFromURI(suggestion.getSuggestion()));
        suggestion.setDataset(result.getDataset());
        suggestion.setSuggesterScore(result.getScore());

        return suggestion;
    }


    enum TypeLOV {
        CLASS("class"),
        PROPERTY("property"),
        DATATYPE("datatype"),
        INSTANCE("instance");

        private final String value;

        TypeLOV(String value) {
            this.value = value;
        }

        String getValue() {
            return value;
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class Summary {
        private String prefix;
    }

}
