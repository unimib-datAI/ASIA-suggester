package it.disco.unimib.suggester;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.tools.javac.util.List;
import it.disco.unimib.suggester.microsoftTranslate.Detect;
import it.disco.unimib.suggester.microsoftTranslate.messages.DetectMessage;
import it.disco.unimib.suggester.microsoftTranslate.TextToTranslate;
import lombok.extern.java.Log;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;


@RunWith(SpringRunner.class)
@SpringBootTest
@Log
public class SuggesterApplicationTests {

    @Autowired
    public Detect detect;


    @Test
    public void textToTranslate() throws IOException {
        TextToTranslate text = new TextToTranslate("Salve Mondo!");
        //log.info(text.getText());
        Gson gson = new Gson();
        String json = gson.toJson(List.of(text));
        //log.info(json);
        Assert.assertEquals(json, "[{\"text\":\"Salve Mondo!\"}]");
        String language = detect.Post(List.of(text));

        Type listType = new TypeToken<ArrayList<DetectMessage>>() {
        }.getType();
        java.util.List<DetectMessage> messageList = gson.fromJson(language, listType);
        Assert.assertEquals("it", messageList.get(0).getLanguage());
        Assert.assertEquals(0, messageList.get(0).getScore().compareTo(1.0));

    }

}
