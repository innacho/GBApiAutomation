package hw;

import hw.dto.FailureResponse;
import io.restassured.http.Method;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public class ShoppingListTest extends AbstractTestConnectUser {

    @ParameterizedTest
    @CsvSource({ "'baking powder', Baking", "'olive oil', 'Pantry Items'", "tomatoes, Produce" })
    void AddToShoppingList(String item, String aisle){
        // adding to shopping list
        String id = given().spec(getRequestSpecHash())
                .body(generateAddToShoppingListRequest(item,aisle))
                .expect()
                .body("name", equalTo(item))
                .when()
                .request(Method.POST, getBaseUrl()+"mealplanner/{username}/shopping-list/items",getUsername())
                .then()
                .spec(getResponseSpec())
                .extract()
                .jsonPath()
                .get("id")
                .toString();
        assertThat(id,notNullValue());
    }

    @Test
    void AddToShoppingListWrongApiKey(){
        given().spec(getRequestSpecHashWrongApiKey())
                .log().all()
                .when()
                .request(Method.POST, getBaseUrl()+"mealplanner/{username}/shopping-list/items", getUsername())
                // .prettyPeek()
                .then()
                .spec(getResponseSpecNotAuth());
    }

    @Test
    void AddToShoppingListBadRequest(){
        FailureResponse response = given().spec(getRequestSpecHash())
                .queryParam("apiKey", getApiKey()+"12")
                .when()
                .request(Method.POST, getBaseUrl()+"mealplanner/{username}/shopping-list/items", getUsername())
                .then()
                .extract()
                .response()
                .body()
                .as(FailureResponse.class);

        assertThat(response.getCode(), equalTo(400));
        assertThat(response.getStatus(), equalTo("failure"));
        assertThat(response.getMessage(), containsString("Json could not be parsed"));
    }

    @Test
    void AddToShoppingListWrongHash(){
        given().spec(getRequestSpec())
                .queryParam("hash", "12")
                .when()
                .request(Method.POST, getBaseUrl()+"mealplanner/{username}/shopping-list/items", getUsername())
             //   .prettyPeek()
                .then()
                .spec(getResponseSpecNotAuth());
    }

    @Test
    void AddToShoppingListWrongUsername(){
        given().spec(getRequestSpecHash())
                .when()
                .request(Method.POST, getBaseUrl()+"mealplanner/{username}/shopping-list/items",getUsername()+"12")
            //    .prettyPeek()
                .then()
                .spec(getResponseSpecNotAuth());
    }

    @Test
    void GetShoppingListTest(){
        given().spec(getRequestSpecHash())
                .when()
                .request(Method.GET, getBaseUrl()+"mealplanner/{username}/shopping-list", getUsername())
                .then()
                .spec(getResponseSpec());
    }

    @Test
    void GetShoppingListWrongApiKey(){
        given().spec(getRequestSpecHashWrongApiKey())
                .when()
                .request(Method.GET, getBaseUrl()+"mealplanner/{username}/shopping-list", getUsername())
                .then()
                .spec(getResponseSpecNotAuth());
    }

    @Test
    void GetShoppingListWrongHash(){
        given().spec(getRequestSpec())
                .queryParam("hash", "12")
                .when()
                .request(Method.GET, getBaseUrl()+"mealplanner/{username}/shopping-list", getUsername())
                .then()
                .spec(getResponseSpecNotAuth());
    }

    @Test
    void GetShoppingListWrongUsername(){
        given().spec(getRequestSpecHash())
                .when()
                .request(Method.GET, getBaseUrl()+"mealplanner/{username}/shopping-list","12")
             //   .prettyPeek()
                .then()
                .spec(getResponseSpecNotAuth());
    }

    @Test
    void GetShoppingListWrongContentType(){
        given().spec(getRequestSpecHash())
                .contentType("text")
                .expect()
                .body(containsString("Bad Content-Type header value"))
                .when()
                .request(Method.GET, getBaseUrl()+"mealplanner/{username}/shopping-list", getUsername())
            //    .prettyPeek()
                .then()
                .statusCode(400);
    }

    @Test
    void GetShoppingListWrongHTTPMethod(){
        given().spec(getRequestSpecHash())
                .expect()
                .body(containsString("The specified HTTP method is not allowed for the requested resource"))
                .when()
                .request(Method.POST, getBaseUrl()+"mealplanner/{username}/shopping-list", getUsername())
           //     .prettyPeek()
                .then()
                .statusCode(405);
    }
}
