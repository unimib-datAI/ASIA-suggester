package it.disco.unimib.suggester.controller;


import it.disco.unimib.suggester.model.table.Column;
import it.disco.unimib.suggester.model.table.Header;
import it.disco.unimib.suggester.model.table.TableSchema;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(outputDir = "target/snippets")
public class SuggestControllerTest {


    @Autowired
    private MockMvc mockMvc;


/*
    @MockBean
    private ITranslator translator;

*/

    @Test
    public void putTranslation() throws Exception {
        Header header = new Header();
        header.setOriginalWord("Casa_Giardino");
        Column column = new Column();
        column.setHeader(header);
        TableSchema schema = new TableSchema();
        schema.addColumn(column);

        mockMvc.perform(put("/api/suggester/translate", schema))
                .andDo(print())
                .andExpect(status().isOk());


    }


}
