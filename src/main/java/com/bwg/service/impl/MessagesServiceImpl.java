package com.bwg.service.impl;

import com.bwg.domain.Messages;
import com.bwg.domain.Payments;
import com.bwg.exception.ResourceNotFoundException;
import com.bwg.model.MessagesModel;
import com.bwg.repository.MessagesRepository;
import com.bwg.repository.UsersRepository;
import com.bwg.service.MessagesService;
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
    public Page<Messages> getAllMessages(Pageable pageable) {
        info(LOG_SERVICE_OR_REPOSITORY, "Fetching All Messages", this);
        return messagesRepository.findAll(pageable);
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
