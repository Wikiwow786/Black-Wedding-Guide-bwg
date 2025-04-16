package com.bwg.service.impl;

import com.bwg.domain.Messages;
import com.bwg.exception.ResourceNotFoundException;
import com.bwg.model.AuthModel;
import com.bwg.model.MessagesModel;
import com.bwg.projection.MessagesProjection;
import com.bwg.repository.MessagesRepository;
import com.bwg.repository.UsersRepository;
import com.bwg.service.MessagesService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

import static com.bwg.logger.Logger.format;
import static com.bwg.logger.Logger.info;
import static com.bwg.logger.LoggingEvent.LOG_SERVICE_OR_REPOSITORY;

@Service
public class MessagesServiceImpl implements MessagesService {

    @Autowired
    private MessagesRepository messagesRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Override
    public Page<MessagesModel> getAllMessages(String search, String conversationId, AuthModel authModel, Pageable pageable) {
        info(LOG_SERVICE_OR_REPOSITORY, "Fetching All Messages", this);

        Long userId = Long.parseLong(authModel.userId());

        Page<MessagesProjection> messagesPage = messagesRepository.findMessages(conversationId, userId, pageable);

        List<MessagesModel> messagesModelList = messagesPage.getContent().stream()
                .map(MessagesModel::new)
                .toList();

        return new PageImpl<>(messagesModelList, pageable, messagesPage.getTotalElements());
    }

    @Override
    public MessagesModel createMessage(MessagesModel messagesModel, AuthModel authModel) {
        info(LOG_SERVICE_OR_REPOSITORY, format("Creating Message..."), this);

        Messages messages = new Messages();

        BeanUtils.copyProperties(messagesModel, messages);
        messages.setSender(usersRepository.findById(Long.parseLong(authModel.userId()))
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found")));
        messages.setReceiver(usersRepository.findById(messagesModel.getReceiverId())
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found")));

        messages.setSentAt(OffsetDateTime.now());
        return new MessagesModel(messagesRepository.save(messages));
    }

    @Override
    public List<MessagesModel> getAllMessagesByUserId(Long userId) {
        info(LOG_SERVICE_OR_REPOSITORY, "Fetching All Message by Id {0}", userId);
        return messagesRepository.findAllBySender_UserId(userId).stream().map(MessagesModel::new).toList();
    }
}