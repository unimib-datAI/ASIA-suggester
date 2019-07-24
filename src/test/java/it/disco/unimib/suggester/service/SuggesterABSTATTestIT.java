package it.disco.unimib.suggester.service;

import it.disco.unimib.suggester.model.suggestion.Suggestion;
import it.disco.unimib.suggester.service.suggester.ISuggester;
import it.disco.unimib.suggester.service.suggester.abstat.domain.Datasets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.springframework.util.CollectionUtils.isEmpty;


@RunWith(SpringRunner.class)
@SpringBootTest
public class SuggesterABSTATTestIT {


    @Autowired
    private ISuggester suggesterAbstat;

    @Before
    public void setup() {
        suggesterAbstat.setTest(true);
    }

    @Test
    public void propertySuggestions() {

        List<Suggestion> suggestions = suggesterAbstat.propertySuggestions("home", true);
        assertFalse(isEmpty(suggestions));
        assertEquals(suggestions.get(0).getPrefix(), "foaf");

    }


    @Test
    public void propertySuggestionsMultipleKeywords() {
        List<Suggestion> suggestions = suggesterAbstat.propertySuggestionsMultipleKeywords(Arrays.asList("home", "house"), true);
        assertFalse(isEmpty(suggestions));


    }

    @Test
    public void abstatListSummaries() throws IOException {
        suggesterAbstat.setTest(true);
        Datasets datasets = suggesterAbstat.summaries();
        assertEquals(datasets.getDatasetsNames().get(0), "linkedgeodata");
    }

    @Test
    public void typeSuggestions() {
        List<Suggestion> suggestions = suggesterAbstat.typeSuggestions("home", true);
        assertTrue(isEmpty(suggestions));
    }

    @Test
    public void typeSuggestionsMultipleKeywords() {
        List<Suggestion> suggestions = suggesterAbstat.typeSuggestionsMultipleKeywords(Arrays.asList("home", "house"), true);
        assertFalse(isEmpty(suggestions));

    }
}