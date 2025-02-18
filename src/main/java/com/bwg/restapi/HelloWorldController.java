package com.bwg.restapi;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hello")
@Log4j2
public class HelloWorldController {

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> hello() {
        log.info("Inside Hello World Controller!!!");
        return ResponseEntity.ok("Hello BWG World!");
    }
}
