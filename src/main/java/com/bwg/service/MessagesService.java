package com.bwg.service;

import com.bwg.domain.Messages;
import com.bwg.model.MessagesModel;

import java.util.List;

public interface MessagesService {
    List<Messages> getAllMessages();

    Messages createMessage(MessagesModel messagesModel);

    List<Messages> getAllMessagesByUserId(Long userId);
}
