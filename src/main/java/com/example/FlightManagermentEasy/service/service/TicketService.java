package com.example.FlightManagermentEasy.service.service;

import com.example.FlightManagermentEasy.entity.Ticket;
import com.example.FlightManagermentEasy.entity.flight.Flight;
import com.example.FlightManagermentEasy.entity.flight.aircraft.Cabin;
import com.example.FlightManagermentEasy.entity.flight.aircraft.Seat;
import com.example.FlightManagermentEasy.entity.user.Passenger;
import com.example.FlightManagermentEasy.entity.user.bank.BankAccount;
import com.example.FlightManagermentEasy.entity.user.bank.PaymentHistory;
import com.example.FlightManagermentEasy.entity.user.booking.Booking;
import com.example.FlightManagermentEasy.entity.user.booking.PromotionTicket;
import com.example.FlightManagermentEasy.repository.TicketRepository;
import com.example.FlightManagermentEasy.repository.flight.FlightRepository;
import com.example.FlightManagermentEasy.repository.flight.aircraft.CabinRepository;
import com.example.FlightManagermentEasy.repository.flight.aircraft.SeatRepository;
import com.example.FlightManagermentEasy.repository.user.bank.BankAccountRepository;
import com.example.FlightManagermentEasy.repository.user.bank.PaymentHistoryRepository;
import com.example.FlightManagermentEasy.repository.user.booking.BookingRepository;
import com.example.FlightManagermentEasy.repository.user.booking.PromotionTicketRepository;
import com.example.FlightManagermentEasy.repository.user.user.PassengerRepository;
import com.example.FlightManagermentEasy.service.MUF;
import com.example.FlightManagermentEasy.service.service.entityStatus.TicketStatus;
import com.example.FlightManagermentEasy.service.session.ThisMomentSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.naming.InsufficientResourcesException;
import javax.sql.DataSource;
import javax.swing.plaf.PanelUI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Service
public class TicketService {
    @Autowired
    ThisMomentSession thisMomentSession;
    @Autowired
    TicketRepository ticketRepository;
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    PassengerRepository passengerRepository;
    @Autowired
    BankAccountRepository bankAccountRepository;
    @Autowired
    FlightRepository flightRepository;
    @Autowired
    PaymentHistoryRepository paymentHistoryRepository;
    @Autowired
    PromotionTicketRepository promotionTicketRepository;
    @Autowired
    CabinRepository cabinRepository;
    @Autowired
    TicketStatus ticketStatus;
    @Autowired
    SeatRepository seatRepository;
    @Autowired
    MUF muf;

    public List<Ticket> reLoadTicketListBySeatRowIdAndFlightId(long seatRowId, long flightId) {
        List<Ticket> ticketList = ticketRepository.findTicketsBySeatRowIdAndFlightId(seatRowId, flightId);
        return ticketStatus.reloadTicketListStatus(ticketList);
    }

    //Passenger Handle
    public Passenger findPassengerByTicketId(long ticketId) {
        Passenger passenger = passengerRepository.findPassengerByTicketId(ticketId);
        if (passenger == null) {
            return new Passenger(true);
        }
        return passenger;
    }

    public String findPassengerNameByTicketId(long ticketId){
        Passenger passenger = passengerRepository.findPassengerByTicketId(ticketId);
        if (passenger == null){
            return "No Passenger Is Using This Ticket";
        }else {
            return "Ticket Owner: "+passenger.getName();
        }
    }

    public Passenger findPassengerByIdentity(String identity) {
        Passenger passenger = passengerRepository.findPassengerByIdentity(identity);
        if (passenger == null) {
            return new Passenger(true);
        }
        return passenger;
    }

    public void setPassengerToTicket(long ticketId,
                                     String identity,
                                     String name,
                                     String address,
                                     String dob,
                                     String gender) {
        Ticket ticket = ticketRepository.findById(ticketId).orElse(null);
        LocalDateTime thisMoment = thisMomentSession.getThisMoment();
        if (ticket != null) {
            Flight flight = flightRepository.findFlightsByTicketId(ticketId);
            if (flight.getDepartureTime().isAfter(thisMoment)) {
                Passenger passenger = findPassengerByIdentity(identity);
                if (passenger == null) {
                    passenger = new Passenger();
                }

                passenger.setIdentity(identity);
                passenger.setName(name);
                passenger.setAddress(address);
                if (dob != null) {
                    if (!dob.isEmpty()) {
                        passenger.setDob(muf.stringToLocalDate(dob));
                    }
                }
                passenger.setGender(gender);
                passengerRepository.save(passenger);
                ticket.setPassenger(passenger);
                ticketRepository.save(ticket);
            }
        }
    }

