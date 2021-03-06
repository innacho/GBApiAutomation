package hw;

import hw.dto.FailureResponse;
import io.restassured.http.Method;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public class ShoppingListDeleteTest extends AbstractTestConnectUser {
    private String id;
    @BeforeEach
    void AddItemToDelete(){
        // step1 : adding item to shopping list to delete
        id = given().spec(getRequestSpecHash())
                .body(generateAddToShoppingListRequest("baking powder","Baking"))
                .expect()
                .body("name", equalTo("baking powder"))
                .when()
                .request(Method.POST, getBaseUrl()+"mealplanner/{username}/shopping-list/items", getUsername())
                .prettyPeek()
                .then()
                .spec(getResponseSpec())
                .extract()
                .jsonPath()
                .get("id")
                .toString();

        assertThat(id,notNullValue());
    }


    @Test
    void DeleteFromShoppingList(){
        // step2 : deleting item from shopping list
        given().spec(getRequestSpecHash())
                .when()
                .delete(getBaseUrl() + "mealplanner/{username}/shopping-list/items/" + id, getUsername())
                //            .prettyPeek()
                .then()
                .spec(getResponseSpecSuccess());

        // step3: making sure that shopping list does not contain item with id anymore
        String resBody = given().spec(getRequestSpecHash())
                .when()
                .request(Method.GET, getBaseUrl()+"mealplanner/{username}/shopping-list", getUsername())
          //      .prettyPeek()
                .then()
                .spec(getResponseSpec())
                .extract()
                .body()
                .jsonPath()
                .toString();

        assertThat(resBody,not(containsString(id)));
    }

    @Test
    void DeleteFromShoppingListNonExistentItem(){
        String wrongId = "9";

        FailureResponse response = given().spec(getRequestSpecHash())
                .when()
                .delete(getBaseUrl() + "mealplanner/{username}/shopping-list/items/" + wrongId, getUsername())
                .then()
                .extract()
                .response()
                .body()
                .as(FailureResponse.class);

        assertThat(response.getCode(), equalTo(404));
        assertThat(response.getStatus(), equalTo("failure"));
        assertThat(response.getMessage(), equalTo("A shopping list item with id "+wrongId+" does not exist"));
    }

    @Test
    void DeleteFromShoppingListWrongApiKey(){
        // step2 : deleting item from shopping list
        given().spec(getRequestSpecHashWrongApiKey())
                .when()
                .delete(getBaseUrl() + "mealplanner/{username}/shopping-list/items/" + id, getUsername())
                //            .prettyPeek()
                .then()
                .spec(getResponseSpecNotAuth());
    }

    @Test
    void DeleteFromShoppingListWrongHash(){
        // step2 : deleting item from shopping list
        given().spec(getRequestSpec())
                .queryParam("hash", "12")
                .when()
                .delete(getBaseUrl() + "mealplanner/{username}/shopping-list/items/" + id, getUsername())
            //    .prettyPeek()
                .then()
                .spec(getResponseSpecNotAuth());
    }

    @Test
    void DeleteFromShoppingListWrongUserName(){
       // step2 : deleting item from shopping list
        given().spec(getRequestSpecHash())
                .when()
                .delete(getBaseUrl() + "mealplanner/{username}/shopping-list/items/" + id, "12")
         //       .prettyPeek()
                .then()
                .spec(getResponseSpecNotAuth());
    }
}
