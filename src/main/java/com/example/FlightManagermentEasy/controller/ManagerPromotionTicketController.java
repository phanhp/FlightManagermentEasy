package com.example.FlightManagermentEasy.controller;

import com.example.FlightManagermentEasy.entity.user.booking.PromotionTicket;
import com.example.FlightManagermentEasy.repository.user.booking.PromotionTicketRepository;
import com.example.FlightManagermentEasy.service.CommonModel;
import com.example.FlightManagermentEasy.service.session.LoginSession;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ManagerPromotionTicketController {
    @Autowired
    PromotionTicketRepository promotionTicketRepository;
    @Autowired
    LoginSession loginSession;
    @Autowired
    CommonModel commonModel;

    @GetMapping("/admin/view-promotion-tickets-page")
    private String adminViewPromotionTicketPage(Model model,
                                                HttpSession session){
        List<PromotionTicket> promotionTicketList = promotionTicketRepository.findAll();

        commonModel.headerModel(model);
        return "admin/viewPromotionTicket";
    }
}
