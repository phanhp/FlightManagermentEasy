package com.example.FlightManagermentEasy.controller;


import com.example.FlightManagermentEasy.dto.flight.FlightDTO;
import com.example.FlightManagermentEasy.dto.flight.aircraft.AircraftDTO;
import com.example.FlightManagermentEasy.dto.flight.location.AirportDTO;
import com.example.FlightManagermentEasy.dto.flight.location.CityDTO;
import com.example.FlightManagermentEasy.dto.flight.location.CountryDTO;
import com.example.FlightManagermentEasy.dto.flight.location.RouteDTO;
import com.example.FlightManagermentEasy.service.CommonModel;

import com.example.FlightManagermentEasy.entity.Ticket;
import com.example.FlightManagermentEasy.repository.TicketRepository;
import com.example.FlightManagermentEasy.repository.flight.location.AirportRepository;
import com.example.FlightManagermentEasy.repository.flight.location.CityRepository;
import com.example.FlightManagermentEasy.repository.flight.location.CountryRepository;
import com.example.FlightManagermentEasy.repository.flight.location.RouteRepository;
import com.example.FlightManagermentEasy.repository.user.bank.BankAccountRepository;
import com.example.FlightManagermentEasy.repository.user.booking.PromotionTicketRepository;
import com.example.FlightManagermentEasy.service.MUF;
import com.example.FlightManagermentEasy.service.dtoconverter.CollectionConverter;
import com.example.FlightManagermentEasy.service.dtoconverter.ObjectConverter;
import com.example.FlightManagermentEasy.service.service.EmailService;
import com.example.FlightManagermentEasy.service.service.TicketService;
import com.example.FlightManagermentEasy.service.service.entityStatus.TicketStatus;
import com.example.FlightManagermentEasy.service.service.flight.FlightService;
import com.example.FlightManagermentEasy.service.service.flight.aircraft.AircraftService;

import com.example.FlightManagermentEasy.service.session.BookingSession;
import com.example.FlightManagermentEasy.service.session.LoginSession;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


@Controller
public class FragmentController {
    @Autowired
    LoginSession loginSession;
    @Autowired
    BookingSession bookingSession;
    @Autowired
    CommonModel commonModel;
    @Autowired
    EmailService emailService;
    @Autowired
    ObjectConverter objectConverter;
    @Autowired
    CollectionConverter collectionConverter;
    @Autowired
    TicketRepository ticketRepository;
    @Autowired
    TicketService ticketService;
    @Autowired
    FlightService flightService;
    @Autowired
    AircraftService aircraftService;
    @Autowired
    PromotionTicketRepository promotionTicketRepository;
    @Autowired
    BankAccountRepository bankAccountRepository;
    @Autowired
    TicketStatus ticketStatus;
    @Autowired
    CountryRepository countryRepository;
    @Autowired
    CityRepository cityRepository;
    @Autowired
    AirportRepository airportRepository;
    @Autowired
    RouteRepository routeRepository;
    @Autowired
    MUF muf;


    @GetMapping("/flight-management/header")
    public String headerController(Model model, HttpSession session) {
        model.addAttribute("loginSession", loginSession);
        model.addAttribute("bookingSession", bookingSession);

        String accountImg = (String) session.getAttribute("accountImg");
        model.addAttribute("accountImg", accountImg);
        try {
            accountImg = loginSession.getProfileImage();
            model.addAttribute("accountImg", accountImg);
        } catch (Exception e) {
            String accountImgError = "Can Not Load Account Image";
            model.addAttribute("accountImgError", accountImgError);
        }
        return "flight-management/fragments";
    }

