package com.bookingapi.tests;

import com.bookingapi.base.BaseTest;
import com.bookingapi.model.request.AuthRequest;
import com.bookingapi.model.request.BookingDatesRequest;
import com.bookingapi.model.request.BookingPatchRequest;
import com.bookingapi.model.request.BookingRequest;
import com.bookingapi.model.response.AuthResponse;
import com.bookingapi.model.response.BookingCreatedResponse;
import com.bookingapi.model.response.BookingResponse;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookingE2ETest extends BaseTest {

    @Test
    @Epic("Booking API")
    @Feature("Booking lifecycle")
    @Story("Create, read, update, partially update, and delete a booking")
    @DisplayName("Complete booking lifecycle")
    @Description("Verifies the booking CRUD lifecycle and confirms a deleted booking is unavailable.")
    void shouldCreateReadUpdatePatchAndDeleteBooking() {
        AuthResponse auth = Allure.step("Create authentication token", () -> bookingAPI.createToken(
                        AuthRequest.builder()
                                .username("admin")
                                .password("password123")
                                .build())
                .then()
                .statusCode(200)
                .extract()
                .as(AuthResponse.class));

        assertNotNull(auth.getToken(), "Authentication should return a token");

        Integer bookingId = null;
        boolean deleted = false;

        try {
            LocalDate checkin = LocalDate.now().plusDays(7);
            BookingRequest originalBooking = BookingRequest.builder()
                    .firstname("E2E")
                    .lastname("Guest")
                    .totalprice(250)
                    .depositpaid(true)
                    .bookingdates(BookingDatesRequest.builder()
                            .checkin(checkin.toString())
                            .checkout(checkin.plusDays(3).toString())
                            .build())
                    .additionalneeds("Breakfast")
                    .build();

            BookingCreatedResponse created = Allure.step("Create booking", () -> bookingAPI.createBooking(originalBooking)
                    .then()
                    .statusCode(200)
                    .extract()
                    .as(BookingCreatedResponse.class));

            int createdBookingId = created.getBookingid();
            bookingId = createdBookingId;
            assertTrue(createdBookingId > 0, "Create response should contain a booking ID");

            BookingResponse initial = Allure.step("Verify created booking", () -> bookingAPI.getBooking(createdBookingId)
                    .then()
                    .statusCode(200)
                    .extract()
                    .as(BookingResponse.class));
            assertBookingEquals(originalBooking, initial);

            BookingRequest updatedBooking = BookingRequest.builder()
                    .firstname("E2E Updated")
                    .lastname(originalBooking.getLastname())
                    .totalprice(originalBooking.getTotalprice())
                    .depositpaid(originalBooking.isDepositpaid())
                    .bookingdates(originalBooking.getBookingdates())
                    .additionalneeds(originalBooking.getAdditionalneeds())
                    .build();

            Allure.step("Update booking with PUT", () -> bookingAPI.updateBooking(createdBookingId, updatedBooking, auth.getToken())
                    .then()
                    .statusCode(200));

            BookingResponse afterPut = Allure.step("Verify PUT update", () -> bookingAPI.getBooking(createdBookingId)
                    .then()
                    .statusCode(200)
                    .extract()
                    .as(BookingResponse.class));
            assertBookingEquals(updatedBooking, afterPut);

            BookingPatchRequest patch = BookingPatchRequest.builder()
                    .additionalneeds("Late checkout")
                    .build();

            Allure.step("Partially update booking with PATCH", () -> bookingAPI.partiallyUpdateBooking(createdBookingId, patch, auth.getToken())
                    .then()
                    .statusCode(200));

            BookingResponse afterPatch = Allure.step("Verify PATCH update and unchanged fields",
                    () -> bookingAPI.getBooking(createdBookingId)
                    .then()
                    .statusCode(200)
                    .extract()
                    .as(BookingResponse.class));
            assertEquals("Late checkout", afterPatch.getAdditionalneeds());
            assertEquals(updatedBooking.getFirstname(), afterPatch.getFirstname());
            assertEquals(updatedBooking.getLastname(), afterPatch.getLastname());
            assertEquals(updatedBooking.getTotalprice(), afterPatch.getTotalprice());
            assertEquals(updatedBooking.isDepositpaid(), afterPatch.isDepositpaid());
            assertEquals(updatedBooking.getBookingdates().getCheckin(),
                    afterPatch.getBookingdates().getCheckin());
            assertEquals(updatedBooking.getBookingdates().getCheckout(),
                    afterPatch.getBookingdates().getCheckout());

            Response deleteResponse = Allure.step("Delete booking", () -> bookingAPI.deleteBooking(createdBookingId, auth.getToken()));
            assertEquals(201, deleteResponse.statusCode(), "Delete should succeed");
            deleted = true;

            Allure.step("Verify deleted booking is unavailable", () -> bookingAPI.getBooking(createdBookingId)
                    .then()
                    .statusCode(404));
        } finally {
            if (bookingId != null && !deleted) {
                assertEquals(201, bookingAPI.deleteBooking(bookingId, auth.getToken()).statusCode(),
                        "Cleanup should delete the created booking");
            }
        }
    }

    private void assertBookingEquals(BookingRequest expected, BookingResponse actual) {
        assertEquals(expected.getFirstname(), actual.getFirstname());
        assertEquals(expected.getLastname(), actual.getLastname());
        assertEquals(expected.getTotalprice(), actual.getTotalprice());
        assertEquals(expected.isDepositpaid(), actual.isDepositpaid());
        assertEquals(expected.getBookingdates().getCheckin(),
                actual.getBookingdates().getCheckin());
        assertEquals(expected.getBookingdates().getCheckout(),
                actual.getBookingdates().getCheckout());
        assertEquals(expected.getAdditionalneeds(), actual.getAdditionalneeds());
    }
}
