package it.disco.unimib.suggester.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import it.disco.unimib.suggester.model.Column;
import it.disco.unimib.suggester.model.Header;
import it.disco.unimib.suggester.model.LanguageType;
import it.disco.unimib.suggester.model.TableSchema;
import lombok.extern.java.Log;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.Matchers.equalTo;


@RunWith(SpringRunner.class)
//@WebMvcTest(SuggestController.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@AutoConfigureMockMvc
@Log
public class SuggestControllerTestIT {

    @LocalServerPort
    private int port;


    @Before
    public void setBaseUri() {
        RestAssured.port = port;
        System.out.println(RestAssured.port);
        RestAssured.baseURI = "http://localhost"; // replace as appropriate
    }


    @Test
    public void putTranslation() {

        Header header = new Header();
        header.setOriginalWord("Casa_Giardino");
        Column column = new Column();
        column.setHeader(header);
        TableSchema schema = new TableSchema();
        schema.addColumn(column);


        Response response = RestAssured.given().contentType(ContentType.JSON)
                .body(schema)
                .put("/api/suggester/translate");

        response.getBody().prettyPrint();

        response.then().body("columnList[0].header.language", equalTo(LanguageType.IT.toString()));
    }


}