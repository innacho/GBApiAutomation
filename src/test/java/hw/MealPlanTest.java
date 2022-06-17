package hw;

import hw.dto.FailureResponse;
import org.junit.jupiter.api.Test;
import io.restassured.http.Method;

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
        id = given().spec(getRequestSpecHash())
                .body(generateMealPlanItemRequest())
                .when()
                .request(Method.POST, getBaseUrl()+"mealplanner/{username}/items", getUsername())
                .then()
                .spec(getResponseSpecSuccess())
                .extract()
                .jsonPath()
                .get("id")
                .toString();

        // getting meal plan to assert
        String resId = given().spec(getRequestSpecHash())
                .when()
                .get(getBaseUrl()+"mealplanner/{username}/week/{start-date}", getUsername(), generateToday())
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
        id = given().spec(getRequestSpecHash())
                .body(generateMealPlanItemRequest())
                .when()
                .request(Method.POST, getBaseUrl()+"mealplanner/{username}/items", getUsername())
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
                .get(getBaseUrl()+"mealplanner/{username}/week/{start-date}", getUsername(), generateToday())
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
        FailureResponse response = given().spec(getRequestSpecHash())
                .when()
                .delete(getBaseUrl()+"mealplanner/{username}/items/" + id, getUsername())
                .then()
                .extract()
                .response()
                .body()
                .as(FailureResponse.class);

        assertThat(response.getCode(), equalTo(404));
        assertThat(response.getStatus(), equalTo("failure"));
        assertThat(response.getMessage(), containsString("does not exist"));
    }
}
