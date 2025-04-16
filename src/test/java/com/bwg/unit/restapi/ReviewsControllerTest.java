package com.bwg.unit.restapi;

import com.bwg.config.MethodSecurityConfig;
import com.bwg.domain.Reviews;
import com.bwg.domain.Services;
import com.bwg.domain.Users;
import com.bwg.exception.ResourceNotFoundException;
import com.bwg.model.ReviewsModel;
import com.bwg.restapi.ReviewsController;
import com.bwg.service.ReviewsService;
import com.bwg.unit.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ReviewsController.class)
@Import({TestConfig.class, MethodSecurityConfig.class})
public class ReviewsControllerTest extends BaseControllerTest{
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewsService reviewsService;

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void getReviewById_ShouldReturn200_WhenExists() throws Exception {
        Reviews review = new Reviews();
        Users users = new Users();
        users.setUserId(2L);
        users.setFirstName("Adil");
        users.setLastName("Waheed");
        Services services = new Services();
        services.setServiceId(3L);
        services.setServiceName("Luxury Wedding Cake");
        review.setReviewId(1L);
        review.setRating(5);
        review.setUser(users);
        review.setService(services);
        review.setComment("Excellent service");

        doReturn(review).when(reviewsService).getReviewById(1L);

        mockMvc.perform(get("/reviews/{reviewId}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.review_id").value(1))
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.comment").value("Excellent service"))
                .andDo(print());
    }

    @Test
    @WithMockUser
    void getReviewById_ShouldReturn404_WhenNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Review not found"))
                .when(reviewsService).getReviewById(99L);

        mockMvc.perform(get("/reviews/{reviewId}", 99L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Review not found"))
                .andDo(print());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_COUPLE"})
    void createReview_ShouldReturn201_WhenCouple() throws Exception {
        Reviews review = new Reviews();
        Users users = new Users();
        users.setUserId(2L);
        users.setFirstName("Adil");
        users.setLastName("Waheed");
        Services services = new Services();
        services.setServiceId(3L);
        services.setServiceName("Luxury Wedding Cake");
        review.setReviewId(1L);
        review.setRating(5);
        review.setUser(users);
        review.setService(services);
        review.setComment("Great service!");

        doReturn(review).when(reviewsService).createReview(any(ReviewsModel.class));

        mockMvc.perform(post("/reviews")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "content": "Great service!"
                        }
                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.review_id").value(1))
                .andExpect(jsonPath("$.comment").value("Great service!"))
                .andDo(print());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void createReview_ShouldReturn403_WhenUnauthorized() throws Exception {
        mockMvc.perform(post("/reviews")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "content": "Nice!"
                        }
                    """))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void updateReview_ShouldReturn200_WhenAuthorized() throws Exception {
        Reviews updatedReview = new Reviews();
        Users users = new Users();
        users.setUserId(2L);
        users.setFirstName("Adil");
        users.setLastName("Waheed");
        Services services = new Services();
        services.setServiceId(3L);
        services.setServiceName("Luxury Wedding Cake");
        updatedReview.setUser(users);
        updatedReview.setService(services);
        updatedReview.setReviewId(1L);
        updatedReview.setComment("Updated review");

        doReturn(updatedReview).when(reviewsService).updateReview(eq(1L), any(ReviewsModel.class));

        mockMvc.perform(put("/reviews/{reviewId}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "comment": "Updated review"
                        }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.review_id").value(1L))
                .andExpect(jsonPath("$.comment").value("Updated review"))
                .andDo(print());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_COUPLE"})
    void updateReview_ShouldReturn403_WhenUnauthorizedRole() throws Exception {
        mockMvc.perform(put("/reviews/{reviewId}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "content": "Attempted update"
                        }
                    """))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_VENDOR"})
    void updateReview_ShouldReturn404_WhenNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Review not found"))
                .when(reviewsService).updateReview(eq(99L), any(ReviewsModel.class));

        mockMvc.perform(put("/reviews/{reviewId}", 99L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "content": "Some update"
                        }
                    """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Review not found"))
                .andDo(print());
    }

}
