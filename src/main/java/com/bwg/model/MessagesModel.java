package com.bwg.model;

import com.bwg.config.OffsetDateTimeCustomSerializer;
import com.bwg.domain.Messages;
import com.bwg.exception.ResourceNotFoundException;
import com.bwg.projection.MessagesProjection;
import com.bwg.repository.MessagesRepository;
import com.bwg.util.BeanUtil;
import com.bwg.util.NameUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MessagesModel {

    private Long messageId;
    @JsonIgnore
    private String uMessageId;
    private Long senderId;
    private Long receiverId;
    private String conversationId;
    private String content;
    @JsonSerialize(using = OffsetDateTimeCustomSerializer.class)
    private OffsetDateTime sentAt;
    private String senderName;
    private String receiverName;

    public MessagesModel() {
    }

    public MessagesModel(Messages messages) {
        this.messageId = messages.getMessageId();
        this.uMessageId = messages.getUMessageId();
        this.senderId = messages.getSender().getUserId();
        this.receiverName = NameUtils.formatFullName(messages.getReceiver().getFirstName(), messages.getReceiver().getLastName());
        this.senderName = NameUtils.formatFullName(messages.getSender().getFirstName(), messages.getSender().getLastName());
        this.receiverId = messages.getReceiver().getUserId();
        this.conversationId = messages.getConversationId();
        this.content = messages.getContent();
        this.sentAt = messages.getSentAt();
    }

    public MessagesModel(MessagesProjection messages) {
        this.messageId = messages.getMessageId();
        this.uMessageId = messages.getUMessageId();
        this.senderId = messages.getSenderId();
        this.receiverId = messages.getReceiverId();
        this.receiverName = NameUtils.formatFullName(messages.getReceiverFirstName(), messages.getReceiverLastName());
        this.senderName = NameUtils.formatFullName(messages.getSenderFirstName(), messages.getSenderLastName());
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

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }
}