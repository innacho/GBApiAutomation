package hw3;

import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ShoppingList extends AbstractTest {
    static private String username;
    static private String hash;
    private String id;

    @ParameterizedTest
    @CsvSource({ "'baking powder', Baking", "'olive oil', 'Pantry Items'", "tomatoes, Produce" })
    void AddToShoppingList(String item, String aisle){
        //calling Connect User to get Username and Hash
        Response response = given()
                .queryParam("apiKey", getApiKey())
                .contentType("application/json")
                .body("{\n"
                        + " \"username\": \"innaf\",\n"
                        + " \"firstName\": \"Inna\",\n"
                        + " \"lastName\": \"Chonka\",\n"
                        + " \"email\": \"fedorovainna@rambler.ru\",\n"
                        + "}")
        //        .log().all()
                .when()
                .request(Method.POST, getBaseUrl()+"users/connect");
        assertThat(response.getStatusCode(), is(200));
        JsonPath body = response.getBody().jsonPath();
        username = body.get("username");
        hash = body.get("hash");


        // adding to shopping list
        id = given()
                .queryParam("apiKey", getApiKey())
                .queryParam("hash", hash)
                .contentType("application/json")
                .body("{\n"
                        + " \"item\": "+ item +",\n"
                        + " \"aisle\": "+ aisle +",\n"
                        + " \"parse\": true \n,"
                        + "}")
       //         .log().all()
                .expect()
                .body("name", equalTo(item))
                .when()
                .request(Method.POST, getBaseUrl()+"mealplanner/{username}/shopping-list/items",username)
        //        .prettyPeek()
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .get("id")
                .toString();
        assertThat(id,notNullValue());
    }

    @AfterAll
    static void tearDown() {
        // cleaning shopping list after tests

        //step1: getting all the items from shopping list
        Response response = given()
                .queryParam("apiKey", getApiKey())
                .queryParam("hash", hash)
                .contentType("application/json")
         //       .log().all()
                .when()
                .request(Method.GET, getBaseUrl()+"mealplanner/{username}/shopping-list",username)
                .prettyPeek();
        assertThat(response.getStatusCode(), is(200));
        JsonPath resBody = response.getBody().jsonPath();

        String resId = resBody.get("aisles[0].items[0].id").toString();

//        // step2: deleting all the items from shopping list
        given()
                .queryParam("hash", hash)
                .queryParam("apiKey", getApiKey())
                .log().all()
                .expect()
                .body("status", equalTo("success"))
                .when()
                .delete(getBaseUrl() + "mealplanner/{username}/shopping-list/items/" + resId, username)
                .prettyPeek()
                .then()
                .statusCode(200);
    }
}
