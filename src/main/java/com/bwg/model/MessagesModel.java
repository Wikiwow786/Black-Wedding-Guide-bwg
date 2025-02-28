package com.bwg.model;

import com.bwg.domain.Messages;
import com.bwg.exception.ResourceNotFoundException;
import com.bwg.repository.MessagesRepository;
import com.bwg.util.BeanUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;

public class MessagesModel {

    private Long messageId;
    private String uMessageId;
    private Long senderId;
    private Long receiverId;
    private String conversationId;
    private String content;
    private OffsetDateTime sentAt;

    public MessagesModel() {
    }

    public MessagesModel(Messages messages) {
        this.messageId = messages.getMessageId();
        this.uMessageId = messages.getUMessageId();
        this.senderId = messages.getSender().getUserId();
        this.receiverId = messages.getReceiver().getUserId();
        this.conversationId = messages.getConversationId();
        this.content = messages.getContent();
        this.sentAt = messages.getSentAt();
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public OffsetDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(OffsetDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public String getUMessageId() {
        return uMessageId;
    }

    public void setUMessageId(String uMessageId) {
        this.uMessageId = uMessageId;
    }
}