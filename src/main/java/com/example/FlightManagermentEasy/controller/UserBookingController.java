package com.example.FlightManagermentEasy.controller;

import com.example.FlightManagermentEasy.dto.flight.FlightDTO;
import com.example.FlightManagermentEasy.entity.Ticket;
import com.example.FlightManagermentEasy.entity.flight.aircraft.Aircraft;
import com.example.FlightManagermentEasy.entity.flight.aircraft.Cabin;
import com.example.FlightManagermentEasy.entity.flight.aircraft.Seat;
import com.example.FlightManagermentEasy.entity.flight.aircraft.SeatRow;
import com.example.FlightManagermentEasy.entity.user.Passenger;
import com.example.FlightManagermentEasy.entity.user.bank.BankAccount;
import com.example.FlightManagermentEasy.entity.user.booking.PromotionTicket;
import com.example.FlightManagermentEasy.repository.TicketRepository;
import com.example.FlightManagermentEasy.repository.flight.FlightRepository;
import com.example.FlightManagermentEasy.repository.flight.aircraft.AircraftRepository;
import com.example.FlightManagermentEasy.repository.user.bank.BankAccountRepository;
import com.example.FlightManagermentEasy.repository.user.booking.PromotionTicketRepository;
import com.example.FlightManagermentEasy.service.CommonModel;
import com.example.FlightManagermentEasy.service.MUF;
import com.example.FlightManagermentEasy.service.service.TicketService;
import com.example.FlightManagermentEasy.service.service.entityStatus.TicketStatus;
import com.example.FlightManagermentEasy.service.service.flight.FlightService;
import com.example.FlightManagermentEasy.service.service.flight.aircraft.AircraftService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.DecimalFormat;
import java.util.*;

@Controller
public class UserBookingController {
    @Autowired
    BookingSession bookingSession;
    @Autowired
    TicketRepository ticketRepository;
    @Autowired
    AircraftRepository aircraftRepository;
    @Autowired
    FlightRepository flightRepository;
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
    CommonModel commonModel;
    @Autowired
    LoginSession loginSession;
    @Autowired
    MUF muf;

