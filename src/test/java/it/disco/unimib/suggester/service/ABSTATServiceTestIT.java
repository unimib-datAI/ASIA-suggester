package it.disco.unimib.suggester.service;

import it.disco.unimib.suggester.model.Suggestion;
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
public class ABSTATServiceTestIT {


    @Autowired
    private ABSTATService abstatService;

    @Before
    public void setup() {
        abstatService.setTest(true);
    }

    @Test
    public void propertySuggestions() {

        List<Suggestion> suggestions = abstatService.propertySuggestions("home", true);
        assertFalse(isEmpty(suggestions));
        assertEquals(suggestions.get(0).getPrefix(), "foaf");

    }


    @Test
    public void propertySuggestionsMultipleKeywords() {
        List<Suggestion> suggestions = abstatService.propertySuggestionsMultipleKeywords(Arrays.asList("home", "house"), true);
        assertFalse(isEmpty(suggestions));


    }

    @Test
    public void abstatListSummaries() throws IOException {
        abstatService.setTest(true);
        ABSTATService.Datasets datasets = abstatService.abstatListSummaries();
        assertEquals(datasets.getDatasetsNames().get(0), "linkedgeodata");
    }

    @Test
    public void typeSuggestions() {
        List<Suggestion> suggestions = abstatService.typeSuggestions("home", true);
        assertTrue(isEmpty(suggestions));
    }

    @Test
    public void typeSuggestionsMultipleKeywords() {
        List<Suggestion> suggestions = abstatService.typeSuggestionsMultipleKeywords(Arrays.asList("home", "house"), true);
        assertFalse(isEmpty(suggestions));

    }
}