package com.bookingapi.api;

import com.bookingapi.helpers.ApiClient;
import com.bookingapi.model.request.AuthRequest;
import com.bookingapi.model.request.BookingPatchRequest;
import com.bookingapi.model.request.BookingRequest;
import io.restassured.response.Response;

import java.util.Map;
import java.util.Objects;

public class BookingAPI {

    private static final String BOOKINGS_ENDPOINT = "/booking";
    private static final String AUTH_ENDPOINT = "/auth";
    private static final String HEALTH_ENDPOINT = "/ping";

    private final ApiClient apiClient;

    public BookingAPI(ApiClient apiClient) {
        this.apiClient = Objects.requireNonNull(apiClient, "apiClient");
    }

    public Response getBookings(Map<String, String> queryParams) {
        return apiClient.getRequest(BOOKINGS_ENDPOINT, queryParams, null);
    }

    public Response getBooking(int bookingId) {
        return apiClient.getRequest(BOOKINGS_ENDPOINT + "/" + bookingId, null, null);
    }

    public Response createBooking(BookingRequest booking) {
        return apiClient.postRequest(BOOKINGS_ENDPOINT, booking, null);
    }

    public Response updateBooking(int bookingId, BookingRequest booking, String token) {
        return apiClient.putRequest(
                BOOKINGS_ENDPOINT + "/" + bookingId,
                booking,
                tokenHeader(token));
    }

    public Response partiallyUpdateBooking(int bookingId, BookingPatchRequest booking, String token) {
        return apiClient.patchRequest(
                BOOKINGS_ENDPOINT + "/" + bookingId,
                booking,
                tokenHeader(token));
    }

    public Response deleteBooking(int bookingId, String token) {
        return apiClient.deleteRequest(
                BOOKINGS_ENDPOINT + "/" + bookingId,
                tokenHeader(token));
    }

    public Response createToken(AuthRequest credentials) {
        return apiClient.postRequest(AUTH_ENDPOINT, credentials, null);
    }

    public Response healthCheck() {
        return apiClient.getRequest(HEALTH_ENDPOINT, null, null);
    }

    private Map<String, String> tokenHeader(String token) {
        return Map.of("Cookie", "token=" + Objects.requireNonNull(token, "token"));
    }
}
