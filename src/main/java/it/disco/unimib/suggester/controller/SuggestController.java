package it.disco.unimib.suggester.controller;


import it.disco.unimib.suggester.model.table.Column;
import it.disco.unimib.suggester.model.table.TableSchema;
import it.disco.unimib.suggester.service.Orchestrator;
import it.disco.unimib.suggester.service.translator.ITranslator;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.joining;

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

    @PutMapping(value = "/translate", consumes = "application/json", produces = "application/json")
    public TableSchema putTranslateSchema(@Valid @RequestBody TableSchema schema, @RequestParam(name = "preferredSummaries[]", required = false) String[] preferredSummaries) {
        if (test) {
            String summaries = Arrays.asList(preferredSummaries).stream().collect(joining(","));
            System.out.println(summaries);
        }
        return orchestrator.translateAndSuggest(schema, Arrays.asList(preferredSummaries));

    }


    @PutMapping("/translateColumn")
    public Column putTranslateColumn(@Valid @RequestBody Column column) {
        if (test) System.out.println(column.toString());
        return orchestrator.translateColumn(column);
    }


    @GetMapping(value = "/summaries", produces = "application/json")
    public List<String> getSummaries() {
        return orchestrator.getAvailableSummaries();
    }


}