    //***************** BOOKING *************************
    //Load Booking Page By Flight
    @GetMapping("/user/booking-ticket-page/{flightId}")
    public String userBookingTicketPage(Model model,
                                        @PathVariable("flightId") long flightId,
                                        HttpSession session) {
        Aircraft aircraft = flightService.findAircraftByFlightId(flightId);
        if (aircraft != null) {
            model.addAttribute("aircraftName", aircraft.getName());
        }

//        List<String> allCabinNameList = new ArrayList<>();
//
//        List<List<List<Map<String, Ticket>>>> cabinListMap = new ArrayList<>();
//        List<List<List<String>>> cabinNameList = new ArrayList<>();
//        //define : 0 = available
//        // true = isTicketDisableWithThisBooking(ticket.id) == true
//        // false = isTicketDisableWithThisBooking(ticket.id) == false
//        List<List<List<Boolean>>> cabinBookCheck = new ArrayList<>();
//        // true = isTicketPurchasedByThisAccount(ticket.id) == true
//        // false = isTicketPurchasedByThisAccount(ticket.id) == false
//        List<List<List<Boolean>>> cabinPurchaseCheck = new ArrayList<>();
//
//        List<Cabin> cabinList = flightService.findCabinListByFlightId(flightId);
//        if (cabinList != null) {
//            for (int i = 0; i < cabinList.size(); i++) {
//                Cabin cabin = cabinList.get(i);
//                allCabinNameList.add(cabin.getName());
//
//                List<List<Map<String, Ticket>>> seatRowListMap = new ArrayList<>();
//                List<List<String>> seatRowNameList = new ArrayList<>();
//                List<List<Boolean>> seatRowBookCheck = new ArrayList<>();
//                List<List<Boolean>> seatRowPurchaseCheck = new ArrayList<>();
//
//                List<SeatRow> seatRowList = aircraftService.findSeatRowListByCabinId(cabin.getId());
//                if (seatRowList != null) {
//                    for (int j = 0; j < seatRowList.size(); j++) {
//                        SeatRow seatRow = seatRowList.get(j);
//
//                        List<Map<String, Ticket>> seatListMap = new ArrayList<>();
//                        List<Boolean> seatBookCheck = new ArrayList<>();
//                        List<Boolean> seatPurchaseCheck = new ArrayList<>();
//                        List<String> seatNameList = new ArrayList<>();
//
//                        List<Seat> seatList = aircraftService.findSeatListBySeatRowId(seatRow.getId());
//                        if (seatList != null) {
//                            for (int k = 0; k < seatList.size(); k++) {
//                                Seat seat = seatList.get(k);
//
//                                Map<String, Ticket> ticketEntry = new HashMap<>();
//                                Boolean bookCheck = false;
//                                Boolean purchaseCheck = false;
//                                String seatName = new String();
//
//                                Ticket ticket = aircraftService.findTicketBySeatIdAndFlightId(seat.getId(), flightId);
//                                if (ticket != null) {
//                                    ticketEntry.put(seat.getName(), ticket);
//                                    bookCheck = bookingSession.isTicketBelongThisBooking(ticket);
//                                    purchaseCheck = bookingSession.isTicketPurchasedByThisAccount(ticket);
//                                    seatName = seat.getName();
//                                }
//
//                                seatListMap.add(ticketEntry);
//                                seatBookCheck.add(bookCheck);
//                                seatPurchaseCheck.add(purchaseCheck);
//                                seatNameList.add(seatName);
//                            }
//                        }
//                        seatRowListMap.add(seatListMap);
//                        seatRowBookCheck.add(seatBookCheck);
//                        seatRowPurchaseCheck.add(seatPurchaseCheck);
//                        seatRowNameList.add(seatNameList);
//                    }
//                }
//                cabinListMap.add(seatRowListMap);
//                cabinBookCheck.add(seatRowBookCheck);
//                cabinPurchaseCheck.add(seatRowPurchaseCheck);
//                cabinNameList.add(seatRowNameList);
//            }
//        }
//        model.addAttribute("cabinListMap", cabinListMap);
//        model.addAttribute("cabinBookCheck", cabinBookCheck);
//        model.addAttribute("cabinPurchaseCheck", cabinPurchaseCheck);
//        model.addAttribute("cabinNameList", cabinNameList);
//
//        model.addAttribute("allCabinNameList", allCabinNameList);

        bookingSession.reloadBookingSession();
        model.addAttribute("flightId", flightId);
        model.addAttribute("flight", flightService.findFlightDTOById(flightId));
        model.addAttribute("flightService", flightService);
        model.addAttribute("aircraftService", aircraftService);
        model.addAttribute("ticketService", ticketService);
        model.addAttribute("bookingSession", bookingSession);
        model.addAttribute("loginSession", loginSession);

        commonModel.headerModel(model);
        return "userSeatBooking";
    }

    //Book Ticket
    @PostMapping("/user/booking-ticket")
    public String userBookingTicket(@RequestParam(value = "selectedTicket", required = false) Optional<List<Long>> optionalSelectedTicketIdList,
                                    @RequestParam(value = "flightId", required = true) long flightId,
                                    @RequestParam(value = "accountId", required = true) long accountId,
                                    HttpSession session) {
        List<Long> selectedTicketIdList = optionalSelectedTicketIdList.orElse(new ArrayList<>());
        for (int i =0; i< selectedTicketIdList.size(); i++){
            System.out.println(selectedTicketIdList.get(i));
        }
        bookingSession.addListTicketToCartBySelectedId(selectedTicketIdList, flightId, accountId);
        return "redirect:/user/cart-page";
    }

    //********************* CART ***********************
    @GetMapping("/user/cart-page")
    public String userCartPage(Model model,
                               @RequestParam("page") Optional<Integer> page,
                               HttpSession session) {
        Pageable pageable = PageRequest.of(page.orElse(0), 1);
        String ticketListPageTitle = "ticketListPage";
        Page<List<Ticket>> ticketListPage = bookingSession.viewBookedTicketListPage(pageable);
        String pageUrlTitle = "userCartPage";
        String pageUrl = "/user/cart-page";

        commonModel.cartPageModel(model, session, page, ticketListPageTitle, ticketListPage, pageUrlTitle, pageUrl);
        commonModel.headerModel(model);
        return "userViewCart";
    }

