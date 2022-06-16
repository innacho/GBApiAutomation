package hw3;

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
                .formParam("title", "Burger")
                .formParam("ingredientList","\n 1 large beefsteak tomato\n" +
                        "½ tsps black pepper\n" +
                        "some canola oil\n" +
                        "6  split english muffins\n" +
                        "1½ Tbsps fresh flat-leaf parsley\n" +
                        "3 pounds ground beef chuck\n" +
                        "2 tsps kosher salt\n" +
                        "3 Tbsps salted butter")
                .expect()
                .body("cuisine", equalTo("American"))
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
