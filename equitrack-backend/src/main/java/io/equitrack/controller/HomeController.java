package io.equitrack.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Essential Controller for monitoring and testing API's
// This controller provides health check endpoints to verify if the application is running
@RestController
@RequestMapping({"/status", "/health"}) // Can be accessed via both /status and /health URLs
public class HomeController {

    @GetMapping
    public String healthCheck(){
        return "Application is Running!";
    }
}