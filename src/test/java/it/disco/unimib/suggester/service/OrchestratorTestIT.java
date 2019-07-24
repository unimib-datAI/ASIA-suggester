package it.disco.unimib.suggester.service;

import it.disco.unimib.suggester.model.translation.IDetectedLanguage;
import lombok.extern.java.Log;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest
@Log
public class OrchestratorTestIT {

    @Autowired
    private Orchestrator orchestrator;

    @Test
    public void
    detectLanguage() {
        String rawHeader = "yo-dude: like, ... []{}this?is_a string";
        String cleanHeader = Orchestrator.headerPreprocessing(rawHeader);
        List<IDetectedLanguage> language = orchestrator.detectLanguage(cleanHeader);
        System.out.println(language.get(0).getLanguageEnum());

    }

    @Test
    public void lookup() {
        orchestrator.lookup("casa").forEach(stringDoublePair -> System.out.println(stringDoublePair.getFirst()));
    }


    @Test
    public void translate() {
    }
}