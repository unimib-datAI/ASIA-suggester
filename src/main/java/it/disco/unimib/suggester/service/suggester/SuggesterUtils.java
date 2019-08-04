package it.disco.unimib.suggester.service.suggester;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import it.disco.unimib.suggester.configuration.SuggesterConfiguration;
import it.disco.unimib.suggester.model.suggestion.Suggestion;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import static org.springframework.util.CollectionUtils.isEmpty;


@Component
public class SuggesterUtils {

    public static final Pattern notAlphanumeric = Pattern.compile("[^a-z0-9]");
    public static final Pattern spaces = Pattern.compile("\\s+");
    private final OkHttpClient client;
    private final SuggesterConfiguration.DistanceCalculator distanceCalculator;

    public SuggesterUtils(OkHttpClient client, SuggesterConfiguration.DistanceCalculator distanceCalculator) {
        this.client = client;
        this.distanceCalculator = distanceCalculator;
    }

    public static String extractEntityNameFromURI(String URI) {
        if (URI.startsWith("http")) {
            int slashIdx = URI.lastIndexOf('/') + 1;
            int hashIdx = URI.lastIndexOf('#') + 1;
            int colonIdx = URI.lastIndexOf(':');
            URI = URI.substring(Math.max(slashIdx, Math.max(hashIdx, colonIdx)));
        }
        return URI;
    }

    public static String extractNamespaceFromURI(String URI) {
        if (URI.startsWith("http")) {
            int slashIdx = URI.lastIndexOf('/') + 1;
            int hashIdx = URI.lastIndexOf('#');
            int colonIdx = URI.lastIndexOf(':');
            URI = URI.substring(0, slashIdx);
        }
        return URI;
    }

    // This function prettifies the json response.
    public static String prettify(String json_text) {
        JsonParser parser = new JsonParser();
        JsonElement json = parser.parse(json_text);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(json);
    }

    public static Suggestion updateWithRatioIndex(Suggestion suggestion) {

        String finalKeyword = suggestion.getSearchedKeyword();
        int max = Math.max(finalKeyword.length(), suggestion.getEntityName().length());
        int min = Math.min(finalKeyword.length(), suggestion.getEntityName().length());
        suggestion.setRatioIndex((double) min / max);
        return suggestion;
    }

    public static Suggestion updateWithEntityName(Suggestion suggestion) {
        suggestion.setEntityName(extractEntityNameFromURI(suggestion.getSuggestion()));
        return suggestion;
    }

    public static Suggestion updateWithDatabasePosition(Suggestion suggestion, List<String> preferredSummaries) {
        suggestion.setPositionDataset(!isEmpty(preferredSummaries) ?
                preferredSummaries.indexOf(suggestion.getDataset()) : -1);
        return suggestion;
    }

    public static Suggestion updateWithSearchedkeyword(Suggestion suggestion, String finalKeyword) {
        suggestion.setSearchedKeyword(finalKeyword);
        return suggestion;
    }

    public Suggestion updateWithDistanceVector(Suggestion suggestion) {
        String finalKeyword = suggestion.getSearchedKeyword();
        suggestion.setDistances(distanceCalculator.apply(finalKeyword.toLowerCase(),
                suggestion.getEntityName().toLowerCase()));
        return suggestion;
    }

    public String performGETRequest(HttpUrl.Builder urlBuilder) throws IOException {
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder().url(url).get().build();
        Response response = client.newCall(request).execute();
        assert response.body() != null;
        return response.body().string();
    }
}
