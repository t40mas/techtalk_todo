package de.piller.techtalks.springboothtmx.todo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class RestControllerTest {

    @GetMapping("/status/check")
    public String status() {
        return "working";
    }
}