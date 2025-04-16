package com.bwg.unit.restapi;

import com.bwg.config.MethodSecurityConfig;
import com.bwg.domain.Bookings;
import com.bwg.domain.Payments;
import com.bwg.exception.ResourceNotFoundException;
import com.bwg.model.PaymentsModel;
import com.bwg.restapi.MediaController;
import com.bwg.restapi.PaymentsController;
import com.bwg.service.MediaService;
import com.bwg.service.PaymentsService;
import com.bwg.unit.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PaymentsController.class)
@Import({TestConfig.class, MethodSecurityConfig.class})
public class PaymentsControllerTest extends BaseControllerTest{
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentsService paymentsService;

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void getPaymentById_ShouldReturn200_WhenAuthorized() throws Exception {
        Payments payment = new Payments();
        Bookings bookings = new Bookings();
        bookings.setBookingId(2L);
        bookings.setStatus(Bookings.BookingStatus.confirmed);
        payment.setPaymentId(1L);
        payment.setAmount(1000.00);
        payment.setBooking(bookings);


        doReturn(payment)
                .when(paymentsService).getPaymentById(1L);

        mockMvc.perform(get("/payments/{paymentId}",1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payment_id").value(1L))
                .andExpect(jsonPath("$.amount").value(1000.00))
                .andDo(print());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_COUPLE"})
    void getPaymentById_ShouldReturn403_WhenUnauthorizedRole() throws Exception {
        mockMvc.perform(get("/payments/{paymentId}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void getPaymentById_ShouldReturn404_WhenNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Payment not found"))
                .when(paymentsService).getPaymentById(99L);

        mockMvc.perform(get("/payments/{paymentId}", 99L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Payment not found"))
                .andDo(print());
    }


    @Test
    @WithMockUser(authorities = {"ROLE_OWNER"})
    void createPayment_ShouldReturn201_WhenOwner() throws Exception {
        PaymentsModel request = new PaymentsModel();
        request.setAmount(1500.0);
        request.setBookingId(2L);

        Bookings bookings = new Bookings();
        bookings.setBookingId(2L);
        bookings.setStatus(Bookings.BookingStatus.confirmed);
        Payments domainPayment = new Payments();
        domainPayment.setPaymentId(1L);
        domainPayment.setAmount(1500.0);
        domainPayment.setBooking(bookings);

        doReturn(domainPayment)
                .when(paymentsService).createPayment(any(PaymentsModel.class));

        mockMvc.perform(post("/payments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "amount": 1500.0,
                          "booking_id": 2
                        }
                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.payment_id").value(1))
                .andExpect(jsonPath("$.amount").value(1500.0))
                .andDo(print());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_COUPLE"})
    void createPayment_ShouldReturn403_WhenInvalidRole() throws Exception {
        mockMvc.perform(post("/payments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "amount": 1500.0,
                          "booking_id": 2
                        }
                    """))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void updatePayment_ShouldReturn200_WhenAuthorized() throws Exception {
        Bookings bookings = new Bookings();
        bookings.setBookingId(2L);
        bookings.setStatus(Bookings.BookingStatus.confirmed);

        Payments updated = new Payments();
        updated.setPaymentId(1L);
        updated.setAmount(2000.0);
        updated.setBooking(bookings);

        doReturn(updated).when(paymentsService).updatePayment(eq(1L), any(PaymentsModel.class));

        mockMvc.perform(put("/payments/{paymentId}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "amount": 2000.0,
                          "booking_id": 2
                        }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payment_id").value(1))
                .andExpect(jsonPath("$.amount").value(2000.0))
                .andDo(print());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_COUPLE"})
    void updatePayment_ShouldReturn403_WhenUnauthorizedRole() throws Exception {
        mockMvc.perform(put("/payments/{paymentId}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "amount": 2000.0,
                          "booking_id": 2
                        }
                    """))
                .andExpect(status().isForbidden())
                .andDo(print());
    }


}
