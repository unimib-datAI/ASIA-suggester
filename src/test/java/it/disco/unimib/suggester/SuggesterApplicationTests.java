package it.disco.unimib.suggester;

import com.google.gson.Gson;
import com.sun.tools.javac.util.List;
import it.disco.unimib.suggester.model.Language;
import it.disco.unimib.suggester.translator.domain.IDetectedLanguage;
import it.disco.unimib.suggester.translator.domain.ILookedupTerm;
import it.disco.unimib.suggester.translator.domain.ITranslation;
import it.disco.unimib.suggester.translator.implementation.microsoftTranslate.MSTranslator;
import lombok.extern.java.Log;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;


@RunWith(SpringRunner.class)
@SpringBootTest
@Log
public class SuggesterApplicationTests {

    @Autowired
    public MSTranslator translator;

    @Test
    public void detect() throws IOException {

        String text = "Salve Mondo!";
        //log.info(text.getText());
        Gson gson = new Gson();
        String json = gson.toJson(List.of(text));
        //log.info(json);
        java.util.List<IDetectedLanguage> messageList = translator.detect(List.of(text));
        Assert.assertEquals("it", messageList.get(0).getLanguage());
        Assert.assertEquals(0, messageList.get(0).getScore().compareTo(1.0));
    }

    @Test
    public void translate() throws IOException {

        String text = "Welcome to Microsoft Translator. Guess how many languages I speak!!";
        java.util.List<ITranslation> messageList = translator.translate(List.of(text), Language.it);
        Assert.assertEquals("en", messageList.get(0).getLanguage());
        Assert.assertEquals("de", messageList.get(0).getTranslations().get(0).getDestLanguage());
        System.out.println(messageList.get(0).toString());
    }


    @Test
    public void lookup() throws IOException {
        String text = "Pineapples";
        java.util.List<ILookedupTerm> lookups = translator.lookup(List.of(text), Language.de);
        Assert.assertEquals(lookups.get(0).getSource().toLowerCase(), "pineapples");

    }


}
