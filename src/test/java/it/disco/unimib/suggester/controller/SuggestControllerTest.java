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

import java.io.IOException;

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


        json = "{\"columnList\":[{\"header\":{\"originalWord\":\"Casa_Giardino\",\"processedWord\":\"casa giardino\"," +
                "\"splitTerms\":[\"casa\",\"giardino\"],\"translatedWord\":[" +
                "{\"translatedWord\":\"HouseGarden\",\"confidence\":0.59565}, " +
                "{\"translatedWord\":\"HomeGarden\",\"confidence\":0.58855}," +
                "{\"translatedWord\":\"HouseGrounds\",\"confidence\":0.32585}, " +
                "{\"translatedWord\":\"HomeGrounds\",\"confidence\":0.31875}," +
                "{\"translatedWord\":\"HouseSurroundingGarden\",\"confidence\":0.30205}, " +
                "{\"translatedWord\":\"HomeSurroundingGarden\",\"confidence\":0.29495}," +
                "{\"translatedWord\":\"HouseYard\",\"confidence\":0.29065}, " +
                "{\"translatedWord\":\"HomeYard\",\"confidence\":0.28355}]," +
                "\"language\":\"IT\"},\"dataType\":null}]}";

        TableSchema returnedSchema = mapper.readValue(json, TableSchema.class);
        Mockito.when(orchestrator.translateTableSchema(Mockito.any(TableSchema.class)))
                .thenReturn(returnedSchema);
    }

    @Test
    public void putTranslation() throws Exception {


        String json = mapper.writeValueAsString(schema);
        mockMvc.perform(put("/api/suggester/translate")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(this.json));


    }


}
