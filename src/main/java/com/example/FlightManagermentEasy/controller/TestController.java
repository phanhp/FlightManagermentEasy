package com.example.FlightManagermentEasy.controller;

import com.example.FlightManagermentEasy.entity.MyToken;
import com.example.FlightManagermentEasy.entity.user.Account;
import com.example.FlightManagermentEasy.repository.MyTimeRepository;
import com.example.FlightManagermentEasy.repository.TestEntityRepository;
import com.example.FlightManagermentEasy.repository.TicketRepository;
import com.example.FlightManagermentEasy.repository.MyTokenRepository;
import com.example.FlightManagermentEasy.repository.flight.FlightRepository;
import com.example.FlightManagermentEasy.repository.flight.aircraft.*;
import com.example.FlightManagermentEasy.repository.flight.location.CityRepository;
import com.example.FlightManagermentEasy.repository.user.bank.BankAccountRepository;
import com.example.FlightManagermentEasy.repository.user.user.AccountRepository;
import com.example.FlightManagermentEasy.repository.user.user.PassengerRepository;
import com.example.FlightManagermentEasy.service.CommonModel;
import com.example.FlightManagermentEasy.service.MUF;
import com.example.FlightManagermentEasy.service.dtoconverter.CollectionConverter;
import com.example.FlightManagermentEasy.service.dtoconverter.ObjectConverter;
import com.example.FlightManagermentEasy.service.service.EmailRequest;
import com.example.FlightManagermentEasy.service.service.EmailService;
import com.example.FlightManagermentEasy.service.service.TicketService;
import com.example.FlightManagermentEasy.service.service.TokenService;
import com.example.FlightManagermentEasy.service.service.entityStatus.TicketStatus;
import com.example.FlightManagermentEasy.service.service.flight.FlightService;
import com.example.FlightManagermentEasy.service.service.flight.aircraft.AircraftService;
import com.example.FlightManagermentEasy.service.service.user.CustomUserDetails;
import com.example.FlightManagermentEasy.service.session.BookingSession;
import com.example.FlightManagermentEasy.service.session.LoginSession;
import com.example.FlightManagermentEasy.service.session.ThisMomentSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.Map;

@Controller
public class TestController {
    @Autowired
    MUF muf;
    @Autowired
    SeatRepository seatRepository;
    @Autowired
    CommonModel commonModel;
    @Autowired
    CabinRepository cabinRepository;
    @Autowired
    ObjectConverter objectConverter;
    @Autowired
    AircraftRepository aircraftRepository;
    @Autowired
    CollectionConverter collectionConverter;
    @Autowired
    SeatRowRepository seatRowRepository;
    @Autowired
    FlightService flightService;
    @Autowired
    FlightRepository flightRepository;
    @Autowired
    CityRepository cityRepository;
    @Autowired
    TicketService ticketService;
    @Autowired
    TicketRepository ticketRepository;
    @Autowired
    PassengerRepository passengerRepository;
    @Autowired
    BookingSession bookingSession;
    @Autowired
    LoginSession loginSession;
    @Autowired
    MyTimeRepository myTimeRepository;
    @Autowired
    ThisMomentSession thisMomentSession;
    @Autowired
    AircraftService aircraftService;
    @Autowired
    BankAccountRepository bankAccountRepository;
    @Autowired
    TestEntityRepository testEntityRepository;
    @Autowired
    TicketStatus ticketStatus;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    CustomUserDetails customUserDetails;
    @Autowired
    TokenService tokenService;
    @Autowired
    EmailService emailService;
    @Autowired
    MyTokenRepository myTokenRepository;
    @Autowired
    ApplicationContext context;

    @GetMapping("/test")
    public String test(Model model) {
        model.addAttribute("encodeKey", "123");
        model.addAttribute("gender", "gay");
        model.addAttribute("name", "Fucker");
        return "emailConfirmTemplate";
    }

    @PostMapping("/test-post/{encodeKey}")
    public String testPost(@PathVariable("encodeKey") String encodeKey){
        System.out.println("Your Encode Key Is: "+encodeKey);
        System.out.println("Mail Confirm Successfully......");
        MyToken token = myTokenRepository.findMyTokenByEncodeKey(encodeKey);
        myTokenRepository.delete(token);
        System.out.println("Token Was Delete After Submit, Check DataBase");
        return "redirect:/";
    }
}
