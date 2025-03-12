package com.bwg.service.impl;

import com.bwg.domain.Messages;
import com.bwg.domain.Payments;
import com.bwg.domain.QMessages;
import com.bwg.exception.ResourceNotFoundException;
import com.bwg.model.AuthModel;
import com.bwg.model.MessagesModel;
import com.bwg.repository.MessagesRepository;
import com.bwg.repository.UsersRepository;
import com.bwg.service.MessagesService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
    public Page<Messages> getAllMessages(String search,String conversationId,AuthModel authModel,Pageable pageable) {
        info(LOG_SERVICE_OR_REPOSITORY, "Fetching All Messages", this);
        BooleanBuilder filter = new BooleanBuilder();
        BooleanExpression senderCondition = QMessages.messages.sender.userId.eq(Long.parseLong(authModel.userId()));
        BooleanExpression receiverCondition = QMessages.messages.receiver.userId.eq(Long.parseLong(authModel.userId()));
        filter.and(senderCondition.or(receiverCondition));

        if(conversationId != null){
            filter.and(QMessages.messages.conversationId.eq(conversationId));
        }
        if(StringUtils.isNotBlank(search)){
            filter.and(QMessages.messages.content.containsIgnoreCase(search));
        }

        return messagesRepository.findAll(filter,pageable);
    }

    @Override
    public Messages createMessage(MessagesModel messagesModel) {
        info(LOG_SERVICE_OR_REPOSITORY, format("Creating Message..."), this);

        Messages messages = new Messages();

        BeanUtils.copyProperties(messagesModel, messages);
        messages.setSender(usersRepository.findById(messagesModel.getSenderId())
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found")));
        messages.setReceiver(usersRepository.findById(messagesModel.getReceiverId())
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found")));

        messages.setSentAt(OffsetDateTime.now());
        return messagesRepository.save(messages);
    }

    @Override
    public List<Messages> getAllMessagesByUserId(Long userId) {
        info(LOG_SERVICE_OR_REPOSITORY, "Fetching All Message by Id {0}", userId);
        return messagesRepository.findAllBySender_UserId(userId);
    }
}