    //UnBook Ticket
    @GetMapping("/user/remove-ticket-from-cart/{ticketId}")
    public String userRemoveTicketFromCart(@PathVariable("ticketId") long ticketId,
                                           HttpSession session,
                                           Model model) {
        String userCartPageUrl = commonModel.getRedirectPageUrlSession(session, model, "userCartPage", "/user/cart-page");
        String result = bookingSession.unBookTicketResult(ticketId);
        return userCartPageUrl;
    }

    //********************* PURCHASED TICKETED PAGE ****************************
    @GetMapping("/user/view-purchased-tickets-page")
    public String userViewPurchasedTicketsPage(Model model,
                                               @RequestParam("page") Optional<Integer> page,
                                               HttpSession session) {
        Pageable pageable = PageRequest.of(page.orElse(0), 1);
        String ticketListPageTitle = "ticketListPage";
        Page<List<Ticket>> ticketListPage = bookingSession.viewPurchasedTicketPage(pageable);
        String pageUrlTitle = "userPurchasedTicketPage";
        String pageUrl = "/user/view-purchased-tickets-page";

        commonModel.cartPageModel(model, session, page, ticketListPageTitle, ticketListPage, pageUrlTitle, pageUrl);
        commonModel.headerModel(model);
        return "userViewPurchasedTickets";
    }

    //UnBook Ticket
    @GetMapping("/user/refund-ticket/{ticketId}")
    public String userRefundTicket(@PathVariable("ticketId") long ticketId,
                                   HttpSession session,
                                   Model model) {
        String userCartPageUrl = commonModel.getRedirectPageUrlSession(session, model, "userPurchasedTicketPage", "/user/view-purchased-tickets-page");
        String result = bookingSession.refundTicketResult(ticketId);
        return userCartPageUrl;
    }

    //************************** PASSENGER *******************************
    @GetMapping("/user/passenger-edit-page/{ticketId}")
    public String userPassengerRegisterPage(@PathVariable("ticketId") long ticketId,
                                            Model model,
                                            HttpSession session) {
        Ticket ticket = ticketRepository.findById(ticketId).orElse(null);
        if (ticket == null) {
            String userCartPageUrl = commonModel.getRedirectPageUrlSession(session, model, "userCartPage", "/user/cart-page");
            return userCartPageUrl;
        }
        Passenger passenger = ticketService.findPassengerByTicketId(ticketId);

        model.addAttribute("ticket", ticket);
        model.addAttribute("passenger", passenger);
        model.addAttribute("ticketService", ticketService);

        commonModel.genderListModel(model);
        commonModel.headerModel(model);
        return "userPassengerInput";
    }

    @PostMapping("/user/passenger-register")
    public String userPassengerRegister(@RequestParam("ticketId") long ticketId,
                                        @RequestParam("identity") String identity,
                                        @RequestParam("name") String name,
                                        @RequestParam(value = "address", required = false) String address,
                                        @RequestParam(value = "dob", required = false) String dob,
                                        @RequestParam(value = "gender", required = false) String gender,
                                        HttpSession session,
                                        Model model) {
        String userCartPageUrl = commonModel.getRedirectPageUrlSession(session, model, "userCartPage", "/user/cart-page");
        String result = bookingSession.setPassengerToTicketResult(ticketId, identity, name, address, dob, gender);
        return userCartPageUrl;
    }

    //******************* PURCHASE TICKET *************************
    @GetMapping("/user/purchase-ticket/{ticketId}")
    public String userPurchaseSingleTicker(@PathVariable("ticketId") long ticketId,
                                           HttpSession session, Model model) {
        if (bookingSession.getBankAccount() == null) {
            return "redirect:/user/bank-account-page";
        } else {
            String userCartPageUrl = commonModel.getRedirectPageUrlSession(session, model, "userCartPage", "/user/cart-page");
            String result = bookingSession.purchaseTicketResult(ticketId);
            return userCartPageUrl;
        }
    }

    @GetMapping("/user/purchase-all-booking")
    public String userPurchaseAllBooking() {
        if (bookingSession.getBankAccount() == null) {
            return "redirect:/user/bank-account-page";
        } else {
            String result = bookingSession.purchaseAllTicketResult();
            return "redirect:/user/cart-page";
        }
    }

