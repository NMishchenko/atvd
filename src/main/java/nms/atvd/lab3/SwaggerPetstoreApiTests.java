package nms.atvd.lab3;

import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.annotations.*;

import java.util.Map;

import static org.hamcrest.Matchers.equalTo;

public class SwaggerPetstoreApiTests {
    private final String BASE_URL = "https://petstore.swagger.io/v2";
    private final String USER = "/user";
    private final String USER_USERNAME = USER + "/{username}";
    private final String USER_LOGIN = USER + "/login";
    private final String USER_LOGOUT = USER + "/logout";
    private String username = "NMishchenko";
    private String group = "122-20-2.12";
    private String firstName = "Mykyta";

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.defaultParser = Parser.JSON;
        RestAssured.requestSpecification = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        RestAssured.responseSpecification = new ResponseSpecBuilder().build();
    }

    @Test
    public void verifyLogin() {
        Map<String, ?> body = Map.of(
                "username", username,
                "password", group);

        Response response = RestAssured.given().body(body).get(USER_LOGIN);
        System.out.printf("\n%s\n", response.jsonPath().get().toString());

        response.then().statusCode(HttpStatus.SC_OK); //check status code
        String sessionId = response.jsonPath().get("message").toString().replaceAll("[^0-9]", "");
        RestAssured.requestSpecification.sessionId(sessionId);
    }

    @Test(dependsOnMethods = "verifyLogin")
    public void verifyCreateUser() {
        Map<String, ?> body = Map.of(
                "username", username,
                "firstName", firstName,
                "lastName", Faker.instance().gameOfThrones().character(),
                "email", Faker.instance().internet().emailAddress(),
                "password", Faker.instance().internet().password(),
                "phone", Faker.instance().phoneNumber().phoneNumber(),
                "userStatus", 1);

        Response response = RestAssured.given().body(body).post(USER);
        System.out.printf("\n%s\n", response.jsonPath().get().toString());
        response.then().statusCode(HttpStatus.SC_OK);
    }

    @Test(dependsOnMethods = "verifyCreateUser")
    public void verifyGetUserInfo() {
        Response response = RestAssured.given().pathParam("username", username).get(USER_USERNAME);
        System.out.printf("\n%s\n", response.jsonPath().get().toString());
        response.then().statusCode(HttpStatus.SC_OK).and().body("firstName", equalTo(firstName));
    }

    @Test(dependsOnMethods = "verifyGetUserInfo")
    public void verifyDeleteUser() {
        Response response = RestAssured.given().pathParam("username", username).delete(USER_USERNAME);
        System.out.printf("\n%s\n", response.jsonPath().get().toString());
        response.then().statusCode(HttpStatus.SC_OK);
    }

    @Test(dependsOnMethods = "verifyLogin", priority = 1)
    public void verifyLogout() {
        Response response = RestAssured.given().get(USER_LOGOUT);
        System.out.printf("\n%s\n", response.jsonPath().get().toString());
        response.then().statusCode(HttpStatus.SC_OK);
    }
}