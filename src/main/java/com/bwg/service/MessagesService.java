package com.bwg.service;

import com.bwg.domain.Messages;
import com.bwg.domain.Payments;
import com.bwg.model.AuthModel;
import com.bwg.model.MessagesModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MessagesService {
    Page<MessagesModel> getAllMessages(String search, String conversationId, AuthModel authModel, Pageable pageable);

    MessagesModel createMessage(MessagesModel messagesModel, AuthModel authModel);

    List<MessagesModel> getAllMessagesByUserId(Long userId);
}