    //Add Promotion Ticket For A Ticket
    @GetMapping("/user/add-promotion-to-ticket-page/ticket/{ticketId}")
    public String userAddPromotionToTicketPage(@PathVariable("ticketId") long ticketId,
                                               Model model,
                                               HttpSession session) {
        String promotionTicketPageError = (String) session.getAttribute("promotionTicketPageError");
        model.addAttribute("promotionTicketPageError", promotionTicketPageError);
        session.setAttribute("promotionTicketPageError", "");
        Ticket ticket = ticketRepository.findById(ticketId).orElse(null);
        if (ticket == null) {
            session.setAttribute("promotionTicketPageError", "Ticket not found");
            return "redirect:/user/add-promotion-to-ticket-page/ticket/" + ticketId;
        }
        FlightDTO flightDTO = flightService.findFlightDTOById(flightRepository.findFlightsByTicketId(ticketId).getId());
        if (flightDTO == null) {
            session.setAttribute("promotionTicketPageError", "Flight not found");
            return "redirect:/user/add-promotion-to-ticket-page/ticket/" + ticketId;
        }
        model.addAttribute("flight", flightDTO);
        model.addAttribute("ticket", ticket);

        String cabinName = ticketService.findCabinNameByTicketId(ticketId);
        model.addAttribute("cabinName", cabinName);
        String seatName = ticketService.findSeatNameByTicketId(ticketId);
        model.addAttribute("seatName", seatName);

        String userCartPageUrl = (String) session.getAttribute("userCartPageUrl");
        model.addAttribute("userCartPageUrl", userCartPageUrl);

        List<PromotionTicket> promotionTicketList = promotionTicketRepository.findPromotionTicketsByTicketId(ticketId);
        model.addAttribute("promotionTicketList", promotionTicketList);

        double currentReduction = ticketService.priceReductionForTicket(ticket) * 100;
        double currentTicketPrice = ticketService.ticketCost(ticket);
        double orignialPrice = ticket.getPrice();
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        model.addAttribute("currentReduction", decimalFormat.format(currentReduction));
        model.addAttribute("currentTicketPrice", decimalFormat.format(currentTicketPrice));
        model.addAttribute("orignialPrice", decimalFormat.format(orignialPrice));

        model.addAttribute("muf", muf);
        commonModel.headerModel(model);
        return "userAddPromotionTicketForm";
    }

    @PostMapping("/user/add-promotion-to-ticket")
    public String userAddPromotionToTicket(@RequestParam("promotionTicketCode") String code,
                                           @RequestParam("ticketId") long ticketId) {
        String result = bookingSession.usePromotionTicketResult(code, ticketId);
        return "redirect:/user/add-promotion-to-ticket-page/ticket/" + ticketId;
    }

    @GetMapping("/user/remove-promotion-from-ticket/{promotionTicketId}/{ticketId}")
    public String removePromotionFromTicket(@PathVariable("promotionTicketId") long promotionTicketId,
                                            @PathVariable("ticketId") long ticketId) {
        String result = bookingSession.unUsedPromotionTicketResult(promotionTicketId);
        return "redirect:/user/add-promotion-to-ticket-page/ticket/" + ticketId;
    }

    //Login Bank Account For BookingSession
    @GetMapping("/user/bank-account-page")
    public String bankAccountPage(Model model, HttpSession session) {
        commonModel.headerModel(model);
        return "userBankAccount";
    }

    @PostMapping("/user/bank-account")
    public String bankAccount(@RequestParam("bankAccountName") String bankAccountName) {
        BankAccount bankAccount;
        try {
            bankAccount = bankAccountRepository.findBankAccountByName(bankAccountName);
        } catch (Exception e) {
            return "redirect:/user/bank-account-page";
        }
        bookingSession.setBankAccount(bankAccount);
        return "redirect:/user/cart-page";
    }

    //View All Purchased Ticket
    @GetMapping("/user/view-purchased-ticket-page")
    public String userViewPurchasedTicketPage(Model model,
                                              HttpSession session) {
        Map<Long, List<Ticket>> ticketListMap = bookingSession.viewPurchasedTicketMap();
        model.addAttribute("ticketListMap", ticketListMap);
        model.addAttribute("aircraftService", aircraftService);
        model.addAttribute("flightService", flightService);
        model.addAttribute("ticketService", ticketService);

        commonModel.genderListModel(model);
        commonModel.headerModel(model);
        return "userViewPurchasedTicketsForm";
    }

}
