package com.example.FlightManagermentEasy.controller.error;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {
    @GetMapping("/flight-management/my403-page")
    public String my403Page(){
        return "/error/my403Form";
    }
}
