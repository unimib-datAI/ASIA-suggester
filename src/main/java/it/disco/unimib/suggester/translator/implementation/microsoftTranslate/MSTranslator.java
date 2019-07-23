package it.disco.unimib.suggester.translator.implementation.microsoftTranslate;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.disco.unimib.suggester.ConfigProperties;
import it.disco.unimib.suggester.model.LanguageType;
import it.disco.unimib.suggester.translator.ITranslator;
import it.disco.unimib.suggester.translator.domain.IDetectedLanguage;
import it.disco.unimib.suggester.translator.domain.ILookedupTerm;
import it.disco.unimib.suggester.translator.domain.ITranslation;
import it.disco.unimib.suggester.translator.implementation.microsoftTranslate.domain.DetectMessage;
import it.disco.unimib.suggester.translator.implementation.microsoftTranslate.domain.LookupMessage;
import it.disco.unimib.suggester.translator.implementation.microsoftTranslate.domain.TranslateMessage;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class MSTranslator implements ITranslator {

    private ConfigProperties properties;

    private final OkHttpClient client;

    public MSTranslator(OkHttpClient client, ConfigProperties properties) {

        this.client = client;
        this.properties = properties;
    }



    private static List<TextToTranslate> toTranslateList(List<String> textList) {
        return textList.stream().map(TextToTranslate::new).collect(Collectors.toList());

    }

    @Override
    public List<IDetectedLanguage> detect(List<String> textList) {
        String url = properties.getTranslator().getMainEndpoint() + "/" + properties.getTranslator().getDetectEndpoint();
        String language = null;
        try {
            language = post(toTranslateList(textList), url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Type listType = new TypeToken<ArrayList<DetectMessage>>() {
        }.getType();
        Gson gson = new Gson();
        return gson.fromJson(language, listType);
    }

    @Override
    public List<ITranslation> translate(List<String> textList, LanguageType destLang) throws IOException {
        System.out.println(destLang.toString());
        String url = properties.getTranslator().getMainEndpoint() + "/" + properties.getTranslator().getTranslateEndpoint() + "&to=de,it";
        return new Gson().fromJson(
                post(toTranslateList(textList), url),
                new TypeToken<ArrayList<TranslateMessage>>() {
                }.getType());

    }

    @Override
    public Optional<List<ILookedupTerm>> lookup(List<String> textList, LanguageType sourceLang, LanguageType destLang) {
        String fromTo = String.format("from=%s&to=%s", sourceLang.toString(), destLang.toString());
        String url = properties.getTranslator().getMainEndpoint() + "/" + properties.getTranslator().getLookupEndpoint() + "&" + fromTo;

        try {
            return Optional.of(new Gson().fromJson(
                    post(toTranslateList(textList), url),
                    new TypeToken<ArrayList<LookupMessage>>() {
                    }.getType()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    // This function performs a POST request.
    private String post(List<TextToTranslate> listTextToTranslate, String urlTo) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");

        Gson gson = new Gson();
        String json = gson.toJson(listTextToTranslate);

        RequestBody body = RequestBody.create(mediaType, json);

        Request request = new Request.Builder()
                .url(urlTo).post(body)
                .addHeader("Ocp-Apim-Subscription-Key", properties.getTranslator().getSubscriptionKey())
                .addHeader("Content-type", "application/json").build();
        Response response = client.newCall(request).execute();
        assert response.body() != null;
        return response.body().string();
    }


}
