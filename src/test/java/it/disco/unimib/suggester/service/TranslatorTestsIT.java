package it.disco.unimib.suggester.service;

import com.google.gson.Gson;
import it.disco.unimib.suggester.model.LanguageType;
import it.disco.unimib.suggester.translator.domain.IDetectedLanguage;
import it.disco.unimib.suggester.translator.domain.ILookedupTerm;
import it.disco.unimib.suggester.translator.domain.ITranslation;
import it.disco.unimib.suggester.translator.implementation.microsoftTranslate.MSTranslator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Collections;


@RunWith(SpringRunner.class)
@SpringBootTest
public class TranslatorTestsIT {

    @Autowired
    public MSTranslator translator;

    @Before
    public void setup() {
        translator.setTest(false);
    }


    @Test
    public void detect() {

        String text = "Salve Mondo!";
        Gson gson = new Gson();
        String json = gson.toJson(Collections.singletonList(text));
        java.util.List<IDetectedLanguage> messageList = translator.detect(Collections.singletonList(text));
        Assert.assertEquals("it", messageList.get(0).getLanguage());
        Assert.assertEquals(0, messageList.get(0).getScore().compareTo(1.0));
    }

    @Test
    public void translate() throws IOException {

        String text = "Welcome to Microsoft Translator. Guess how many languages I speak!!";
        java.util.List<ITranslation> messageList = translator.translate(Collections.singletonList(text), LanguageType.IT);
        Assert.assertEquals("en", messageList.get(0).getLanguage());
        Assert.assertEquals("de", messageList.get(0).getTranslations().get(0).getDestLanguage());
        System.out.println(messageList.get(0).toString());
    }


    @Test
    public void lookup() {
        String text = "Piñas";
        java.util.List<ILookedupTerm> lookups = translator.lookup(Collections.singletonList(text), LanguageType.ES, LanguageType.EN);
        System.out.println(lookups.toString());
        Assert.assertFalse(lookups.isEmpty());
        Assert.assertEquals("pineapples", lookups.get(0).getTranslations().get(0).getTarget().toLowerCase());

    }


}
