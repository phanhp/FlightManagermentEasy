package com.example.FlightManagermentEasy.service.session;

import com.example.FlightManagermentEasy.exception.InvalidDataException;
import com.example.FlightManagermentEasy.entity.Ticket;
import com.example.FlightManagermentEasy.entity.flight.Flight;
import com.example.FlightManagermentEasy.entity.user.Account;
import com.example.FlightManagermentEasy.entity.user.bank.BankAccount;
import com.example.FlightManagermentEasy.entity.user.booking.Booking;
import com.example.FlightManagermentEasy.entity.user.booking.PromotionTicket;

import com.example.FlightManagermentEasy.repository.TicketRepository;
import com.example.FlightManagermentEasy.repository.flight.FlightRepository;
import com.example.FlightManagermentEasy.repository.flight.aircraft.SeatRepository;
import com.example.FlightManagermentEasy.repository.user.booking.BookingRepository;

import com.example.FlightManagermentEasy.repository.user.booking.PromotionTicketRepository;

import com.example.FlightManagermentEasy.repository.user.user.AccountRepository;
import com.example.FlightManagermentEasy.service.service.TicketService;
import com.example.FlightManagermentEasy.service.service.entityStatus.TicketStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.springframework.web.context.annotation.SessionScope;

import javax.naming.InsufficientResourcesException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@SessionScope
public class BookingSession {
    @Autowired
    LoginSession loginSession;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    ThisMomentSession thisMomentSession;
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    TicketRepository ticketRepository;
    @Autowired
    FlightRepository flightRepository;
    @Autowired
    SeatRepository seatRepository;
    @Autowired
    TicketService ticketService;
    @Autowired
    PromotionTicketRepository promotionTicketRepository;
    @Autowired
    TicketStatus ticketStatus;

    private List<Ticket> nonPurchasedTicketList;

    private List<Ticket> purchasedTickedList;

    private List<Ticket> flightedTicketList;

    private Booking nonPurchasedBooking;

    private List<Booking> purchasedBookingList;

    private BankAccount bankAccount;


    //RELOAD BOOKING SESSION
    public void reloadBookingSession() {
        if (loginSession.isLoggedIn()) {
            Account account = loginSession.getAccount();
            LocalDateTime thisMoment = thisMomentSession.getThisMoment();

            //nonPurchasedBooking reload
            List<Booking> nonPurchasedBookingList = bookingRepository.findNonPurchasedBookingsByAccountId(account.getId());
            if (nonPurchasedBookingList != null) {
                if (!nonPurchasedBookingList.isEmpty()) {
                    Booking nonPurchasedBooking = nonPurchasedBookingList.get(0);
                    this.nonPurchasedBooking = nonPurchasedBooking;
                } else {
                    Booking nonPurchasedBooking = new Booking(thisMoment, false, account);
                    bookingRepository.save(nonPurchasedBooking);
                    this.nonPurchasedBooking = nonPurchasedBooking;
                }
            } else {
                Booking nonPurchasedBooking = new Booking(thisMoment, false, account);
                bookingRepository.save(nonPurchasedBooking);
                this.nonPurchasedBooking = nonPurchasedBooking;
            }

            //nonPurchasedTicketList reload
            List<Ticket> nonPurchasedTicketList = ticketRepository.findNonPurchaseTicketsByAccountId(account.getId());
            if (nonPurchasedTicketList == null) {
                nonPurchasedTicketList = new ArrayList<>();
            }
            this.nonPurchasedTicketList = ticketStatus.reloadTicketListStatus(nonPurchasedTicketList);
            for (int i = 0; i < this.nonPurchasedTicketList.size(); i++) {
                Ticket ticket = this.nonPurchasedTicketList.get(i);
                ticket.setBooking(this.nonPurchasedBooking);
                ticketRepository.save(ticket);
            }

            //purchasedTicketList reload
            List<Ticket> purchasedTicketList = ticketRepository.findPurchasedTicketsByAccountId(account.getId());
            if (purchasedTicketList == null) {
                purchasedTicketList = new ArrayList<>();
            }
            this.purchasedTickedList = purchasedTicketList;

            //flightedTicketList reload
            List<Ticket> flightedTicketList = ticketRepository.findFlightedTicketsByAccountId(account.getId(), thisMoment);
            if (flightedTicketList == null) {
                flightedTicketList = new ArrayList<>();
            }
            this.flightedTicketList = ticketStatus.reloadTicketListStatus(flightedTicketList);

            //purchasedBookingList reload
            List<Booking> purchasedBookingList = bookingRepository.findPurchasedBookingByAccountId(account.getId());
            if (purchasedBookingList == null) {
                purchasedBookingList = new ArrayList<>();
            }
            this.purchasedBookingList = purchasedBookingList;
        } else {
            this.nonPurchasedTicketList = null;
            this.purchasedTickedList = null;
            this.flightedTicketList = null;
            this.nonPurchasedBooking = null;
            this.purchasedBookingList = null;
            this.bankAccount = null;
        }
    }

