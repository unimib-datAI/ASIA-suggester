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


@SuppressWarnings("SpellCheckingInspection")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Log
public class SuggestControllerTestIT {

    @LocalServerPort
    private int port;
    private Column columnITA;

    @Autowired
    private SuggestController controller;
    private Column columnENG;
    private TableSchema tableSchemaENG;
    private TableSchema tableSchemaITA;

    @Before
    public void setupForTest() {
        RestAssured.port = port;
        System.out.println(RestAssured.port);
        baseURI = "http://localhost"; // replace as appropriate

        Header headerITA = new Header();
        headerITA.setOriginalWord("CasaGiardino");
        columnITA = new Column();
        columnITA.setHeader(headerITA);
        tableSchemaITA = new TableSchema();
        tableSchemaITA.addColumn(columnITA);

        Header headerENG = new Header();
        headerENG.setOriginalWord("HomeAndGarden");
        columnENG = new Column();
        columnENG.setHeader(headerENG);
        tableSchemaENG = new TableSchema();
        tableSchemaENG.addColumn(columnENG);
        tableSchemaENG.setLanguage(LanguageType.EN);
        tableSchemaENG.setForceSingleLanguage(true);
        controller.setTest(true);

    }


    @Test
    public void putTranslationTableSchemaITA() {

        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(tableSchemaITA)
                .param("preferredSummaries", "linkedgeodata", "dbpedia-2016-10")
                .put("/suggester/api/schema/translate");

        response.getBody().prettyPrint();

        response.then().body("columnList[0].header.language", equalTo(LanguageType.IT.toString()));

    }

    @Test
    public void putTranslationTableSchemaENG() {

        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(tableSchemaENG)
                .param("preferredSummaries", "linkedgeodata", "dbpedia-2016-10")
                .put("/suggester/api/schema/translate");

        response.getBody().prettyPrint();

        response.then().body("columnList[0].header.language", equalTo(LanguageType.EN.toString()));

    }

    @Test
    public void putTranslationColumnITA() {
        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .body(columnITA)
                        .put("/suggester/api/column/translate");
        response.getBody().prettyPrint();
    }

    @Test
    public void putTranslationColumnITALOV() {
        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .body(columnITA)
                        .param("suggester", SuggestController.TypeSuggester.LOV.getValue())
                        .put("/suggester/api/column/translate");
        response.getBody().prettyPrint();
    }

    @Test
    public void putTranslationColumnENG() {
        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .body(columnENG)
                        .put("/suggester/api/column/translate");
        response.getBody().prettyPrint();
    }


    @Test
    public void getSummaries() {
        given().contentType(ContentType.JSON).get("/suggester/api/summaries").getBody().prettyPrint();
    }

}