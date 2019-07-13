package it.disco.unimib.suggester.microsoftTranslate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import it.disco.unimib.suggester.ITranslator;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MSTranslator implements ITranslator {

    @Value("${key}")
    String subscriptionKey;


    @Autowired
    private OkHttpClient client;

    // This function prettifies the json response.
    public static String prettify(String json_text) {
        JsonParser parser = new JsonParser();
        JsonElement json = parser.parse(json_text);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(json);
    }

    private static List<TextToTranslate> toTranslateList(List<String> textList) {
        return textList.stream().map(TextToTranslate::new).collect(Collectors.toList());

    }

    @Override
    public String detect(List<String> textList) throws IOException {
        String url = "https://api.cognitive.microsofttranslator.com/detect?api-version=3.0";
        return post(toTranslateList(textList), url);
    }

    @Override
    public String translate(List<String> textList) throws IOException {
        String url = "https://api.cognitive.microsofttranslator.com/translate?api-version=3.0&to=de,it";
        return post(toTranslateList(textList), url);
    }

    @Override
    public String lookup(List<String> textList) throws IOException {
        String url = "https://api.cognitive.microsofttranslator.com/dictionary/lookup?api-version=3.0&from=en&to=es";
        return post(toTranslateList(textList), url);
    }

    // This function performs a POST request.
    public String post(List<TextToTranslate> listTextToTranslate, String urlTo) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");

        Gson gson = new Gson();
        String json = gson.toJson(listTextToTranslate);

        RequestBody body = RequestBody.create(mediaType, json);

        Request request = new Request.Builder()
                .url(urlTo).post(body)
                .addHeader("Ocp-Apim-Subscription-Key", subscriptionKey)
                .addHeader("Content-type", "application/json").build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }


}