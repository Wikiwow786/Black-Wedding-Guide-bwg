package com.bwg.restapi;

import com.bwg.model.AuthModel;
import com.bwg.model.MessagesModel;
import com.bwg.resolver.AuthPrincipal;
import com.bwg.service.MessagesService;
import com.bwg.util.CorrelationIdHolder;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessagesController {

    @Autowired
    private MessagesService messagesService;

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<MessagesModel>> getAllMessages(@RequestParam(required = false)String search,@RequestParam(required = false)String conversationId, Pageable pageable, @AuthPrincipal AuthModel authModel) {
        return ResponseEntity.ok(messagesService.getAllMessages(search,conversationId,authModel,pageable).map(MessagesModel::new));
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessagesModel> createMessage(@RequestBody MessagesModel messagesModel, @AuthPrincipal AuthModel authModel) {
        return ResponseEntity.ok(new MessagesModel(messagesService.createMessage(messagesModel)));
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping(value = "/conversation/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MessagesModel>> getAllMessagesByUserId(@PathVariable(value = "userId") final Long userId, @AuthPrincipal AuthModel authModel) {
        return ResponseEntity.ok(messagesService.getAllMessagesByUserId(userId).stream().map(MessagesModel::new).toList());
    }
}
