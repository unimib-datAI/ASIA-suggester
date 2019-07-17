package it.disco.unimib.suggester.service;

import it.disco.unimib.suggester.translator.domain.IDetectedLanguage;
import lombok.extern.java.Log;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest
@Log
public class OrchestratorTest {

    @Autowired
    private Orchestrator orchestrator;

    @Test
    public void
    detectLanguage() {
        String rawHeader = "yo-dude: like, ... []{}this?is_a string";
        String cleanHeader = Orchestrator.headerPreprocessing(rawHeader);
        try {

            List<IDetectedLanguage> language = orchestrator.detectLanguage(cleanHeader);
            System.out.println(language.get(0).getLanguageEnum());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void lookup() throws IOException {
        orchestrator.lookup("casa").forEach(stringDoublePair -> System.out.println(stringDoublePair.fst));
    }


    @Test
    public void translate() {
    }
}