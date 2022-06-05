package hw3;

import org.junit.jupiter.api.Test;
import io.restassured.http.Method;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

public class ClassifyCuisineTest extends AbstractTest{
    @Test
    void ClassifyCuisineWithQuery(){
        given()
                .queryParam("apiKey",getApiKey())
                .contentType("application/x-www-form-urlencoded")
                .formParam("title","sushi")
                .log().all()
                .expect()
                .body("cuisine", equalTo("Japanese"))
                .body("confidence", greaterThan(0.5F))
                .log().all()
                .when()
                .request(Method.POST, getBaseUrl()+"recipes/cuisine")
                .then()
                .statusCode(200);
    }

    @Test
    void ClassifyCuisineWithWrongContentTypeHeader(){
        Response response = given()
                .queryParam("query","rice")
                .queryParam("apiKey",getApiKey())
                .contentType("text")
                .when()
                .request(Method.POST, getBaseUrl()+"recipes/cuisine");

        assertThat(response.getStatusCode(), is(500));
        String responseText = response.getBody().asPrettyString();
        assertThat(responseText, containsString("IllegalArgumentException"));
    }

    @Test
    void ClassifyCuisineForDeLangTitle(){
        given()
                .queryParam("apiKey",getApiKey())
                .queryParam("language", "de")
                .contentType("application/x-www-form-urlencoded")
                .formParam("title","Burger Kuchen")
                .log().all()
                .expect()
                .body("cuisine", equalTo("American"))
                .body("confidence", greaterThan(0.5F))
                .log().all()
                .when()
                .request(Method.POST, getBaseUrl()+"recipes/cuisine")
                .then()
                .statusCode(200);
    }

    @Test
    void ClassifyCuisineForBurgerIngredientList(){
        given()
                .queryParam("apiKey",getApiKey())
                .contentType("application/x-www-form-urlencoded")
                .formParam("title", "Burger")
                .formParam("ingredientList","\n 1 large beefsteak tomato\n" +
                        "½ tsps black pepper\n" +
                        "some canola oil\n" +
                        "6  split english muffins\n" +
                        "1½ Tbsps fresh flat-leaf parsley\n" +
                        "3 pounds ground beef chuck\n" +
                        "2 tsps kosher salt\n" +
                        "3 Tbsps salted butter")
                .log().all()
                .expect()
                .body("cuisine", equalTo("American"))
                .body("confidence", greaterThan(0.5F))
                .log().all()
                .when()
                .request(Method.POST, getBaseUrl()+"recipes/cuisine")
                .then()
                .statusCode(200);
    }

    @Test
    void ClassifyCuisineWithWrongApiKey(){
        given()
                .queryParam("apiKey",getApiKey()+"12")
                .log().all()
                .expect()
                .body("message", containsString("You are not authorized"))
                .when()
                .request(Method.POST, getBaseUrl()+"recipes/cuisine")
                .prettyPeek()
                .then()
                .statusCode(401);
    }
}
