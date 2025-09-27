package io.equitrack.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//Essential Controller for monitoring and testing API's
@RestController
@RequestMapping({"/status", "/health"})
public class HomeController {

    @GetMapping
    public String healthCheck(){
        return "Application is Running!";
    }
}
