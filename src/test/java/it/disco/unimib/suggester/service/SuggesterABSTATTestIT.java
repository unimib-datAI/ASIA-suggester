package it.disco.unimib.suggester.service;

import it.disco.unimib.suggester.model.suggestion.Suggestion;
import it.disco.unimib.suggester.service.suggester.ISuggester;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.*;
import static org.springframework.util.CollectionUtils.isEmpty;


@SuppressWarnings("SpellCheckingInspection")
@RunWith(SpringRunner.class)
@SpringBootTest
public class SuggesterABSTATTestIT {


    @Qualifier("ABSTATSuggester")
    @Autowired
    private ISuggester suggesterAbstat;

    @Before
    public void setup() {
        suggesterAbstat.setTest(true);
    }

    @Test
    public void propertySuggestions() {

        List<Suggestion> suggestions = suggesterAbstat.propertySuggestions("home");
        assertFalse(isEmpty(suggestions));
        assertEquals(suggestions.get(0).getPrefix(), "foaf");

        List<Suggestion> homeGarden = suggesterAbstat.propertySuggestions("HomeGarden");
        assertTrue(isEmpty(homeGarden));

    }


    @Test
    public void propertySuggestionsMultipleKeywords() {
        List<Suggestion> suggestions =
                suggesterAbstat.propertySuggestionsMultipleKeywords(Arrays.asList("home", "house"))
                        .stream()
                        .filter(suggestion -> !StringUtils.isEmpty(suggestion))
                        .flatMap(Collection::stream)
                        .distinct()
                        .sorted(comparing(Suggestion::getOccurrence)).collect(toList());
        assertFalse(isEmpty(suggestions));
    }

    @Test
    public void abstatListSummaries() {
        suggesterAbstat.setTest(true);
        List<String> datasets = suggesterAbstat.getSummaries();
        assertEquals(datasets.get(0), "linkedgeodata");
    }

    @Test
    public void typeSuggestions() {
        List<Suggestion> suggestions = suggesterAbstat.typeSuggestions("home");
        assertTrue(isEmpty(suggestions));
    }

    @Test
    public void typeSuggestionsMultipleKeywords() {
        List<Suggestion> suggestions = suggesterAbstat.typeSuggestionsMultipleKeywords(Arrays.asList("home", "house"))
                .stream()
                .filter(suggestion -> !StringUtils.isEmpty(suggestion))
                .flatMap(Collection::stream)
                .distinct()
                .sorted(comparing(Suggestion::getOccurrence)).collect(toList());
        assertFalse(isEmpty(suggestions));
    }
}