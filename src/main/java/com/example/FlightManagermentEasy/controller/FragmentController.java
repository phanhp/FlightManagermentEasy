package com.example.FlightManagermentEasy.controller;

import com.example.FlightManagermentEasy.entity.user.Account;
import com.example.FlightManagermentEasy.service.CommonModel;
import com.example.FlightManagermentEasy.service.session.BookingSession;
import com.example.FlightManagermentEasy.service.session.LoginSession;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FragmentController {
    @Autowired
    LoginSession loginSession;
    @Autowired
    BookingSession bookingSession;
    @Autowired
    CommonModel commonModel;

    @GetMapping("/flight-management/header")
    public String headerController(Model model,
                                   HttpSession session) {
        model.addAttribute("loginSession", loginSession);
        model.addAttribute("bookingSession", bookingSession);
        String accountImg = (String) session.getAttribute("accountImg");
        model.addAttribute("accountImg", accountImg);
        return "flight-management/fragments";
    }


}