    //Seat handle
    public String findSeatNameByTicketId(long ticketId){
        Seat seat = seatRepository.findSeatsByTicketId(ticketId);
        if (seat != null){
            return seat.getName();
        }
        return "Can Not Find This Seat";
    }

    public String findCabinNameByTicketId(long ticketId){
        Cabin cabin = cabinRepository.findCabinByTicketId(ticketId);
        if (cabin!=null){
            return cabin.getName();
        }
        return "Can Not Find This Cabin";
    }

    //Payment Handle
    public void createPayment(Ticket ticket, BankAccount source, BankAccount receive, double amount) throws RuntimeException {
        LocalDateTime thisMoment = thisMomentSession.getThisMoment();
        PaymentHistory paymentHistory = new PaymentHistory();
        if (source.getBalance() < amount) {
            throw new RuntimeException();
        }
        source.setBalance(source.getBalance() - amount);
        receive.setBalance(receive.getBalance() + amount);
        bankAccountRepository.save(source);
        bankAccountRepository.save(receive);
        paymentHistory.setTicket(ticket);
        paymentHistory.setAmount(amount);
        paymentHistory.setTime(thisMoment);
        paymentHistory.setSourceAccount(source);
        paymentHistory.setReceiveAccount(receive);
        paymentHistoryRepository.save(paymentHistory);
    }

    @Transactional(rollbackFor = Exception.class)
    public Ticket purchaseTicket(Ticket ticket, BankAccount source) throws RuntimeException {
        if (!ticket.isPurchased()) {
            BankAccount receive = bankAccountRepository.findBankAccountByName("FlightManagement");
            double amount = ticketCost(ticket);
            try {
                createPayment(ticket, source, receive, amount);
            } catch (Exception e) {
                throw new RuntimeException();
            }
            return ticketStatus.setPurchasedTicket(ticket);
        } else {
            return ticket;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Ticket refundTicket(Ticket ticket, double percentRefund) throws RuntimeException {
        if (ticket.isPurchased()) {
            List<PaymentHistory> paymentHistoryList = ticket.getPaymentHistoryList();
            if (paymentHistoryList.isEmpty()) {
                throw new RuntimeException();
            }
            PaymentHistory oldPaymentHistory = paymentHistoryList.get(paymentHistoryList.size() - 1);
            BankAccount source = oldPaymentHistory.getReceiveAccount();
            BankAccount receive = oldPaymentHistory.getSourceAccount();
            double amount = oldPaymentHistory.getAmount() * percentRefund;
            try {
                createPayment(ticket, source, receive, amount);
            } catch (Exception e) {
                throw new RuntimeException();
            }
            return ticketStatus.setRefundTicket(ticket);
        } else {
            return ticket;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Ticket refundTicket(long ticketId, double percentRefund) {
        Ticket ticket = ticketRepository.findById(ticketId).orElse(null);
        if (ticket != null) {
            return refundTicket(ticket, percentRefund);
        } else {
            return null;
        }
    }

    //Promotion-ticket handle
    public List<PromotionTicket> findPromotionTicketListByTicketId(long ticketId){
        return promotionTicketRepository.findPromotionTicketsByTicketId(ticketId);
    }

    public double priceReductionForTicket(Ticket ticket) {
        List<PromotionTicket> promotionTicketList = promotionTicketRepository.findPromotionTicketsByTicketId(ticket.getId());
        if (promotionTicketList == null) {
            return 0;
        } else {
            double remainPercent = 1;
            for (int i = 0; i < promotionTicketList.size(); i++) {
                remainPercent = remainPercent * (1 - promotionTicketList.get(i).getValue());
            }
            return (1 - remainPercent);
        }
    }

    public double ticketCost(Ticket ticket) {
        return ticket.getPrice() * (1 - priceReductionForTicket(ticket));
    }

    //CRUD Ticket
    //create new ticket
    public void createNewTicket(double price, Seat seat, Flight flight) {
        Ticket ticket = new Ticket(price, true, false, seat, flight);
        ticketRepository.save(ticket);
    }

    //Delete
    public void deleteTicket(Ticket ticket) {
        ticketStatus.clearTicketRelationShip(ticket);
        ticketRepository.delete(ticket);
    }

}
