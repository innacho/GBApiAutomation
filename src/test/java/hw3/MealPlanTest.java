package hw3;

import org.junit.jupiter.api.Test;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

public class MealPlanTest extends AbstractTest {
    private String username;
    private String hash;
    private String id;
    @Test
    void AddAndDeleteMeal(){
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
                .log().all()
                .when()
                .request(Method.POST, getBaseUrl()+"users/connect");
        assertThat(response.getStatusCode(), is(200));
        JsonPath body = response.getBody().jsonPath();
        username = body.get("username");
        hash = body.get("hash");


        // adding menu-item to meal plan
        id = given()
                .queryParam("apiKey", getApiKey())
                .queryParam("hash", hash)
                .contentType("application/json")
                .body("{\n"
                        + " \"date\": 1644881179,\n"
                        + " \"slot\": 1,\n"
                        + " \"position\": 0,\n"
                        + " \"type\": \"MENU_ITEM\",\n"
                        + " \"value\": {\n"
                        + " \"id\" : 378557 ,\n"
                        + " \"servings\" : 2 ,\n"
                        + " \"title\": \"Pizza 73 BBQ Steak Pizza, 9\",\n"
                        + " \"imageType\": \"png\",\n"
                        + " }\n"
                        + "}")
                .log().all()
                .expect()
                .body("status", equalTo("success"))
                .when()
                .request(Method.POST, getBaseUrl()+"mealplanner/{username}/items",username)
                .prettyPeek()
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .get("id")
                .toString();

        // deleting menu-item from meal plan
        given()
                .queryParam("hash", hash)
                .queryParam("apiKey", getApiKey())
                .log().all()
                .expect()
                .body("status", equalTo("success"))
                .when()
                .delete(getBaseUrl()+"mealplanner/{username}/items/" + id, username)
                .prettyPeek()
                .then()
                .statusCode(200);

    }

    @Test
    void DeleteNonExistentMeal(){
        //calling Connect User to get Username and Hash
        Response response = given()
                .queryParam("apiKey",getApiKey())
                .contentType("application/json")
                .body("{\n"
                        + " \"username\": \"innaf\",\n"
                        + " \"firstName\": \"Inna\",\n"
                        + " \"lastName\": \"Chonka\",\n"
                        + " \"email\": \"fedorovainna@rambler.ru\",\n"
                        + "}")
                .log().all()
                .when()
                .request(Method.POST, getBaseUrl()+"users/connect");
        assertThat(response.getStatusCode(), is(200));
        JsonPath body = response.getBody().jsonPath();
        username = body.get("username");
        hash = body.get("hash");

        id = "9";
        // trying to delete non existent menu-item from meal plan
        given()
                .queryParam("hash", hash)
                .queryParam("apiKey", getApiKey())
                .log().all()
                .expect()
                .body("status", equalTo("failure"))
                .body("message", containsString("does not exist"))
                .when()
                .delete(getBaseUrl()+"mealplanner/{username}/items/" + id, username)
                .prettyPeek()
                .then()
                .statusCode(404);

    }

}
