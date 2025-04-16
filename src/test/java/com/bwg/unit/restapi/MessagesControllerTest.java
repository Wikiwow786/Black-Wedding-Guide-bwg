package com.bwg.unit.restapi;

import com.bwg.config.MethodSecurityConfig;
import com.bwg.model.AuthModel;
import com.bwg.model.MessagesModel;
import com.bwg.repository.UsersRepository;
import com.bwg.restapi.MessagesController;
import com.bwg.service.MessagesService;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MessagesController.class)
@Import({TestConfig.class, MethodSecurityConfig.class})
public class MessagesControllerTest extends BaseControllerTest{
    @MockBean
    private MessagesService messagesService;
    @Autowired
    private MockMvc mockMvc;


    @Test
    @WithMockUser(authorities = {"ROLE_USER"})
    void createMessage_ShouldReturn201() throws Exception {
        MessagesModel requestModel = new MessagesModel();
        requestModel.setSenderId(1L);
        requestModel.setReceiverId(2L);
        requestModel.setConversationId("conv-123");
        requestModel.setContent("Hello!");
        requestModel.setSentAt(OffsetDateTime.now().withNano(0));
        requestModel.setSenderName("John Doe");
        requestModel.setReceiverName("Jane Smith");
        doReturn(requestModel)
                .when(messagesService)
                .createMessage(any(MessagesModel.class), any(AuthModel.class));

        mockMvc.perform(post("/messages")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "sender_id": 1,
                          "receiver_id": 2,
                          "conversation_id": "conv-123",
                          "content": "Hello!",
                          "sent_at": "2025-04-15T15:00:00Z",
                          "sender_name": "John Doe",
                          "receiver_name": "Jane Smith"
                        }
                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sender_id").value(1))
                .andExpect(jsonPath("$.receiver_id").value(2))
                .andExpect(jsonPath("$.conversation_id").value("conv-123"))
                .andExpect(jsonPath("$.content").value("Hello!"))
                .andExpect(jsonPath("$.sender_name").value("John Doe"))
                .andExpect(jsonPath("$.receiver_name").value("Jane Smith"))
                .andDo(print());
    }


    @Test
    @WithMockUser(authorities = {"ROLE_USER"})
    void getAllMessagesByUserId_ShouldReturn200() throws Exception {
        MessagesModel msg = new MessagesModel();
        msg.setMessageId(1L);
        msg.setSenderId(1L);
        msg.setReceiverId(2L);
        msg.setContent("Hi!");
        msg.setConversationId("conv-001");
        msg.setSentAt(OffsetDateTime.now().withNano(0));
        msg.setSenderName("John");
        msg.setReceiverName("Jane");

        doReturn(List.of(msg))
                .when(messagesService)
                .getAllMessagesByUserId(eq(1L));

        mockMvc.perform(get("/messages/conversation/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].message_id").value(1))
                .andExpect(jsonPath("$[0].content").value("Hi!"))
                .andExpect(jsonPath("$[0].sender_id").value(1))
                .andExpect(jsonPath("$[0].receiver_id").value(2))
                .andExpect(jsonPath("$[0].conversation_id").value("conv-001"))
                .andExpect(jsonPath("$[0].sender_name").value("John"))
                .andExpect(jsonPath("$[0].receiver_name").value("Jane"))
                .andDo(print());
    }


    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"}) // invalid role
    void accessWithInvalidRole_ShouldReturn403() throws Exception {
        mockMvc.perform(get("/messages/conversation/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
