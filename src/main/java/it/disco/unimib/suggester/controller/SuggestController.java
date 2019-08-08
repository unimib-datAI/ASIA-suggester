package it.disco.unimib.suggester.controller;


import it.disco.unimib.suggester.model.table.Column;
import it.disco.unimib.suggester.model.table.TableSchema;
import it.disco.unimib.suggester.model.translation.LanguageType;
import it.disco.unimib.suggester.service.Orchestrator;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@RestController
@CrossOrigin("*")
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
                                          @RequestParam(name = "preferredSummaries", required = false) String[] preferredSummaries,
                                          @RequestParam(name = "suggester", required = false) String suggester) {
        if (test) {
            String summaries = String.join(",", asList(preferredSummaries));
            System.out.println(summaries);
        }

        String suggesterName;
        if (Objects.nonNull(suggester) &&
                TypeSuggester.checkFromName(suggester)) suggesterName = suggester;
        else suggesterName = null;

        TableSchema tableSchema = preferredSummaries != null
                ? orchestrator.translateAndSuggest(schema, asList(preferredSummaries), suggesterName)
                : orchestrator.translateAndSuggest(schema, emptyList(), suggesterName);
        return tableSchema;
    }


    @PutMapping(value = "column/translate", consumes = "application/json", produces = "application/json")
    public Column putTranslateColumn(@Valid @RequestBody Column column,
                                     @RequestParam(name = "preferredSummaries", required = false) String[] preferredSummaries,
                                     @RequestParam(name = "suggester", required = false) String suggester) {
        if (test) System.out.println(column.toString());

        String suggesterName;
        if (Objects.nonNull(suggester) &&
                TypeSuggester.checkFromName(suggester)) suggesterName = suggester;
        else suggesterName = null;

        return preferredSummaries != null
                ? orchestrator.translateAndSuggest(column, asList(preferredSummaries), suggesterName)
                : orchestrator.translateAndSuggest(column, emptyList(), suggesterName);
    }


    @GetMapping(value = "summaries", produces = "application/json")
    public List<String> getSummaries(@RequestParam(name = "suggester", required = false) String suggester) {

        String suggesterName;
        if (Objects.nonNull(suggester) &&
                TypeSuggester.checkFromName(suggester)) suggesterName = suggester;
        else suggesterName = null;

        return orchestrator.getAvailableSummaries(suggesterName);
    }

    @GetMapping(value = "suggesters")
    public List<String> getSuggesters() {
        return Arrays.stream(TypeSuggester.values()).map(TypeSuggester::getValue).collect(toList());
    }


    @GetMapping(value = "languages")
    public LanguageType[] getLanguages() {
        return LanguageType.values();
    }


    enum TypeSuggester {
        ABSTAT("abstat"), LOV("lov");

        private final String value;

        TypeSuggester(String suggester) {
            this.value = suggester;

        }

        String getValue() {
            return value;
        }

        public static boolean checkFromName(String x) {
            if (Objects.nonNull(x))
                for (TypeSuggester currentType : TypeSuggester.values())
                    if (x.equals(currentType.getValue())) return true;
            return false;
        }
    }



}
