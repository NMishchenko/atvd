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

import java.util.*;

public class RestfulBookerApiTests {
    private final String BASE_URL = "https://restful-booker.herokuapp.com";
    private final String AUTH = "/auth";
    private final String BOOKING = "/booking";
    private final String BOOKING_FILTER = "/booking?firstname={fn}&lastname={ln}";
    private final String BOOKING_ID = BOOKING + "/{id}";
    private final String FIRST_NAME = "Mykyta";
    private final String LAST_NAME = "Mishchenko";
    private final String ADMIN_NAME = "admin";
    private final String ADMIN_PASSWORD = "password123";
    private String authToken;
    private ArrayList ids;

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.defaultParser = Parser.JSON;
        RestAssured.requestSpecification = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        RestAssured.responseSpecification = new ResponseSpecBuilder().build();
    }

    // POST
    @Test
    public void verifyLogin() {
        Map<String, String> body = Map.of(
                "username", ADMIN_NAME,
                "password", ADMIN_PASSWORD);
        Response response = RestAssured.given()
                .body(body)
                .post(AUTH);
        response.then().statusCode(HttpStatus.SC_OK);
        authToken = response.jsonPath().get("token");
        System.out.printf("\n%s\n", response.jsonPath().get().toString());
    }

    // POST
    @Test(dependsOnMethods = "verifyLogin")
    public void verifyCreateBooking() {
        Response response = RestAssured.given()
                .body(generateBookingInfo())
                .post(BOOKING);
        response.then().statusCode(HttpStatus.SC_OK);
        System.out.printf("\n%s\n", response.jsonPath().get().toString());
    }

    // GET
    @Test(dependsOnMethods = "verifyCreateBooking")
    public void verifyGetBooking() {
        Map<String, String> params = Map.of("fn", FIRST_NAME, "ln", LAST_NAME);
        Response response = RestAssured.given()
                .pathParams(params)
                .get(BOOKING_FILTER);
        response.then().statusCode(HttpStatus.SC_OK);
        ids = response.jsonPath().get("bookingid");
        System.out.printf("\n%s\n", response.jsonPath().get().toString());
    }

    // PUT
    @Test(dependsOnMethods = "verifyCreateBooking", priority = 1)
    public void verifyUpdateBooking() {
        Response response = RestAssured.given()
                .header("Cookie", "token=" + authToken)
                .body(generateBookingInfo())
                .pathParam("id", ids.get(0))
                .put(BOOKING_ID);
        response.then().statusCode(HttpStatus.SC_OK);
        System.out.printf("\n%s\n", response.jsonPath().get().toString());
    }

    private Map<String, ?> generateBookingInfo() {
        return  Map.of(
                "firstname", FIRST_NAME,
                "lastname", LAST_NAME,
                "totalprice", Faker.instance().number().numberBetween(10, 10000),
                "depositpaid", Faker.instance().bool().bool(),
                "bookingdates", Map.of("checkin", "2024-01-05", "checkout", "2024-08-05"),
                "additionalneeds", "Breakfast");
    }
}
