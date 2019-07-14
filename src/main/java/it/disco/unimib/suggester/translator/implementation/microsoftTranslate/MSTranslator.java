package it.disco.unimib.suggester.translator.implementation.microsoftTranslate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import it.disco.unimib.suggester.model.Language;
import it.disco.unimib.suggester.translator.ITranslator;
import it.disco.unimib.suggester.translator.domain.IDetectedLanguage;
import it.disco.unimib.suggester.translator.domain.ILookedupTerm;
import it.disco.unimib.suggester.translator.domain.ITranslation;
import it.disco.unimib.suggester.translator.implementation.microsoftTranslate.domain.DetectMessage;
import it.disco.unimib.suggester.translator.implementation.microsoftTranslate.domain.LookupMessage;
import it.disco.unimib.suggester.translator.implementation.microsoftTranslate.domain.TranslateMessage;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
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
    public List<IDetectedLanguage> detect(List<String> textList) throws IOException {
        String url = "https://api.cognitive.microsofttranslator.com/detect?api-version=3.0";
        String language = post(toTranslateList(textList), url);
        Type listType = new TypeToken<ArrayList<DetectMessage>>() {
        }.getType();
        Gson gson = new Gson();
        return gson.fromJson(language, listType);
    }

    @Override
    public List<ITranslation> translate(List<String> textList, Language destLang) throws IOException {
        System.out.println(destLang.toString());
        String url = "https://api.cognitive.microsofttranslator.com/translate?api-version=3.0&to=de,it";
        return new Gson().fromJson(
                post(toTranslateList(textList), url),
                new TypeToken<ArrayList<TranslateMessage>>() {
                }.getType());

    }

    @Override
    public List<ILookedupTerm> lookup(List<String> textList, Language destLang) throws IOException {
        String url = "https://api.cognitive.microsofttranslator.com/dictionary/lookup?api-version=3.0&from=en&to=es";
        return new Gson().fromJson(
                post(toTranslateList(textList), url),
                new TypeToken<ArrayList<LookupMessage>>() {
                }.getType());

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
