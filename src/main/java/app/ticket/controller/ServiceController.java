package app.ticket.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServiceController {
    @RequestMapping("/ping")
    public String ping() {
        return "Hello";
    }
}
