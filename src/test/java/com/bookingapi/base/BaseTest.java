package com.bookingapi.base;

import com.bookingapi.api.BookingAPI;
import com.bookingapi.helpers.ApiClient;
import org.junit.jupiter.api.BeforeEach;

public abstract class BaseTest {

    private static final String BASE_URL = "https://restful-booker.herokuapp.com";

    protected ApiClient apiClient;
    protected BookingAPI bookingAPI;

    @BeforeEach
    void setUp() {
        apiClient = new ApiClient(BASE_URL);
        bookingAPI = new BookingAPI(apiClient);
    }
}
