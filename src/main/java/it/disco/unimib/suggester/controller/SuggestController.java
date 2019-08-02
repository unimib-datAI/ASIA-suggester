package it.disco.unimib.suggester.controller;


import it.disco.unimib.suggester.model.table.Column;
import it.disco.unimib.suggester.model.table.TableSchema;
import it.disco.unimib.suggester.service.Orchestrator;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

@RestController
@RequestMapping("/suggester/api")
public class SuggestController {

    @Getter
    @Setter
    private boolean test = false;

    private final Orchestrator orchestrator;


    public SuggestController(Orchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @PutMapping(value = "/schema/translate", consumes = "application/json", produces = "application/json")
    public TableSchema putTranslateSchema(@Valid @RequestBody TableSchema schema,
                                          @RequestParam(name = "preferredSummaries[]", required = false) String[] preferredSummaries) {
        if (test) {
            String summaries = String.join(",", asList(preferredSummaries));
            System.out.println(summaries);
        }
        return preferredSummaries != null
                ? orchestrator.translateAndSuggest(schema, asList(preferredSummaries))
                : orchestrator.translateAndSuggest(schema, emptyList());
    }


    @PutMapping(value = "column/translate", consumes = "application/json", produces = "application/json")
    public Column putTranslateColumn(@Valid @RequestBody Column column,
                                     @RequestParam(name = "preferredSummaries[]", required = false) String[] preferredSummaries) {
        if (test) System.out.println(column.toString());

        return preferredSummaries != null
                ? orchestrator.translateAndSuggest(column, asList(preferredSummaries))
                : orchestrator.translateAndSuggest(column, emptyList());
    }


    @GetMapping(value = "/summaries", produces = "application/json")
    public List<String> getSummaries() {
        return orchestrator.getAvailableSummaries();
    }


}
