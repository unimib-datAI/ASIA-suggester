package it.disco.unimib.suggester.service.translator.mstranslate;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.disco.unimib.suggester.configuration.ConfigProperties;
import it.disco.unimib.suggester.model.translation.IDetectedLanguage;
import it.disco.unimib.suggester.model.translation.ILookedupTerm;
import it.disco.unimib.suggester.model.translation.ITranslation;
import it.disco.unimib.suggester.model.translation.LanguageType;
import it.disco.unimib.suggester.service.translator.ITranslator;
import it.disco.unimib.suggester.service.translator.mstranslate.domain.DetectMessage;
import it.disco.unimib.suggester.service.translator.mstranslate.domain.LookupMessage;
import it.disco.unimib.suggester.service.translator.mstranslate.domain.TextToTranslate;
import it.disco.unimib.suggester.service.translator.mstranslate.domain.TranslateMessage;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@SuppressWarnings("SpellCheckingInspection")
@Service
public class MSTranslator implements ITranslator {

    private final OkHttpClient client;
    private final ConfigProperties properties;
    private boolean test = false;

    private final Gson gson = new Gson();

    public MSTranslator(OkHttpClient client, ConfigProperties properties) {

        this.client = client;
        this.properties = properties;
    }


    private static List<TextToTranslate> toTranslateList(List<String> textList) {
        return textList.stream().map(TextToTranslate::new).collect(toList());

    }

    @Override
    public void setTest(boolean test) {
        this.test = test;
    }

    @Override
    public List<IDetectedLanguage> detect(List<String> textList) {
        String url = properties.getTranslator().getFullDetectEndpoint();
        String language = null;
        try {
            language = post(toTranslateList(textList), url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Type listType = new TypeToken<ArrayList<DetectMessage>>() {
        }.getType();
        List<IDetectedLanguage> ls = null;
        try { // there might be conversion errors for instance when the credentials are not recognized by the translator

            ls = gson.fromJson(language, listType);
        } catch (Exception e) {
            e.printStackTrace();
            ls = textList.stream()
                    .map(x -> new DetectMessage("en", 1.0, Boolean.TRUE, Boolean.FALSE, null))
                    .collect(Collectors.toList());
        }

        return ls;
    }

    @Override
    public List<ITranslation> translate(List<String> textList, LanguageType destLang) throws IOException {
        if (test) System.out.println(destLang.toString());
        String url = properties.getTranslator().getFullTranslateEndpoint() + "&to=de,it";
        return gson.fromJson(
                post(toTranslateList(textList), url),
                new TypeToken<ArrayList<TranslateMessage>>() {
                }.getType());

    }

    @Override
    public List<ILookedupTerm> lookup(List<String> textList, LanguageType sourceLang, LanguageType destLang) {
        String fromTo = String.format("from=%s&to=%s", sourceLang.toString(), destLang.toString());
        String url = properties.getTranslator().getFullLookupEndpoint() + "&" + fromTo;

        try {
            return gson.fromJson(
                    post(toTranslateList(textList), url),
                    new TypeToken<ArrayList<LookupMessage>>() {
                    }.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

    // This function performs a POST request.
    private String post(List<TextToTranslate> listTextToTranslate, String urlTo) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");

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
