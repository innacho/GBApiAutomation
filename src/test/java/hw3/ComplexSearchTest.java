package hw3;

import org.junit.jupiter.api.Test;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ComplexSearchTest extends AbstractTest {

    @Test
    void SearchWithQuery(){
        given()
                .queryParam("query","rice")
                .queryParam("apiKey",getApiKey())
                .log().all()
                .expect()
                .body("results[0].title", containsString("Rice"))
                .when()
                .request(Method.GET, getBaseUrl()+"recipes/complexSearch")
                .prettyPeek()
                .then()
                .statusCode(200);
    }

    @Test
    void SearchRecipesSortedByCalories(){
        Response response = given()
                .queryParam("sort","calories")
                .queryParam("apiKey",getApiKey())
                .log().all()
                .when()
                .request(Method.GET, getBaseUrl()+"recipes/complexSearch");
        assertThat(response.getStatusCode(), is(200));
        JsonPath body = response.getBody().jsonPath();
        float secondRecipeCalories = body.get("results[1].nutrition.nutrients[0].amount");
        assertThat(body.get("results[0].nutrition.nutrients[0].amount"), greaterThan(secondRecipeCalories));
    }

    @Test
    void SearchRecipesWithWrongApiKey(){
        given()
                .queryParam("query","rice")
                .queryParam("apiKey",getApiKey()+"12")
                .log().all()
                .expect()
                .body("message", containsString("You are not authorized"))
                .when()
                .request(Method.GET, getBaseUrl()+"recipes/complexSearch")
                .prettyPeek()
                .then()
                .statusCode(401);
    }

    @Test
    void SearchRecipesWithWrongContentTypeHeader(){
        Response response = given()
                .queryParam("query","rice")
                .queryParam("apiKey",getApiKey())
                .contentType("text")
                .when()
                .request(Method.GET, getBaseUrl()+"recipes/complexSearch");

        assertThat(response.getStatusCode(), is(400));
        String responseText = response.getBody().prettyPrint();
        assertThat(responseText, containsString("Bad Content-Type header value"));
    }

    @Test
    void SearchRecipesWithMaxCalories(){
        String maxCalories = "1200";
        Response response = given()
                .queryParam("sort","calories")
                .queryParam("maxCalories",maxCalories)
                .queryParam("apiKey",getApiKey())
                .log().all()
                .when()
                .request(Method.GET, getBaseUrl()+"recipes/complexSearch");
        assertThat(response.getStatusCode(), is(200));
        JsonPath body = response.getBody().jsonPath();
        assertThat(body.get("results[0].nutrition.nutrients[0].amount"), lessThanOrEqualTo(Float.valueOf(maxCalories)));
    }

}