    //Get bank and set bank account to pay
    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    public BankAccount getBankAccount() {
        return this.bankAccount;
    }

    //Check Ticket Relationship with Account
    //is ticket purchased by this account
    public boolean isTicketPurchasedByThisAccount(Ticket ticket) {
        if (ticket == null) {
            return false;
        }
        reloadBookingSession();
        if (loginSession.isLoggedIn()) {
            ticket = ticketStatus.reloadTicketStatus(ticket);
            if (!ticket.isPurchased()) {
                return false;
            } else {
                Account account = accountRepository.findAccountByTicketId(ticket.getId());
                if (account == null) {
                    return false;
                } else if (account.getId() == loginSession.getAccount().getId()) {
                    return true;
                }
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean isTicketPurchasedByThisAccount(long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId).orElse(null);
        try {
            return isTicketPurchasedByThisAccount(ticket);
        } catch (Exception e) {
            return false;
        }
    }

    //is ticket disable with this booking
    public boolean isTicketDisableWithThisBooking(Ticket ticket) {
        if (ticket == null) {
            return true;
        }
        reloadBookingSession();
        if (loginSession.isLoggedIn()) {
            ticket = ticketStatus.reloadTicketStatus(ticket);
            if (ticket.isPurchased()) {
                return true;
            }
            if (ticket.isAvailable()) {
                return false;
            }
            if (!ticket.isAvailable()) {
                Booking booking = bookingRepository.findBookingsByTicketId(ticket.getId());
                if (booking.getId() == this.nonPurchasedBooking.getId()) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean isTicketDisableWithThisBooking(long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId).orElse(null);
        return isTicketDisableWithThisBooking(ticket);
    }

    //is ticket belong this booking
    public boolean isTicketBelongThisBooking(Ticket ticket) {
        if (ticket == null) {
            return false;
        }
        reloadBookingSession();
        if (loginSession.isLoggedIn()) {
            ticket = ticketStatus.reloadTicketStatus(ticket);
            if (ticket.isPurchased()) {
                return false;
            } else {
                if (ticket.isAvailable()) {
                    return false;
                }
                Booking booking = bookingRepository.findBookingsByTicketId(ticket.getId());
                if (booking.getId() == this.nonPurchasedBooking.getId()) {
                    return true;
                }
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean isTicketBelongThisBooking(long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId).orElse(null);
        try {
            return isTicketBelongThisBooking(ticket);
        } catch (Exception e) {
            return false;
        }
    }


    //each time booking ticket from flight,
//remove the previous selected seat that is not selected in this booking time
//add new selected seat that is selected in this booking time
//only effect on this flight id

    public boolean isFlightIdInNonPurchasedTicketList(long flightId) {
        reloadBookingSession();
        if (loginSession.isLoggedIn()) {
            int count = 0;
            for (int i = 0; i < this.nonPurchasedTicketList.size(); i++) {
                if (this.nonPurchasedTicketList.get(i).getFlight().getId() == flightId) {
                    count++;
                    break;
                }
            }
            return count != 0;
        } else {
            return false;
        }
    }


    //Separate Non-purchased Ticket In nonPurchasedTicketList To Map By flightId
    public Map<Long, List<Ticket>> viewBookedTicketMap() {
        reloadBookingSession();
        if (this.nonPurchasedTicketList.isEmpty()) {
            return new HashMap<>();
        }
        Map<Long, List<Ticket>> ticketMap = new HashMap<>();
        for (Ticket ticket : this.nonPurchasedTicketList) {
            long flightId = ticket.getFlight().getId();
            if (!ticketMap.containsKey(flightId)) {
                ticketMap.put(flightId, new ArrayList<>());
            }
            List<Ticket> bookedTicketInFlight = ticketMap.get(flightId);
            bookedTicketInFlight.add(ticket);
        }
        return ticketMap;
    }

    public void addListTicketToCartBySelectedId(List<Long> selectedTicketIdList, long flightId, long accountId) {
        reloadBookingSession();
        if (loginSession.isLoggedIn()) {
            if (accountId == loginSession.getAccount().getId()) {
                Map<Long, List<Ticket>> ticketListMap = viewBookedTicketMap();
                List<Ticket> ticketListWithFlightIdInCart = new ArrayList<>();
                //All Ticket In nonPurchasedList Of This flightId
                List<Long> ticketIdListWithFlightIdInCart = new ArrayList<>();
                if (isFlightIdInNonPurchasedTicketList(flightId)) {
                    ticketListWithFlightIdInCart = ticketListMap.get(flightId);
                    for (int i = 0; i < ticketListWithFlightIdInCart.size(); i++) {
                        ticketIdListWithFlightIdInCart.add(ticketListWithFlightIdInCart.get(i).getId());
                    }
                }
                //All Tickets Those Were Remove From Booking This Time
                List<Long> removeTicketIdList = new ArrayList<>();
                for (int i = 0; i < ticketIdListWithFlightIdInCart.size(); i++) {
                    if (!selectedTicketIdList.contains(ticketIdListWithFlightIdInCart.get(i))) {
                        removeTicketIdList.add(ticketIdListWithFlightIdInCart.get(i));
                    }
                }
                for (int i = 0; i < removeTicketIdList.size(); i++) {
                    long ticketId = removeTicketIdList.get(i);
                    Ticket ticket = ticketRepository.findById(ticketId).orElse(null);
                    if (ticket != null) {
                        if (!isTicketDisableWithThisBooking(ticket)) {
                            unBookTicketResult(ticket);
                        }
                    }
                }
                for (int i = 0; i < selectedTicketIdList.size(); i++) {
                    long ticketId = selectedTicketIdList.get(i);
                    Ticket ticket = ticketRepository.findById(ticketId).orElse(null);
                    if (ticket != null) {
                        if (!isTicketDisableWithThisBooking(ticket)) {
                            bookTicketResult(ticket);
                        }
                    }
                }
            }
        }
    }

    public int numberNonPurchasedTicket() {
        if (this.nonPurchasedTicketList == null) {
            return 0;
        }
        return this.nonPurchasedTicketList.size();
    }

    public int numberPurchasedTicket() {
        if (this.purchasedTickedList == null) {
            return 0;
        }
        return this.purchasedTickedList.size();
    }

    public int numberFlightedTicket() {
        if (this.flightedTicketList == null) {
            return 0;
        }
        return this.flightedTicketList.size();
    }

    public Page<List<Ticket>> viewBookedTicketListPage(Pageable pageable) {
        Map<Long, List<Ticket>> ticketListMap = viewBookedTicketMap();
        return convertTicketListMapToPage(ticketListMap, pageable);
    }

    //Separate purchased ticket in list by flight id
    public Map<Long, List<Ticket>> viewPurchasedTicketMap() {
        reloadBookingSession();
        if (this.purchasedTickedList.isEmpty()) {
            return new HashMap<>();
        }
        Map<Long, List<Ticket>> ticketMap = new HashMap<>();
        for (Ticket ticket : this.purchasedTickedList) {
            long flightId = ticket.getFlight().getId();
            if (!ticketMap.containsKey(flightId)) {
                ticketMap.put(flightId, new ArrayList<>());
            }
            List<Ticket> bookedTicketInFlight = ticketMap.get(flightId);
            bookedTicketInFlight.add(ticket);
        }
        return ticketMap;
    }

    public Page<List<Ticket>> viewPurchasedTicketPage(Pageable pageable) {
        Map<Long, List<Ticket>> ticketListMap = viewPurchasedTicketMap();
        return convertTicketListMapToPage(ticketListMap, pageable);
    }

    //Convert
    public List<List<Ticket>> convertTicketListMapToListOfList(Map<Long, List<Ticket>> ticketListMap) {
        return new ArrayList<>(ticketListMap.values());
    }

    public Page<List<Ticket>> convertTicketListMapToPage(Map<Long, List<Ticket>> ticketListMap, Pageable pageable) {
        List<List<Ticket>> ticketListList = convertTicketListMapToListOfList(ticketListMap);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), ticketListList.size());
        return new PageImpl<>(ticketListList.subList(start, end), pageable, ticketListList.size());
    }

    public long getFlightIdFromTicketList(List<Ticket> ticketList) {
        Flight flight = flightRepository.findFlightsByTicketId(ticketList.get(0).getId());
        return flight.getId();
    }

    //************************ TICKET BOOKING **************************************
    public Ticket bookTicket(Ticket ticket) throws InvalidDataException {
        if (!loginSession.isLoggedIn()) {
            throw new InvalidDataException("Not Login Yet");
        }
        if (ticket == null) {
            throw new InvalidDataException("Ticket Can Not Be Found");
        }
        reloadBookingSession();
        ticket = ticketStatus.reloadTicketStatus(ticket);
        if (ticket.isPurchased()) {
            throw new InvalidDataException("Can Not Book A Purchased Ticket");
        }
        Flight flight = flightRepository.findFlightsByTicketId(ticket.getId());
        if (flight == null) {
            throw new InvalidDataException("Flight Can Not Be Found");
        }
        LocalDateTime thisMoment = thisMomentSession.getThisMoment();
        if (flight.getDepartureTime().isBefore(thisMoment)) {
            throw new InvalidDataException("Ticket Can Not Be Unbooked Because The Flight Was Performed");
        }
        if (!ticket.isAvailable()) {
            throw new InvalidDataException("Ticket Is Not Available Now");
        }
        if (this.nonPurchasedBooking == null) {
            this.nonPurchasedBooking = new Booking();
            bookingRepository.save(this.nonPurchasedBooking);
        }
        ticket = ticketStatus.setBookedTicket(ticket, this.nonPurchasedBooking);
        return ticket;
    }

    public String bookTicketResult(Ticket ticket) {
        try {
            ticket = bookTicket(ticket);
            return "Book Ticket Successfully";
        } catch (InvalidDataException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public String bookTicketResult(long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId).orElse(null);
        return bookTicketResult(ticket);
    }

    public Ticket unbookedTicket(Ticket ticket) throws InvalidDataException {
        if (!loginSession.isLoggedIn()) {
            throw new InvalidDataException("Not Login Yet");
        }
        if (ticket == null) {
            throw new InvalidDataException("Ticket Can Not Be Found");
        }
        reloadBookingSession();
        ticket = ticketStatus.reloadTicketStatus(ticket);
        if (ticket.isPurchased()) {
            throw new InvalidDataException("Can Not Unbooked A Purchased Ticket");
        }
        Flight flight = flightRepository.findFlightsByTicketId(ticket.getId());
        if (flight == null) {
            throw new InvalidDataException("Flight Can Not Be Found");
        }
        LocalDateTime thisMoment = thisMomentSession.getThisMoment();
        if (flight.getDepartureTime().isBefore(thisMoment)) {
            throw new InvalidDataException("Ticket Can Not Be Unbooked Because The Flight Was Performed");
        }
        if (!isTicketBelongThisBooking(ticket)) {
            throw new InvalidDataException("This Ticket Is Not Belong To You");
        }
        ticket = ticketStatus.setUnBookedTicket(ticket);
        return ticket;
    }

    public String unBookTicketResult(Ticket ticket) {
        try {
            ticket = unbookedTicket(ticket);
            return "Unbooked Ticket";
        } catch (InvalidDataException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public String unBookTicketResult(long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId).orElse(null);
        return unBookTicketResult(ticket);
    }

    //**************************** TICKET PURCHASE AND REFUND ***********************************
    public String purchaseTicketResult(long ticketId) {
        LocalDateTime thisMoment = thisMomentSession.getThisMoment();
        BankAccount bankAccount = this.bankAccount;
        Account account = loginSession.getAccount();
        Ticket ticket = ticketRepository.findById(ticketId).get();
        Booking booking = new Booking(thisMoment, true, account);
        bookingRepository.save(booking);
        try {
            ticket.setBooking(booking);
            ticket = ticketService.purchaseTicket(ticket, bankAccount);
            return "Ticket Purchased Successfully";
        } catch (InvalidDataException e) {
            e.printStackTrace();
            return e.getMessage();
        } catch (InsufficientResourcesException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public String purchaseAllTicketResult() {
        List<Ticket> ticketList = this.nonPurchasedTicketList;
        BankAccount bankAccount = this.bankAccount;
        for (int i = 0; i < ticketList.size(); i++) {
            try {
                Ticket ticket = ticketService.purchaseTicket(ticketList.get(i), bankAccount);
            } catch (InvalidDataException e) {
                e.printStackTrace();
                return e.getMessage();
            } catch (InsufficientResourcesException e) {
                e.printStackTrace();
                return e.getMessage();
            }
        }
        this.nonPurchasedBooking.setPurchased(true);
        bookingRepository.save(this.nonPurchasedBooking);
        return "All Tickets Purchased Successfully";
    }

    public String refundTicketResult(long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId).get();
        try {
            ticket = ticketService.refundTicket(ticket, 0.7);
            return "Ticket Refunded, Check Your Bank Account";
        } catch (InvalidDataException e) {
            e.printStackTrace();
            return e.getMessage();
        } catch (InsufficientResourcesException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    //************************** PASSENGER *********************************
    public String setPassengerToTicketResult(long ticketId, String identity, String name,
                                             String address, String dob, String gender) {
        Ticket ticket = ticketRepository.findById(ticketId).orElse(null);
        if (isTicketBelongThisBooking(ticket)) {
            try {
                ticketService.setPassengerToTicket(ticket, identity, name, address, dob, gender);
                return "Passenger Set Successfully";
            } catch (InvalidDataException e) {
                e.printStackTrace();
                return e.getMessage();
            }
        }
        if (isTicketPurchasedByThisAccount(ticket)) {
            try {
                ticketService.setPassengerToTicket(ticket, identity, name, address, dob, gender);
                return "Passenger Set Successfully";
            } catch (InvalidDataException e) {
                e.printStackTrace();
                return e.getMessage();
            }
        }
        return "Passenger Set Fail, Try Again";
    }

    //**************************** PROMOTION TICKET *******************************
    public String usePromotionTicketResult(PromotionTicket promotionTicket, Ticket ticket) {
        try {
            ticketService.usePromotionTicket(promotionTicket, ticket);
            return "Add Promotion Ticket Success";
        } catch (InvalidDataException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public String usePromotionTicketResult(long promotionTicketId, long ticketId) {
        PromotionTicket promotionTicket = promotionTicketRepository.findById(promotionTicketId).orElse(null);
        Ticket ticket = ticketRepository.findById(ticketId).orElse(null);
        return usePromotionTicketResult(promotionTicket, ticket);
    }

    public String usePromotionTicketResult(String promotionTicketCode, long ticketId) {
        PromotionTicket promotionTicket = promotionTicketRepository.findPromotionTicketByCode(promotionTicketCode);
        Ticket ticket = ticketRepository.findById(ticketId).orElse(null);
        return usePromotionTicketResult(promotionTicket, ticket);
    }

    public String unUsedPromotionTicketResult(PromotionTicket promotionTicket) {
        try {
            ticketService.unUsedPromotionTicket(promotionTicket);
            return "Promotion Ticket Unused Success";
        } catch (InvalidDataException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public String unUsedPromotionTicketResult(long promotionTicketId) {
        PromotionTicket promotionTicket = promotionTicketRepository.findById(promotionTicketId).orElse(null);
        return unUsedPromotionTicketResult(promotionTicket);
    }
}
