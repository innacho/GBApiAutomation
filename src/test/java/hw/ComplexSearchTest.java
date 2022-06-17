package hw;

import org.junit.jupiter.api.Test;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ComplexSearchTest extends AbstractTest {


    @Test
    void SearchWithQuery(){
        given().spec(getRequestSpec())
                .queryParam("query","rice")
                .expect()
                .body("results[0].title", containsString("Rice"))
                .when()
                .request(Method.GET, getBaseUrl()+"recipes/complexSearch")
         //       .prettyPeek()
                .then()
                .spec(getResponseSpec());
    }

    @Test
    void SearchRecipesSortedByCalories(){
        JsonPath body = given().spec(getRequestSpec())
                .queryParam("sort","calories")
                .when()
                .request(Method.GET, getBaseUrl()+"recipes/complexSearch")
                .then()
                .spec(getResponseSpec())
                .extract()
                .body()
                .jsonPath();

        float secondRecipeCalories = body.get("results[1].nutrition.nutrients[0].amount");
        assertThat(body.get("results[0].nutrition.nutrients[0].amount"), greaterThan(secondRecipeCalories));
    }

    @Test
    void SearchRecipesWithWrongApiKey(){
        given().spec(getRequestSpecNotAuth())
                .when()
                .request(Method.GET, getBaseUrl()+"recipes/complexSearch")
                .then()
                .spec(getResponseSpecNotAuth());
    }

    @Test
    void SearchRecipesWithWrongContentTypeHeader(){
        given().spec(getRequestSpec())
                .queryParam("query","rice")
                .contentType("text")
                .expect()
                .body(containsString("Bad Content-Type header value"))
                .when()
                .request(Method.GET, getBaseUrl()+"recipes/complexSearch")
                .then()
                .statusCode(400);
    }

    @Test
    void SearchRecipesWithMaxCalories(){
        String maxCalories = "1200";
        JsonPath body = given().spec(getRequestSpec())
                .queryParam("sort","calories")
                .queryParam("maxCalories", maxCalories)
                .when()
                .request(Method.GET, getBaseUrl()+"recipes/complexSearch")
                .then()
                .spec(getResponseSpec())
                .extract()
                .body()
                .jsonPath();

        assertThat(body.get("results[0].nutrition.nutrients[0].amount"), lessThanOrEqualTo(Float.valueOf(maxCalories)));
    }

}
