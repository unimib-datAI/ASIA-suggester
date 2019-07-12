package it.disco.unimib.suggester.microsoftTranslate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;


@Component
public class Detect {

    @Value("${key}")
    String subscriptionKey;
    String url = "https://api.cognitive.microsofttranslator.com/detect?api-version=3.0";
    @Autowired
    private OkHttpClient client;

    // This function prettifies the json response.
    public static String prettify(String json_text) {
        JsonParser parser = new JsonParser();
        JsonElement json = parser.parse(json_text);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(json);
    }

    // This function performs a POST request.
    public String Post(List<TextToTranslate> listTextToTranslate) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");

        Gson gson = new Gson();
        String json = gson.toJson(listTextToTranslate);

        /*RequestBody body = RequestBody.create(mediaType,
                "[{\n\t\"Text\": \"Salve mondo!\"\n}]");*/
        RequestBody body = RequestBody.create(mediaType, json);
        Request request = new Request.Builder()
                .url(url).post(body)
                .addHeader("Ocp-Apim-Subscription-Key", subscriptionKey)
                .addHeader("Content-type", "application/json").build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }


}