    @GetMapping("/userViewCartForm")
    public String userViewCartForm(Model model, Page<List<Ticket>> ticketListPage, List<Integer> integerList) {
        model.addAttribute("promotionTicketRepository", promotionTicketRepository);
        model.addAttribute("aircraftService", aircraftService);
        model.addAttribute("flightService", flightService);
        model.addAttribute("ticketService", ticketService);
        model.addAttribute("bookingSession", bookingSession);
        model.addAttribute("firstPage", 0);
        model.addAttribute("previousPage", 0);
        model.addAttribute("nextPage", 0);
        model.addAttribute("lastPage", 0);
        model.addAttribute("currentPage", 0);
        model.addAttribute("userCartPage", "");
        model.addAttribute("ticketListPage", ticketListPage);
        model.addAttribute("allPageList", integerList);
        model.addAttribute("paymentResult", "");
        return "userViewCart";
    }

    @GetMapping("/userViewPurchasedTicketForm")
    public String userViewPurchasedTicketForm(Model model, Page<List<Ticket>> ticketListPage, List<Integer> integerList) {
        model.addAttribute("promotionTicketRepository", promotionTicketRepository);
        model.addAttribute("paymentResult", "");
        model.addAttribute("aircraftService", aircraftService);
        model.addAttribute("flightService", flightService);
        model.addAttribute("ticketService", ticketService);
        model.addAttribute("bookingSession", bookingSession);
        model.addAttribute("firstPage", 0);
        model.addAttribute("previousPage", 0);
        model.addAttribute("nextPage", 0);
        model.addAttribute("lastPage", 0);
        model.addAttribute("currentPage", 0);
        model.addAttribute("userCartPage", "");
        model.addAttribute("ticketListPage", ticketListPage);
        model.addAttribute("allPageList", integerList);
        return "userViewPurchasedTickets";
    }

    @GetMapping("/adminCreateFlight")
    public String adminCreateFlight(Model model, HttpSession session,
                                    List<CountryDTO> countryDTOList, List<CityDTO> cityDTOList,
                                    List<AirportDTO> airportDTOList, List<RouteDTO> routeDTOList,
                                    List<AircraftDTO> aircraftDTOList) {
        model.addAttribute("countryList", countryDTOList);
        model.addAttribute("cityList", cityDTOList);
        model.addAttribute("airportList", airportDTOList);
        model.addAttribute("routeList", routeDTOList);
        model.addAttribute("aircraftList", aircraftDTOList);
        model.addAttribute("flight", new FlightDTO());
        return "managerCreateFlight";
    }

    @GetMapping("/homeFragment")
    public String homeFragment(Model model, HttpSession session,
                               List<FlightDTO> flightList,
                               List<CityDTO> cityDTOList,
                               List<Integer> integerList) {
        model.addAttribute("muf", muf);
        model.addAttribute("cityList", cityDTOList);
        model.addAttribute("flightList", flightList);
        model.addAttribute("resultSearch", "");
        model.addAttribute("homePage", "");
        model.addAttribute("firstPage", "");
        model.addAttribute("previousPage", "");
        model.addAttribute("departureCityId", "");
        model.addAttribute("arrivalCityId", "");
        model.addAttribute("departureTime", "");
        model.addAttribute("nextPage", "");
        model.addAttribute("lastPage", "");
        model.addAttribute("currentPage", "");
        model.addAttribute("allPageList", integerList);
        return "flight-management/home";
    }

    @GetMapping("/adminViewFlight")
    public String adminViewFlight(Model model, HttpSession session,
                                  List<FlightDTO> flightList,
                                  List<CityDTO> cityDTOList,
                                  List<Integer> integerList) {
        model.addAttribute("muf", muf);
        model.addAttribute("cityList", cityDTOList);
        model.addAttribute("flightList", flightList);
        model.addAttribute("resultSearch", "");
        model.addAttribute("adminViewFlightPage", "");
        model.addAttribute("firstPage", "");
        model.addAttribute("previousPage", "");
        model.addAttribute("departureCityId", "");
        model.addAttribute("arrivalCityId", "");
        model.addAttribute("departureTime", "");
        model.addAttribute("nextPage", "");
        model.addAttribute("lastPage", "");
        model.addAttribute("currentPage", "");
        model.addAttribute("allPageList", integerList);
        return "managerViewFlight";
    }
}
