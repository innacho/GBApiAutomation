package hw3;

import org.junit.jupiter.api.Test;
import io.restassured.http.Method;

import java.time.Instant;
import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

public class MealPlanTest extends AbstractTestConnectUser {

    private String id;

    @Test
    void AddItemToMealPlan(){
        // adding menu-item to meal plan
        LocalDate date = LocalDate.now();
        long now = Instant.now().getEpochSecond();

        id = given().spec(getRequestSpecHash())
                .body("{\n"
                        + " \"date\": "+ now + ",\n"
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
                .expect()
                .body("status", equalTo("success"))
                .when()
                .request(Method.POST, getBaseUrl()+"mealplanner/{username}/items", getUsername())
              //  .prettyPeek()
                .then()
                .spec(getResponseSpecSuccess())
                .extract()
                .jsonPath()
                .get("id")
                .toString();

        // getting meal plan to assert
        String resId = given().spec(getRequestSpecHash())
                .when()
                .get(getBaseUrl()+"mealplanner/{username}/week/{start-date}", getUsername(), "2022-06-14")
                .then()
                .spec(getResponseSpec())
                .extract()
                .jsonPath()
                .get("days[0].items[0].id")
                .toString();

        assertThat(resId, is(id));
    }

    @Test
    void AddAndDeleteMeal(){
        LocalDate date = LocalDate.now();
        long now = Instant.now().getEpochSecond();
        // adding menu-item to meal plan
        id = given().spec(getRequestSpecHash())
                .body("{\n"
                        + " \"date\": "+ now + ",\n"
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
                .when()
                .request(Method.POST, getBaseUrl()+"mealplanner/{username}/items", getUsername())
                .prettyPeek()
                .then()
                .spec(getResponseSpecSuccess())
                .extract()
                .jsonPath()
                .get("id")
                .toString();

        // deleting menu-item from meal plan
        given().spec(getRequestSpecHash())
                .when()
                .delete(getBaseUrl()+"mealplanner/{username}/items/" + id, getUsername())
                .prettyPeek()
                .then()
                .spec(getResponseSpecSuccess());

        // getting meal plan to assert
        String result = given().spec(getRequestSpecHash())
                .when()
                .get(getBaseUrl()+"mealplanner/{username}/week/{start-date}", getUsername(), "2022-06-14")
                .prettyPeek()
                .then()
                .spec(getResponseSpec())
                .extract()
                .jsonPath()
                .get("days")
                .toString();

        assertThat(result, not(containsString(id)));
    }

    @Test
    void DeleteNonExistentMeal(){
        id = "9";
        // trying to delete non existent menu-item from meal plan
        given().spec(getRequestSpecHash())
                .log().all()
                .expect()
                .body("status", equalTo("failure"))
                .body("message", containsString("does not exist"))
                .when()
                .delete(getBaseUrl()+"mealplanner/{username}/items/" + id, getUsername())
                .prettyPeek()
                .then()
                .statusCode(404);
    }
}
