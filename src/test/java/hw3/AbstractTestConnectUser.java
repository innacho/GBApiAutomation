package hw3;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public abstract class AbstractTestConnectUser extends AbstractTest{
    static private String username;
    static private String hash;
    private static RequestSpecification requestSpecificationHash;
    private static RequestSpecification requestSpecificationHashWrongApiKey;
    private static ResponseSpecification responseSpecificationSuccess;

    static void createSpecs(){
        requestSpecificationHash = new RequestSpecBuilder()
                .addQueryParam("apiKey", getApiKey())
                .addQueryParam("hash", hash)
                .setContentType(ContentType.JSON)
        //        .log(LogDetail.ALL)
                .build();

        requestSpecificationHashWrongApiKey = new RequestSpecBuilder()
                .addQueryParam("apiKey", getApiKey()+"12")
                .addQueryParam("hash", hash)
                .setContentType(ContentType.JSON)
         //       .log(LogDetail.ALL)
                .build();

        responseSpecificationSuccess = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectStatusLine("HTTP/1.1 200 OK")
                .expectContentType(ContentType.JSON)
                .expectBody("status", equalTo("success"))
                .expectResponseTime(Matchers.lessThan(5000L))
                .build();


    }

    @BeforeAll
    static void getUsernameAndHash(){
        //calling Connect User to get Username and Hash
        JsonPath body = given().spec(getRequestSpec())
                .body("{\n"
                        + " \"username\": \"innaf\",\n"
                        + " \"firstName\": \"Inna\",\n"
                        + " \"lastName\": \"Chonka\",\n"
                        + " \"email\": \"fedorovainna@rambler.ru\",\n"
                        + "}")
                .when()
                .request(Method.POST, getBaseUrl()+"users/connect")
                .then()
                .spec(getResponseSpec())
                .extract()
                .body()
                .jsonPath();

        username = body.get("username");
        hash = body.get("hash");

        createSpecs();
    }

    public static String getUsername() {return username;}

    public static RequestSpecification getRequestSpecHash() {
        return requestSpecificationHash;
    }

    public static RequestSpecification getRequestSpecHashWrongApiKey() {
        return requestSpecificationHashWrongApiKey;
    }

    public static ResponseSpecification getResponseSpecSuccess() {
        return responseSpecificationSuccess;
    }


    @AfterAll
    static void tearDown() {
        // cleaning shopping list after tests

        //step1: getting all the items from shopping list
        JsonPath resBody = given().spec(requestSpecificationHash)
                .when()
                .request(Method.GET, getBaseUrl()+"mealplanner/{username}/shopping-list", username)
                .then()
                .spec(getResponseSpec())
                .extract()
                .body()
                .jsonPath();

        // step2: deleting all the items from shopping list if it is not empty

        float cost = resBody.get("cost");
        if(cost > 0.0) {
            int i = 0;
            while(true) {
                int j = 0;
                String aisleAddress = "aisles[" + i + "]";
                try{
                    resBody.get(aisleAddress).toString();
                }
                catch(Exception e){
                    break;
                }

                while(true) {
                    try{
                        String idAddress = "aisles[" + i + "].items[" + j + "].id";
                        String resId = resBody.get(idAddress).toString();

                        given().spec(getRequestSpecHash())
                                .log().all()
                                .when()
                                .delete(getBaseUrl() + "mealplanner/{username}/shopping-list/items/" + resId, username)
                                .then()
                                .spec(getResponseSpecSuccess());
                        j++;
                    }
                    catch(Exception e){
                        break;
                    }
                }
                i++;
            }

            // step3: making sure that shopping list is empty
            JsonPath resBodyAfter = given().spec(getRequestSpecHash())
                    .when()
                    .request(Method.GET, getBaseUrl()+"mealplanner/{username}/shopping-list", username)
                    .prettyPeek()
                    .then()
                    .spec(getResponseSpec())
                    .extract()
                    .body()
                    .jsonPath();

            float cost3 = resBodyAfter.get("cost");
            assertThat(cost3,equalTo(0.0F));
        }
    }
}
