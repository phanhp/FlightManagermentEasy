package com.example.FlightManagermentEasy.controller;

import com.example.FlightManagermentEasy.dto.flight.FlightDTO;
import com.example.FlightManagermentEasy.dto.flight.location.CityDTO;
import com.example.FlightManagermentEasy.entity.flight.Flight;
import com.example.FlightManagermentEasy.entity.flight.location.City;
import com.example.FlightManagermentEasy.entity.user.Account;
import com.example.FlightManagermentEasy.repository.MyTimeRepository;
import com.example.FlightManagermentEasy.repository.TicketRepository;
import com.example.FlightManagermentEasy.repository.flight.FlightRepository;
import com.example.FlightManagermentEasy.repository.flight.location.CityRepository;
import com.example.FlightManagermentEasy.repository.user.user.PassengerRepository;
import com.example.FlightManagermentEasy.service.CommonModel;
import com.example.FlightManagermentEasy.service.MUF;
import com.example.FlightManagermentEasy.service.dtoconverter.CollectionConverter;
import com.example.FlightManagermentEasy.service.dtoconverter.ObjectConverter;
import com.example.FlightManagermentEasy.service.service.TicketService;
import com.example.FlightManagermentEasy.service.service.flight.FlightService;
import com.example.FlightManagermentEasy.service.session.BookingSession;
import com.example.FlightManagermentEasy.service.session.LoginSession;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {
    @Autowired
    MUF muf;
    @Autowired
    ObjectConverter objectConverter;
    @Autowired
    CollectionConverter collectionConverter;
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
    CommonModel commonModel;

    @GetMapping({"/", "/flight-management", "/flight-management/search"})
    public String viewHomePage(Model model,
                               @RequestParam(value = "departureCityId", required = false, defaultValue = "0") Optional<Long> departureCityIdOptional,
                               @RequestParam(value = "arrivalCityId", required = false, defaultValue = "0") Optional<Long> arrivalCityIdOptional,
                               @RequestParam(value = "departureTime", required = false, defaultValue = "") Optional<String> departureTimeOptional,
                               @RequestParam("page") Optional<Integer> page,
                               HttpSession session) {
        Pageable pageable = PageRequest.of(page.orElse(0), 3);
        String pageUrlTitle = "homePage";
        String pageUrl = "/flight-management";

        commonModel.viewFlightPageModel(model, session, departureCityIdOptional, arrivalCityIdOptional, departureTimeOptional, page, pageable, pageUrlTitle, pageUrl);
        return "flight-management/home";
    }
}
