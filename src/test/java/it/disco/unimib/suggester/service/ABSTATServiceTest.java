package it.disco.unimib.suggester.service;

import it.disco.unimib.suggester.model.Suggestions;
import lombok.extern.java.Log;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;


@RunWith(SpringRunner.class)
@SpringBootTest
@Log
public class ABSTATServiceTest {


    @Autowired
    private ABSTATService abstatService;

    @Test
    public void propertySuggestions() throws IOException {

        Suggestions suggestions = abstatService.propertySuggestions("home", true);

        Assert.assertEquals(suggestions.getSuggestions().get(0).getPrefix(), "foaf");

    }
}