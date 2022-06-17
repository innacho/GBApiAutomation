package hw;

import hw.dto.FormParamsRequest;
import org.junit.jupiter.api.Test;
import io.restassured.http.Method;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

public class ClassifyCuisineTest extends AbstractTest{
    @Test
    void ClassifyCuisineWithQuery(){
        given().spec(getRequestSpecFormParam())
                .formParam("title","sushi")
                .expect()
                .body("cuisine", equalTo("Japanese"))
                .when()
                .request(Method.POST, getBaseUrl()+"recipes/cuisine")
                .then()
                .spec(getResponseSpecFormParam());
    }

    @Test
    void ClassifyCuisineWithWrongContentTypeHeader(){
        Response response = given().spec(getRequestSpecFormParam())
                .contentType("text")
                .when()
                .request(Method.POST, getBaseUrl()+"recipes/cuisine");

        assertThat(response.getStatusCode(), is(500));
        String responseText = response.getBody().asPrettyString();
        assertThat(responseText, containsString("IllegalArgumentException"));
    }

    @Test
    void ClassifyCuisineForDeLangTitle(){
        given().spec(getRequestSpecFormParam())
                .queryParam("language", "de")
                .formParam("title","Burger Kuchen")
                .expect()
                .body("cuisine", equalTo("American"))
                .when()
                .request(Method.POST, getBaseUrl()+"recipes/cuisine")
                .then()
                .spec(getResponseSpecFormParam());
    }

    @Test
    void ClassifyCuisineForBurgerIngredientList(){
        given().spec(getRequestSpecFormParam())
                .formParam("title", FormParamsRequest.burgerTitle)
                .formParam("ingredientList", FormParamsRequest.burgerIngredientList)
                .expect()
                .body("cuisine", equalTo(FormParamsRequest.burgerCuisine))
                .when()
                .request(Method.POST, getBaseUrl()+"recipes/cuisine")
                .then()
                .spec(getResponseSpecFormParam());
    }

    @Test
    void ClassifyCuisineWithWrongApiKey(){
        given().spec(getRequestSpecNotAuth())
                .when()
                .request(Method.POST, getBaseUrl()+"recipes/cuisine")
                .then()
                .spec(getResponseSpecNotAuth());
    }
}
