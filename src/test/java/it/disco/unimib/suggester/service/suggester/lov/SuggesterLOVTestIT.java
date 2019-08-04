package it.disco.unimib.suggester.service.suggester.lov;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import it.disco.unimib.suggester.model.suggestion.Suggestion;
import it.disco.unimib.suggester.service.suggester.ISuggester;
import it.disco.unimib.suggester.service.suggester.SuggesterUtils;
import it.disco.unimib.suggester.service.suggester.lov.domain.LOVSearchResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Objects;

@SuppressWarnings("SpellCheckingInspection")
@RunWith(SpringRunner.class)
@SpringBootTest
public class SuggesterLOVTestIT {

    @Qualifier("LOVSuggester")
    @Autowired
    private ISuggester suggesterLOV;

    @Autowired
    private SuggesterUtils suggesterUtils;

    @Before
    public void setUp() {
        suggesterLOV.setTest(true);
    }

    @Test
    public void getLOVSuggestions() {

        String res = ReflectionTestUtils.invokeMethod(suggesterLOV,
                "getLOVSuggestions",
                "Person", LOVSuggester.TypeLOV.CLASS, "");

        System.out.println(SuggesterUtils.prettify(res));
        Gson gson = new Gson();
        LOVSearchResult mappedResult = gson.fromJson(res, LOVSearchResult.class);
        System.out.println(mappedResult);

    }

    @Test
    public void getListOfSuggestions() {
        suggesterLOV.setPreferredSummaries(ImmutableList.of("dbpedia"));
        List<Suggestion> res = ReflectionTestUtils.invokeMethod(suggesterLOV,
                "getListOfSuggestions",
                "Person", LOVSuggester.TypeLOV.CLASS);

        System.out.println(Objects.requireNonNull(res).toString());

    }

    @Test
    public void getSummaries() {
        List<String> res = suggesterLOV.getSummaries();
        res.forEach(System.out::println);

    }


}