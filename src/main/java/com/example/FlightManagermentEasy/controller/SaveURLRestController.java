package com.example.FlightManagermentEasy.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class SaveURLRestController {
    @GetMapping("/flight-management/save-URL")
    @ResponseBody
    public void saveURL(@RequestParam(value = "currentURL")Optional<String> curentURL,
                        HttpSession session){
        String getCurrentURL = curentURL.orElse("/");
        session.setAttribute("currentURL", getCurrentURL);
    }

}
