package com.bwg.unit.restapi;
import com.bwg.config.MethodSecurityConfig;
import com.bwg.domain.Bookings;

import com.bwg.domain.Services;
import com.bwg.domain.Users;
import com.bwg.exception.ResourceNotFoundException;
import com.bwg.model.AuthModel;

import com.bwg.model.BookingsModel;
import com.bwg.restapi.BookingsController;

import com.bwg.service.BookingsService;
import com.bwg.unit.config.TestConfig;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@WebMvcTest(controllers = BookingsController.class)
@Import({TestConfig.class, MethodSecurityConfig.class})
class BookingControllerTest extends BaseControllerTest{

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingsService bookingsService;

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void getBookingById_ShouldReturn200_WhenAuthorized() throws Exception {
        Bookings booking = new Bookings();
        Users users = new Users();
        Services services = new Services();
        users.setUserId(1L);
        users.setFirstName("Adil");
        users.setLastName("Waheed");
        services.setServiceName("Luxury Wedding");
        services.setServiceId(1L);
        booking.setBookingId(1L);
        booking.setStatus(Bookings.BookingStatus.confirmed);
        booking.setUser(users);
        booking.setService(services);

        doReturn(booking).when(bookingsService).getBookingById(eq(1L), any(AuthModel.class));

        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.booking_id").value(1L))
                .andExpect(jsonPath("$.status").value("confirmed"))
                .andDo(print());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_VENDOR"})
    void getBookingById_ShouldReturn404_WhenBookingNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Booking not found"))
                .when(bookingsService).getBookingById(eq(99L), any(AuthModel.class));

        mockMvc.perform(get("/bookings/{bookingId}", 99L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Booking not found"))
                .andDo(print());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_COUPLE"})
    void getBookingById_ShouldReturn403_WhenUnauthorizedRole() throws Exception {
        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_COUPLE"})
    void createBooking_ShouldReturn201_WhenRoleCouple() throws Exception {
        Users user = new Users();
        user.setUserId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");

        Services service = new Services();
        service.setServiceId(2L);

        Bookings booking = new Bookings();
        booking.setBookingId(1L);
        booking.setUser(user);
        booking.setService(service);
        booking.setEventDate(OffsetDateTime.now());
        booking.setStatus(Bookings.BookingStatus.confirmed);
        booking.setCreatedAt(OffsetDateTime.now());
        booking.setUpdatedAt(OffsetDateTime.now());

        doReturn(booking)
                .when(bookingsService).createBooking(any(BookingsModel.class), any(AuthModel.class));

        mockMvc.perform(post("/bookings")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "booking_id": 1,
                          "status": "confirmed",
                          "user_id": 1,
                          "service_id": 2
                        }
                    """)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.booking_id").value(1L))
                .andExpect(jsonPath("$.user_name").value("John Doe"))
                .andExpect(jsonPath("$.service_id").value(2L))
                .andExpect(jsonPath("$.status").value("confirmed"))
                .andExpect(jsonPath("$.created_at").exists())
                .andDo(print());
    }


    @Test
    @WithMockUser(authorities = {"ROLE_VENDOR"})
    void createBooking_ShouldReturn403_WhenNotCouple() throws Exception {
        mockMvc.perform(post("/bookings")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "booking_id": 1,
                              "status": "confirmed",
                              "user_id": 1,
                              "service_id": 2
                            }
                            """)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_VENDOR"})
    void updateBooking_ShouldReturn200_WhenAuthorized() throws Exception {
        Users user = new Users();
        user.setUserId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");

        Services service = new Services();
        service.setServiceId(2L);

        Bookings booking = new Bookings();
        booking.setBookingId(1L);
        booking.setUser(user);
        booking.setService(service);
        booking.setEventDate(OffsetDateTime.now());
        booking.setStatus(Bookings.BookingStatus.confirmed);
        booking.setCreatedAt(OffsetDateTime.now());
        booking.setUpdatedAt(OffsetDateTime.now());

        doReturn(booking)
                .when(bookingsService).updateBooking(eq(1L), any(BookingsModel.class));

        mockMvc.perform(put("/bookings/{bookingId}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "status": "confirmed",
                          "user_id": 1,
                          "service_id": 2
                        }
                    """)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.booking_id").value(1))
                .andExpect(jsonPath("$.user_name").value("John Doe"))
                .andExpect(jsonPath("$.status").value("confirmed"))
                .andDo(print());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_COUPLE"})
    void updateBooking_ShouldReturn403_WhenUnauthorizedRole() throws Exception {
        mockMvc.perform(put("/bookings/{bookingId}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "status": "confirmed",
                          "user_id": 1,
                          "service_id": 2
                        }
                    """)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void updateBooking_ShouldReturn404_WhenNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Booking not found"))
                .when(bookingsService).updateBooking(eq(999L), any(BookingsModel.class));

        mockMvc.perform(put("/bookings/{bookingId}", 999L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "status": "confirmed",
                          "user_id": 1,
                          "service_id": 2
                        }
                    """)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Booking not found"))
                .andDo(print());
    }
    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void deleteBooking_ShouldReturn204_WhenAuthorized() throws Exception {
        mockMvc.perform(delete("/bookings/{bookingId}", 1L)
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());

        verify(bookingsService).deleteBooking(1L);
    }
    @Test
    @WithMockUser(authorities = {"ROLE_COUPLE"})
    void deleteBooking_ShouldReturn403_WhenUnauthorizedRole() throws Exception {
        mockMvc.perform(delete("/bookings/{bookingId}", 1L)
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andDo(print());
    }
    @Test
    @WithMockUser(authorities = {"ROLE_OWNER"})
    void deleteBooking_ShouldReturn404_WhenNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Booking not found"))
                .when(bookingsService).deleteBooking(999L);

        mockMvc.perform(delete("/bookings/{bookingId}", 999L)
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Booking not found"))
                .andDo(print());
    }



}

