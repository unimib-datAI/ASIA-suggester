package it.disco.unimib.suggester.controller;


import it.disco.unimib.suggester.model.table.Column;
import it.disco.unimib.suggester.model.table.Header;
import it.disco.unimib.suggester.model.table.TableSchema;
import it.disco.unimib.suggester.model.translation.IDetectedLanguage;
import it.disco.unimib.suggester.service.Orchestrator;
import it.disco.unimib.suggester.service.translator.ITranslator;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.Collections.singletonList;

@RestController
@RequestMapping("/api/suggester")
public class SuggestController {

    @Getter
    @Setter
    private boolean test = false;

    private ITranslator translator;
    private Orchestrator orchestrator;


    public SuggestController(ITranslator translator, Orchestrator orchestrator) {
        this.translator = translator;
        this.orchestrator = orchestrator;
    }

    @PutMapping("/translate")
    public TableSchema putTranslateSchema(@RequestBody TableSchema schema) {
        return orchestrator.translateSchema(schema);

    }


    @PutMapping("translateColumn")
    public Column putTranslateColumn(@NotNull @RequestBody Column column) {
        if (test) System.out.println(column.toString());
        Header header = column.getHeader();
        List<IDetectedLanguage> detectedLanguages = translator.detect(singletonList(header.getOriginalWord()));
        header.setLanguage(detectedLanguages.get(0).getLanguageEnum());
        return column;
    }


}
