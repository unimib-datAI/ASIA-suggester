package it.disco.unimib.suggester.service;

import it.disco.unimib.suggester.model.table.Column;
import it.disco.unimib.suggester.model.table.Header;
import it.disco.unimib.suggester.model.table.TableSchema;
import it.disco.unimib.suggester.model.translation.IDetectedLanguage;
import lombok.extern.java.Log;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


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
        String cleanHeader = OrchestratorUtils.headerPreProcessing(rawHeader);
        List<IDetectedLanguage> language = orchestrator.detectLanguage(cleanHeader);
        System.out.println(language.get(0).getLanguageEnum());

    }

    @Test
    //old
    public void lookup() {
        orchestrator.lookup("casa").forEach(stringDoublePair -> System.out.println(stringDoublePair.getTranslatedWord()));
    }


    @Test
    public void translate() {
    }

    @Test
    public void generatephrasesCombinatorially() {
        List<String> listWithDuplicates = Arrays.asList("AA", "BB", "AA", "BB");
        List<String> listWithoutDuplicates =
                listWithDuplicates.stream()
                        .distinct()
                        .collect(Collectors.toList());

        listWithoutDuplicates.forEach(l -> System.out.println(l));


        Header header = new Header();
        header.setOriginalWord("CasaGiardino");
        Column column = new Column();
        column.setHeader(header);
        TableSchema schema = new TableSchema();
        schema.addColumn(column);
        TableSchema tableSchema = orchestrator.translateTableSchema(schema);

        ReflectionTestUtils.invokeMethod(orchestrator,
                "generatePhrasesCombinatorially",
                tableSchema.getColumnList().get(0));
    }


}