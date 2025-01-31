package tests;

import io.restassured.http.ContentType;
import models.Booking;
import models.BookingDates;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class BookerApiTests {
    private String token;
    private int bookingId;

    @BeforeClass
    public void setup() {
        baseURI = "https://restful-booker.herokuapp.com";

        token = given()
                .contentType(ContentType.JSON)
                .body("{ \"username\": \"admin\", \"password\": \"password123\"}")
                .when()
                .post("/auth")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }

    @Test(priority = 1)
    public void createBookingTest() {
        Booking booking = new Booking();
        booking.setFirstname("Silas");
        booking.setLastname("Hayri");
        booking.setTotalprice(1000);
        booking.setDepositpaid(true);

        BookingDates dates = new BookingDates();
        dates.setCheckin("2025-05-01");
        dates.setCheckout("2025-05-05");
        booking.setBookingdates(dates);

        booking.setAdditionalneeds("Breakfast");

        bookingId = given()
                .contentType(ContentType.JSON)
                .body(booking)
                .when()
                .post("/booking")
                .then()
                .statusCode(200)
                .body("booking.firstname", equalTo("Silas"))
                .body("booking.lastname", equalTo("Hayri"))
                .extract()
                .path("bookingid");
    }

    @Test(priority = 2)
    public void getBookingTest() {
        given()
                .when()
                .get("/booking/" + bookingId)
                .then()
                .statusCode(200)
                .body("firstname", equalTo("Silas"))
                .body("lastname", equalTo("Hayri"));
    }

    @Test(priority = 3)
    public void updateBookingTest() {
        Booking updatedBooking = new Booking();
        updatedBooking.setFirstname("Serdar");
        updatedBooking.setLastname("Hayri");
        updatedBooking.setTotalprice(1200);
        updatedBooking.setDepositpaid(true);

        BookingDates dates = new BookingDates();
        dates.setCheckin("2025-05-01");
        dates.setCheckout("2025-05-05");
        updatedBooking.setBookingdates(dates);

        updatedBooking.setAdditionalneeds("Breakfast and Dinner");

        given()
                .contentType(ContentType.JSON)
                .header("Cookie", "token=" + token)
                .body(updatedBooking)
                .when()
                .put("/booking/" + bookingId)
                .then()
                .statusCode(200)
                .body("firstname", equalTo("Serdar"));
    }

    @Test(priority = 4)
    public void deleteBookingTest() {
        given()
                .header("Cookie", "token=" + token)
                .when()
                .delete("/booking/" + bookingId)
                .then()
                .statusCode(201);
    }
} 