package it.disco.unimib.suggester.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import it.disco.unimib.suggester.model.table.Column;
import it.disco.unimib.suggester.model.table.Header;
import it.disco.unimib.suggester.model.table.TableSchema;
import it.disco.unimib.suggester.model.translation.LanguageType;
import lombok.extern.java.Log;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Log
public class SuggestControllerTestIT {

    @LocalServerPort
    private int port;
    private TableSchema schema;
    private Column column;

    @Autowired
    private SuggestController controller;

    @Before
    public void setBaseUri() {
        RestAssured.port = port;
        System.out.println(RestAssured.port);
        baseURI = "http://localhost"; // replace as appropriate

        Header header = new Header();
        header.setOriginalWord("CasaGiardino");
        column = new Column();
        column.setHeader(header);
        schema = new TableSchema();
        schema.addColumn(column);
        controller.setTest(true);

    }


    @Test
    public void putTranslation() {

        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(schema)
                .param("preferredSummaries[]", new String[]{"linkedgeodata", "dbpedia-2016-10"})
                .put("/api/suggester/translate");

        response.getBody().prettyPrint();

        response.then().body("columnList[0].header.language", equalTo(LanguageType.IT.toString()));
    }

    @Test
    public void putTranslationColumn() {
        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .body(column)
                        .put("/api/suggester/translateColumn");
        response.getBody().prettyPrint();
    }


    @Test
    public void getSummaries() {
        given().contentType(ContentType.JSON).get("/api/suggester/summaries").getBody().prettyPrint();
    }

}