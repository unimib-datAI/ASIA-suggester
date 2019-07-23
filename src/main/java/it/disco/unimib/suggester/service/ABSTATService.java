package it.disco.unimib.suggester.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import it.disco.unimib.suggester.ConfigProperties;
import it.disco.unimib.suggester.model.Suggestions;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;

@Service
public class ABSTATService {


    private final OkHttpClient client;
    private ConfigProperties properties;
    private List<String> preferredSummaries = new ArrayList<>();
    private static Pattern notAlphanumeric = Pattern.compile("[^a-z0-9]");
    private static Pattern spaces = Pattern.compile("\\s+");

    public ABSTATService(ConfigProperties properties, OkHttpClient client) {
        this.properties = properties;
        this.client = client;
    }

    public static String stringPreprocessing(String str) {
        Matcher m = notAlphanumeric.matcher(str);
        str = m.replaceAll(" ");
        m = spaces.matcher(str);
        str = m.replaceAll(" ");
        String[] words = str.split(" ");
        for (int i = 1; i < words.length; i++)
            words[i] = words[i].substring(0, 1).toUpperCase() + words[i].substring(1);
        return str;
    }

    public static String filterURI(String URI) {
        if (URI.startsWith("http")) {
            int slashIdx = URI.lastIndexOf('/');
            int hashIdx = URI.lastIndexOf('#');
            int colonIdx = URI.lastIndexOf(':');
            // Che brutta cosa!
            URI = URI.substring(Math.max(slashIdx, Math.max(hashIdx, colonIdx)));
        }
        return URI;
    }

    //Direttamente da https://stackoverflow.com/a/21657510
    private static byte[] getParamsString(Map<String, Object> params)
            throws UnsupportedEncodingException {
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (postData.length() != 0)
                postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        return postData.toString().getBytes(StandardCharsets.UTF_8);
    }

    // This function prettifies the json response.
    public static String prettify(String json_text) {
        JsonParser parser = new JsonParser();
        JsonElement json = parser.parse(json_text);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(json);
    }

    //Dovrebbe tornare una lista, ma non riuscendo a testare l'endpoint
    //ritorno una stringa
    private String abstatSuggestions(String keyword, String position) throws IOException {
        String url = properties.getSummarizer().getMainEndpoint() + "/api/v1/SolrSuggestions";


        MediaType mediaType = MediaType.parse("application/json");


        HttpUrl.Builder urlBuilder = requireNonNull(HttpUrl.parse(url)).newBuilder();
        if (!Objects.equals(keyword, "") && (!Objects.equals(position, ""))) {
            urlBuilder.addQueryParameter("qString", keyword);
            urlBuilder.addQueryParameter("qPosition", position);
            urlBuilder.addQueryParameter("rows", "15");
            urlBuilder.addQueryParameter("start", "0");
            String urlSuggestion = urlBuilder.build().toString();

            Request request = new Request.Builder().url(urlSuggestion).get().build();
            Response response = client.newCall(request).execute();
            assert response.body() != null;
            String bodyString = response.body().string();
            System.out.println(prettify(bodyString));
            return bodyString;


        }
        return "";
    }

    //Dovrebbe tornare una lista, ma non riuscendo a testare l'endpoint
    //ritorno una stringa
    public Suggestions propertySuggestions(String keyword, boolean filter) throws IOException {
        keyword = filterURI(keyword);
        if (filter) keyword = stringPreprocessing(keyword);

        String suggestions = this.abstatSuggestions(keyword, "pred");
        //Type listType = new TypeToken<ArrayList<Suggestion>>(){}.getType();
        Gson gson = new Gson();
        return gson.fromJson(suggestions, Suggestions.class);

    }

}
