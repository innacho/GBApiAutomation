package hw3;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.hamcrest.Matchers;
import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.BeforeAll;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class AbstractTest {

    static Properties prop = new Properties();
    private static InputStream configFile;
    private static String apiKey;
    private static String baseUrl;
    private static RequestSpecification requestSpecification;
    private static RequestSpecification requestSpecificationNotAuth;
    private static RequestSpecification requestSpecificationFormParam;
    private static ResponseSpecification responseSpecification;
    private static ResponseSpecification responseSpecificationNotAuth;
    private static ResponseSpecification responseSpecificationFormParam;

    static void createSpecs(){
        requestSpecification = new RequestSpecBuilder()
                .addQueryParam("apiKey", apiKey)
                .setContentType(ContentType.JSON)
        //        .log(LogDetail.ALL)
                .build();

        requestSpecificationNotAuth = new RequestSpecBuilder()
                .addQueryParam("apiKey", apiKey + "12")
                .setContentType(ContentType.JSON)
         //       .log(LogDetail.ALL)
                .build();

        requestSpecificationFormParam = new RequestSpecBuilder()
                .addQueryParam("apiKey", apiKey)
                .setContentType(ContentType.URLENC)
         //       .log(LogDetail.ALL)
                .build();

        responseSpecification = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectStatusLine("HTTP/1.1 200 OK")
                .expectContentType(ContentType.JSON)
                .expectResponseTime(Matchers.lessThan(5000L))
                .build();

        responseSpecificationNotAuth = new ResponseSpecBuilder()
                .expectStatusCode(401)
                .expectContentType(ContentType.JSON)
                .expectResponseTime(Matchers.lessThan(5000L))
                .expectBody("message", containsString("You are not authorized"))
                .build();

        responseSpecificationFormParam = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .expectResponseTime(Matchers.lessThan(9000L))
                .expectBody("confidence", greaterThan(0.5F))
                .build();
    }

    @BeforeAll
    static void initTest() throws IOException {
        configFile = new FileInputStream("src/main/resources/prop.properties");
        prop.load(configFile);

        apiKey =  prop.getProperty("apiKey");
        baseUrl= prop.getProperty("base_url");

        createSpecs();

    }

    @BeforeAll
    static void setUpLogger(){
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    public static String getApiKey() {
        return apiKey;
    }

    public static String getBaseUrl() {
        return baseUrl;
    }

    public static RequestSpecification getRequestSpec() {
        return requestSpecification;
    }

    public static RequestSpecification getRequestSpecNotAuth() {
        return requestSpecificationNotAuth;
    }

    public static RequestSpecification getRequestSpecFormParam() {
        return requestSpecificationFormParam;
    }

    public static ResponseSpecification getResponseSpec() {
        return responseSpecification;
    }

    public static ResponseSpecification getResponseSpecNotAuth() {
        return responseSpecificationNotAuth;
    }

    public static ResponseSpecification getResponseSpecFormParam() {
        return responseSpecificationFormParam;
    }
}
