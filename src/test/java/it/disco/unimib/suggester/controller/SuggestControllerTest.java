package it.disco.unimib.suggester.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import it.disco.unimib.suggester.model.table.Column;
import it.disco.unimib.suggester.model.table.Header;
import it.disco.unimib.suggester.model.table.TableSchema;
import it.disco.unimib.suggester.service.Orchestrator;
import it.disco.unimib.suggester.service.translator.ITranslator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SuppressWarnings("SpellCheckingInspection")
@RunWith(SpringRunner.class)
@WebMvcTest(SuggestController.class)
//@AutoConfigureMockMvc
//@AutoConfigureRestDocs(outputDir = "target/snippets")
public class SuggestControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;


    @MockBean
    private ITranslator translator;

    @MockBean
    private Orchestrator orchestrator;
    private String json;
    private TableSchema schema;


    @Before
    public void setup() throws IOException {
        Header header = new Header();
        header.setOriginalWord("Casa_Giardino");
        Column column = new Column();
        column.setHeader(header);
        schema = new TableSchema();
        schema.addColumn(column);

        String fName = "schema-translateITA.json";

        File file = ResourceUtils.getFile("classpath:" + fName);
        json = new String(Files.readAllBytes(Paths.get(file.getPath())));
        System.out.println(json);

        TableSchema returnedSchema = mapper.readValue(json, TableSchema.class);
        Mockito.when(orchestrator.translateAndSuggest(
                Mockito.any(TableSchema.class),
                Mockito.anyObject(),
                Mockito.anyObject()))
                .thenReturn(returnedSchema);
    }

    @Test
    public void putTranslation() throws Exception {

        String json = mapper.writeValueAsString(schema);
        mockMvc.perform(
                put("/suggester/api/schema/translate")
                        .param("preferredSummaries[]", "linkedgeodata", "dbpedia-2016-10")
                        .param("suggester", SuggestController.TypeSuggester.ABSTAT.getValue())
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(this.json));


    }


}
