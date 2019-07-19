package it.disco.unimib.suggester.controller;


import it.disco.unimib.suggester.model.Column;
import it.disco.unimib.suggester.model.Header;
import it.disco.unimib.suggester.model.TableSchema;
import it.disco.unimib.suggester.service.Orchestrator;
import it.disco.unimib.suggester.translator.ITranslator;
import it.disco.unimib.suggester.translator.domain.IDetectedLanguage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.Collections.singletonList;

@RestController
@RequestMapping("/api/suggester")
public class SuggestController {


    private ITranslator translator;
    private Orchestrator orchestrator;


    @Autowired
    public SuggestController(ITranslator translator, Orchestrator orchestrator) {
        this.translator = translator;
        this.orchestrator = orchestrator;
    }

    @PutMapping("/translate")
    public TableSchema putTranslateSchema(@RequestBody TableSchema schema) {
        return orchestrator.translateSchema(schema);

    }


    @PutMapping("translateColumn")
    public Column putTranslateColumn(@RequestBody Column column) {
        System.out.println(column.toString());


        Header header = column.getHeader();
        List<IDetectedLanguage> detectedLanguages = translator.detect(singletonList(header.getOriginalWord()));
        header.setLanguage(detectedLanguages.get(0).getLanguageEnum());

        return column;
    }


}
