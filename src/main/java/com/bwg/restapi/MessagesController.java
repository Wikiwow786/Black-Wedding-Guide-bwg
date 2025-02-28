package com.bwg.restapi;

import com.bwg.model.MessagesModel;
import com.bwg.service.MessagesService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<List<MessagesModel>> getAllMessages() {
        return ResponseEntity.ok(messagesService.getAllMessages().stream().map(MessagesModel::new).toList());
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessagesModel> createMessage(@RequestBody MessagesModel messagesModel) {
        return ResponseEntity.ok(new MessagesModel(messagesService.createMessage(messagesModel)));
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping(value = "/conversation/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MessagesModel>> getAllMessagesByUserId(@PathVariable(value = "userId") final Long userId) {
        return ResponseEntity.ok(messagesService.getAllMessagesByUserId(userId).stream().map(MessagesModel::new).toList());
    }
}
