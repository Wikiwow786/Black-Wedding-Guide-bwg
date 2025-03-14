package com.bwg.projection;

import java.time.OffsetDateTime;

public interface MessagesProjection {
    Long getMessageId();
    String getUMessageId();
    Long getSenderId();
    String getSenderFirstName();
    String getSenderLastName();
    Long getReceiverId();
    String getReceiverFirstName();
    String getReceiverLastName();
    String getConversationId();
    String getContent();
    OffsetDateTime getSentAt();
}

