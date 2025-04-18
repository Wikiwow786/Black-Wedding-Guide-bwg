package com.bwg.unit.restapi;

import com.bwg.exception.ResourceNotFoundException;
import com.bwg.model.AuthModel;
import com.bwg.model.BookmarkModel;
import com.bwg.resolver.AuthPrincipalResolver;
import com.bwg.restapi.BookmarkController;
import com.bwg.service.BookmarkService;
import com.bwg.unit.config.TestConfig;
import com.bwg.unit.service.util.TestDataFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;


@WebMvcTest(controllers = BookmarkController.class)
@Import(TestConfig.class)
class BookmarkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookmarkService bookmarkService;

    @MockBean
    private AuthPrincipalResolver authPrincipalResolver;

    private AuthModel mockAuth;

    @BeforeEach
    void setUp() {
        mockAuth = TestDataFactory.buildAuthModel("123", "ROLE_ADMIN");

        when(authPrincipalResolver.supportsParameter(argThat(
                param -> param.getParameterType().equals(AuthModel.class)
        ))).thenReturn(true);

        when(authPrincipalResolver.resolveArgument(any(), any(), any(), any()))
                .thenReturn(mockAuth);
    }

    @AfterEach
    void resetMocks() {
        Mockito.reset(bookmarkService, authPrincipalResolver);
    }

    @Test
    @WithMockUser
    void getByUser_ShouldReturnBookmarksList() throws Exception {
        BookmarkModel bookmark = new BookmarkModel(
                1L,
                123L,
                "My Bookmark",
                "http://img.jpg",
                "UID123",
                OffsetDateTime.now().withNano(0)
        );

        List<BookmarkModel> bookmarks = List.of(bookmark);

        doReturn(bookmarks)
                .when(bookmarkService)
                .getUserBookmarks(any(AuthModel.class));

        mockMvc.perform(get("/bookmarks/user/42")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookMarkId").value(1L))
                .andExpect(jsonPath("$[0].userId").value(123L))
                .andExpect(jsonPath("$[0].title").value("My Bookmark"))
                .andExpect(jsonPath("$[0].imageUrl").value("http://img.jpg"))
                .andExpect(jsonPath("$[0].uBookMarkId").value("UID123"))
                .andExpect(jsonPath("$[0].createdAt").exists()) // or .value(...) if fixed
                .andDo(print());
    }

    @Test
    @WithMockUser
    void createBookmark_ShouldReturnCreatedBookmark() throws Exception {
        BookmarkModel request = new BookmarkModel(
                1L,
                123L,
                "My Bookmark",
                "http://img.jpg",
                "UID123",
                OffsetDateTime.now().withNano(0)
        );

        doReturn(request)
                .when(bookmarkService)
                .createBookmark(any(BookmarkModel.class), any(AuthModel.class));

        mockMvc.perform(post("/bookmarks")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "bookMarkId": 1,
                          "userId": 123,
                          "title": "My Bookmark",
                          "imageUrl": "http://img.jpg",
                          "uBookMarkId": "UID123",
                          "createdAt": "2025-04-15T15:00:00Z"
                        }
                    """)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookMarkId").value(1))
                .andExpect(jsonPath("$.title").value("My Bookmark"))
                .andExpect(jsonPath("$.imageUrl").value("http://img.jpg"))
                .andExpect(jsonPath("$.uBookMarkId").value("UID123"))
                .andExpect(jsonPath("$.userId").value(123))
                .andExpect(jsonPath("$.createdAt").exists())
                .andDo(print());
    }

    @Test
    @WithMockUser
    void deleteBookmark_ShouldReturn204_NoContent() throws Exception {
        mockMvc.perform(delete("/bookmarks/1")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    @WithMockUser
    void deleteUserBookmarks_ShouldReturn204_NoContent() throws Exception {

        mockMvc.perform(delete("/bookmarks/user/123")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void deleteBookmark_WhenNotFound_ShouldReturn404() throws Exception {
        Mockito.doThrow(new ResourceNotFoundException("Bookmark not found"))
                .when(bookmarkService)
                .deleteBookmark(999L);
        mockMvc.perform(delete("/bookmarks/999")
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Bookmark not found"))
                .andDo(print());
    }


}
