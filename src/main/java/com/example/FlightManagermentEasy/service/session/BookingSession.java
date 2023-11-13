package com.example.FlightManagermentEasy.service.session;

import com.example.FlightManagermentEasy.entity.MyTime;
import com.example.FlightManagermentEasy.entity.Ticket;
import com.example.FlightManagermentEasy.entity.flight.Flight;
import com.example.FlightManagermentEasy.entity.user.Account;
import com.example.FlightManagermentEasy.entity.user.bank.BankAccount;
import com.example.FlightManagermentEasy.entity.user.booking.Booking;
import com.example.FlightManagermentEasy.repository.MyTimeRepository;
import com.example.FlightManagermentEasy.repository.TicketRepository;
import com.example.FlightManagermentEasy.repository.flight.FlightRepository;
import com.example.FlightManagermentEasy.repository.flight.aircraft.SeatRepository;
import com.example.FlightManagermentEasy.repository.user.booking.BookingRepository;
import com.example.FlightManagermentEasy.repository.user.user.AccountRepository;
import com.example.FlightManagermentEasy.service.data.MyTimeData;
import com.example.FlightManagermentEasy.service.service.TicketService;
import com.example.FlightManagermentEasy.service.service.entityStatus.TicketStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.annotation.SessionScope;

import java.sql.Array;
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
    TicketStatus ticketStatus;

    private List<Ticket> nonPurchasedTicketList;

    private List<Ticket> purchasedTickedList;

    private List<Ticket> flightedTicketList;

    private Booking nonPurchasedBooking;

    private List<Booking> purchasedBookingList;

    private BankAccount bankAccount;

    //reload booking session
    public void reloadBookingSession() {
        if (loginSession.isLoggedIn()) {
            Account account = loginSession.getAccount();
            LocalDateTime thisMoment = thisMomentSession.getThisMoment();

            //nonPurchasedTicketList reload
            List<Ticket> nonPurchasedTicketList = ticketRepository.findNonPurchaseTicketsByAccountId(account.getId());
            if (nonPurchasedTicketList == null) {
                nonPurchasedTicketList = new ArrayList<>();
            }
            this.nonPurchasedTicketList = ticketStatus.reloadTicketListStatus(nonPurchasedTicketList);

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
                } else {
                    return false;
                }
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
                if (ticket.getBooking().getId() == this.nonPurchasedBooking.getId()) {
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
        try {
            return isTicketDisableWithThisBooking(ticket);
        } catch (Exception e) {
            return true;
        }
    }

    //is ticket belong this booking
    public boolean isTicketBelongThisBooking(Ticket ticket) {
        reloadBookingSession();
        if (loginSession.isLoggedIn()) {
            ticket = ticketStatus.reloadTicketStatus(ticket);
            if (ticket.isPurchased()) {
                return false;
            } else {
                if (ticket.isAvailable()) {
                    return false;
                } else if (ticket.getBooking().getId() == this.nonPurchasedBooking.getId()) {
                    return true;
                } else {
                    return false;
                }
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

    //each time booking ticket from flight,
//remove the previous selected seat that is not selected in this booking time
//add new selected seat that is selected in this booking time
//only effect on this flight id
    public void addListTicketToCartBySelectedId(List<Long> selectedTicketIdList, long flightId) {
        reloadBookingSession();
        if (loginSession.isLoggedIn()) {
            Map<Long, List<Ticket>> ticketListMap = viewBookedTicketMap();
            List<Ticket> ticketListWithFlightIdInCart;
            List<Long> ticketIdListWithFlightIdInCart = new ArrayList<>();
            if (isFlightIdInNonPurchasedTicketList(flightId)) {
                ticketListWithFlightIdInCart = ticketListMap.get(flightId);
                for (int i = 0; i < ticketListWithFlightIdInCart.size(); i++) {
                    ticketIdListWithFlightIdInCart.add(ticketListWithFlightIdInCart.get(i).getId());
                }
            }
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
                        ticket = ticketStatus.setUnBookedTicket(ticket);
                    }
                }
            }
            for (int i = 0; i < selectedTicketIdList.size(); i++) {
                long ticketId = selectedTicketIdList.get(i);
                Ticket ticket = ticketRepository.findById(ticketId).orElse(null);
                if (ticket != null) {
                    if (!isTicketDisableWithThisBooking(ticket)) {
                        ticket = ticketStatus.setBookedTicket(ticket, this.nonPurchasedBooking);
                    }
                }
            }
        }
    }

    //Separate non-purchased ticket in list by flight id
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

    public int numberNonPurchasedTicket(){
        if(this.nonPurchasedTicketList == null){
            return 0;
        }
        return this.nonPurchasedTicketList.size();
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


    //purchase ticket
    //purchase single ticket
    public boolean isReadyForTransaction() {
        if (loginSession.isLoggedIn()) {
            reloadBookingSession();
            BankAccount bankAccount = this.bankAccount;
            if (bankAccount == null) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean isTicketReadyForTransaction(Ticket ticket) {
        LocalDateTime thisMoment = thisMomentSession.getThisMoment();
        Flight flight = flightRepository.findFlightsByTicketId(ticket.getId());
        if (flight == null) {
            return false;
        }
        if (flight.getDepartureTime().isBefore(thisMoment)) {
            return false;
        }
        return true;
    }

    public boolean isTicketReadyForTransaction(long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId).orElse(null);
        if (ticket == null) {
            return false;
        }
        return isTicketReadyForTransaction(ticket);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean purchaseSingleTicket(long ticketId) {
        if (isReadyForTransaction()) {
            if (isTicketReadyForTransaction(ticketId)) {
                LocalDateTime thisMoment = thisMomentSession.getThisMoment();
                BankAccount bankAccount = this.bankAccount;
                Account account = loginSession.getAccount();
                Ticket ticket = ticketRepository.findById(ticketId).get();
                Booking booking = new Booking(thisMoment, true, account);
                bookingRepository.save(booking);
                try {
                    ticket.setBooking(booking);
                    ticket = ticketService.purchaseTicket(ticket, bankAccount);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    //purchase all ticket in non purchase ticket list
    @Transactional(rollbackFor = Exception.class)
    public boolean purchaseAllTicketInCart() {
        List<Ticket> ticketList = this.nonPurchasedTicketList;
        if (isReadyForTransaction()) {
            BankAccount bankAccount = this.bankAccount;
            try {
                for (int i = 0; i < ticketList.size(); i++) {
                    if (isTicketReadyForTransaction(ticketList.get(i))) {
                        Ticket ticket = ticketService.purchaseTicket(ticketList.get(i), bankAccount);
                    }
                }
                this.nonPurchasedBooking.setPurchased(true);
                bookingRepository.save(this.nonPurchasedBooking);
                return true;
            } catch (Exception e) {
                return false;
            }
        } else {
            return false;
        }
    }

    //refund ticket
    @Transactional(rollbackFor = Exception.class)
    public boolean refundTicket(long ticketId) {
        if (isReadyForTransaction()) {
            if (isTicketReadyForTransaction(ticketId)) {
                Ticket ticket = ticketRepository.findById(ticketId).get();
                try {
                    ticket = ticketService.refundTicket(ticket, 0.7);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
