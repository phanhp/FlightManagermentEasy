package com.example.FlightManagermentEasy.service.service;

import com.example.FlightManagermentEasy.exception.InvalidDataException;
import com.example.FlightManagermentEasy.entity.Ticket;
import com.example.FlightManagermentEasy.entity.flight.Flight;
import com.example.FlightManagermentEasy.entity.flight.aircraft.Cabin;
import com.example.FlightManagermentEasy.entity.flight.aircraft.Seat;
import com.example.FlightManagermentEasy.entity.user.Passenger;
import com.example.FlightManagermentEasy.entity.user.bank.BankAccount;
import com.example.FlightManagermentEasy.entity.user.bank.PaymentHistory;
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

import com.example.FlightManagermentEasy.service.session.LoginSession;
import com.example.FlightManagermentEasy.service.session.ThisMomentSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.InsufficientResourcesException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    LoginSession loginSession;
    @Autowired
    MUF muf;

    //************************ PASSENGER **************************************
    public Passenger findPassengerByTicketId(long ticketId) {
        Passenger passenger = passengerRepository.findPassengerByTicketId(ticketId);
        if (passenger == null) {
            passenger = new Passenger();
        }
        return passenger;
    }

    public String findPassengerNameByTicketId(long ticketId) {
        Passenger passenger = passengerRepository.findPassengerByTicketId(ticketId);
        if (passenger == null) {
            return "No Passenger Is Using This Ticket";
        } else {
            return "Ticket Owner: " + passenger.getName();
        }
    }

    public Passenger findPassengerByIdentity(String identity) {
        Passenger passenger = passengerRepository.findPassengerByIdentity(identity);
        if (passenger == null) {
            passenger = new Passenger();
        }
        return passenger;
    }

    public void setPassengerToTicket(Ticket ticket, String identity, String name,
                                     String address, String dob, String gender) throws InvalidDataException {
        if (ticket == null) {
            throw new InvalidDataException("Ticket Can Not Be Found");
        }
        LocalDateTime thisMoment = thisMomentSession.getThisMoment();
        Flight flight = flightRepository.findFlightsByTicketId(ticket.getId());
        if (flight == null) {
            throw new InvalidDataException("Flight Can Not Be Found");
        }
        if (flight.getDepartureTime().isBefore(thisMoment)) {
            throw new InvalidDataException("Can Not Set Passenger To This Flight");
        }
        ticket = ticketStatus.reloadTicketStatus(ticket);
        Passenger passenger = findPassengerByIdentity(identity);
        if (identity != null) {
            passenger.setIdentity(identity);
        } else {
            throw new InvalidDataException("Identity Can Not Be Empty");
        }
        if (name != null) {
            passenger.setName(name);
        } else {
            throw new InvalidDataException("Name Can Not Be Empty");
        }
        passenger.setAddress(address);
        passenger.setDob(muf.stringToLocalDate(dob));
        passenger.setGender(gender);
        passengerRepository.save(passenger);
        ticket.setPassenger(passenger);
        ticketRepository.save(ticket);
    }

    public void setPassengerToTicket(long ticketId, String identity, String name,
                                     String address, String dob, String gender) throws InvalidDataException {
        Ticket ticket = ticketRepository.findById(ticketId).orElse(null);
        try {
            setPassengerToTicket(ticket, identity, name, address, dob, gender);
        } catch (InvalidDataException e) {
            e.printStackTrace();
            throw new InvalidDataException(e.getMessage());
        }
    }

    //************************ SEAT **************************************
    public String findSeatNameByTicketId(long ticketId) {
        Seat seat = seatRepository.findSeatsByTicketId(ticketId);
        if (seat != null) {
            return seat.getName();
        }
        return "Empty Seat";
    }

    public String findCabinNameByTicketId(long ticketId) {
        Cabin cabin = cabinRepository.findCabinByTicketId(ticketId);
        if (cabin != null) {
            return cabin.getName();
        }
        return "Empty Cabin";
    }

    //******************************* PAYMENT HISTORY ********************************************
    @Transactional(rollbackFor = {InvalidDataException.class, InsufficientResourcesException.class})
    public void createPayment(Ticket ticket, BankAccount source, BankAccount receive, double amount) throws
            InvalidDataException, InsufficientResourcesException {
        if (!loginSession.isLoggedIn()) {
            System.out.println("You Have To Login To Create Payment");
            throw new InvalidDataException("You Have To Login To Create Payment");
        }
        if (source == null) {
            System.out.println("Source Bank Account Can Not Be Found");
            throw new InvalidDataException("Source Bank Account Can Not Be Found");
        }
        if (receive == null) {
            System.out.println("Receive Bank Account Can Not Be Found");
            throw new InvalidDataException("Receive Bank Account Can Not Be Found");
        }
        if (ticket == null) {
            System.out.println("Ticket Could Not Be Found");
            throw new InvalidDataException("Ticket Could Not Be Found");
        }
        LocalDateTime thisMoment = thisMomentSession.getThisMoment();
        Flight flight = flightRepository.findFlightsByTicketId(ticket.getId());
        if (flight == null) {
            System.out.println("Flight Can Not Be Found");
            throw new InvalidDataException("Flight Can Not Be Found");
        } else {
            if (flight.getDepartureTime().isBefore(thisMoment)) {
                System.out.println("Flight Was Performed, Can Not Create Payment For This Ticket");
                throw new InvalidDataException("Flight Was Performed, Can Not Create Payment For This Ticket");
            }
        }
        source.setBalance(source.getBalance() - amount);
        if (source.getBalance() < 0) {
            System.out.println("Source Bank Account Dont Have Enough Money");
            throw new InsufficientResourcesException("Source Bank Account Dont Have Enough Money");
        }
        double securityPayment = receive.getBalance();
        receive.setBalance(receive.getBalance() + amount);
        if (receive.getBalance() < 0 || receive.getBalance() < securityPayment) {
            System.out.println("Payment Cancel Due To Some Un-Expected Problems");
            throw new InsufficientResourcesException("Payment Cancel Due To Some Un-Expected Problems");
        }
        bankAccountRepository.save(source);
        bankAccountRepository.save(receive);
        PaymentHistory paymentHistory = new PaymentHistory();
        paymentHistory.setTicket(ticket);
        paymentHistory.setAmount(amount);
        paymentHistory.setTime(thisMoment);
        paymentHistory.setSourceAccount(source);
        paymentHistory.setReceiveAccount(receive);
        paymentHistoryRepository.save(paymentHistory);
    }

    @Transactional(rollbackFor = {InvalidDataException.class, InsufficientResourcesException.class})
    public Ticket purchaseTicket(Ticket ticket, BankAccount source) throws
            InvalidDataException, InsufficientResourcesException {
        BankAccount receive = bankAccountRepository.findBankAccountByName("FlightManagement");
        if (ticket == null) {
            System.out.println("Ticket Could Not Be Found");
            throw new InvalidDataException("Ticket Could Not Be Found");
        }
        ticket = ticketStatus.reloadTicketStatus(ticket);
        if (ticket.isPurchased()) {
            System.out.println("Ticket Was Purchased By Another Account");
            throw new InvalidDataException("Ticket Was Purchased By Another Account");
        }
        double amount = ticketCost(ticket);
        try {
            createPayment(ticket, source, receive, amount);
            return ticketStatus.setPurchasedTicket(ticket);
        } catch (InsufficientResourcesException e) {
            throw new InsufficientResourcesException(e.getMessage());
        } catch (InvalidDataException e) {
            throw new InvalidDataException(e.getMessage());
        }
    }

    @Transactional(rollbackFor = {InvalidDataException.class, InsufficientResourcesException.class})
    public Ticket refundTicket(Ticket ticket, double percentRefund) throws
            InsufficientResourcesException, InvalidDataException {
        if (ticket == null) {
            throw new InvalidDataException("Ticket Could Not Be Found");
        }
        ticket = ticketStatus.reloadTicketStatus(ticket);
        if (!ticket.isPurchased()) {
            throw new InvalidDataException("Ticket Was Not Purchased Yet, Can Not Refund");
        }
        List<PaymentHistory> paymentHistoryList = paymentHistoryRepository.findPaymentHistoryByTicketId(ticket.getId());
        if (paymentHistoryList == null) {
            throw new InvalidDataException("Can Not Find Payment History Of This Ticket");
        }
        if (paymentHistoryList.isEmpty()) {
            throw new InvalidDataException("Can Not Find Payment History Of This Ticket");
        }
        PaymentHistory oldPaymentHistory = paymentHistoryList.get(paymentHistoryList.size() - 1);
        BankAccount source = oldPaymentHistory.getReceiveAccount();
        BankAccount receive = oldPaymentHistory.getSourceAccount();
        double amount = oldPaymentHistory.getAmount() * percentRefund;
        try {
            createPayment(ticket, source, receive, amount);
            return ticketStatus.setRefundTicket(ticket);
        } catch (InvalidDataException e) {
            throw new InvalidDataException(e.getMessage());
        } catch (InsufficientResourcesException e) {
            throw new InsufficientResourcesException(e.getMessage());
        }
    }

    //************************ PROMOTION TICKET *************************************
    public void unUsedPromotionTicket(PromotionTicket promotionTicket) throws InvalidDataException {
        if (promotionTicket == null) {
            throw new InvalidDataException("Promotion Ticket Can Not Be Found");
        }
        Ticket ticket = ticketRepository.findTicketByPromotionTicketId(promotionTicket.getId());
        LocalDateTime thisMoment = thisMomentSession.getThisMoment();
        if (ticket != null) {
            if (ticket.isPurchased()) {
                throw new InvalidDataException("Can Not Un Used Promotion Ticket On A Purchased Ticket");
            }
            Flight flight = flightRepository.findFlightsByTicketId(ticket.getId());
            if (flight != null) {
                if (flight.getDepartureTime().isBefore(thisMoment)) {
                    throw new InvalidDataException("Can Not Un Used Promotion Ticket Because Flight Was Performed");
                }
            }
            promotionTicket.setTicket(null);
            promotionTicket.setAvailable(true);
        }
        if (promotionTicket.getExpiredDate().isBefore(thisMoment)) {
            promotionTicket.setAvailable(false);
            promotionTicketRepository.save(promotionTicket);
            throw new InvalidDataException("Can Not Un Used Promotion Ticket Because It Was Expired");
        }
        promotionTicketRepository.save(promotionTicket);
    }

    public void usePromotionTicket(PromotionTicket promotionTicket, Ticket ticket) throws InvalidDataException {
        if (promotionTicket == null) {
            throw new InvalidDataException("Promotion Ticket Can Not Be Found");
        }
        if (ticket == null) {
            throw new InvalidDataException("Ticket Can Not Be Found");
        }
        LocalDateTime thisMoment = thisMomentSession.getThisMoment();
        if (ticket.isPurchased()) {
            throw new InvalidDataException("Can Not Used Promotion Ticket On A Purchased Ticket");
        }
        Flight flight = flightRepository.findFlightsByTicketId(ticket.getId());
        if (flight == null) {
            throw new InvalidDataException("Flight Can Not Be Found");
        }
        if (flight.getDepartureTime().isBefore(thisMoment)) {
            throw new InvalidDataException("Can Not Un Used Promotion Ticket Because Flight Was Performed");
        }
        if (promotionTicket.getExpiredDate().isBefore(thisMoment)) {
            throw new InvalidDataException("Can Not Un Used Promotion Ticket Because It Was Expired");
        }
        if (!promotionTicket.isAvailable()) {
            throw new InvalidDataException("Promotion Ticket Is Not Available");
        }
        promotionTicket.setTicket(ticket);
        promotionTicket.setAvailable(false);
        promotionTicketRepository.save(promotionTicket);
    }

    //************************ TICKET **************************************
    //Reload Ticket From A List
    public List<Ticket> reLoadTicketListBySeatRowIdAndFlightId(long seatRowId, long flightId) {
        List<Ticket> ticketList = ticketRepository.findTicketsBySeatRowIdAndFlightId(seatRowId, flightId);
        if (ticketList != null) {
            return ticketStatus.reloadTicketListStatus(ticketList);
        }
        return new ArrayList<>();
    }

    //CRUD TICKET
    //Create New Ticket
    public void createNewTicket(double price, Seat seat, Flight flight) throws InvalidDataException {
        if (flight == null) {
            throw new InvalidDataException("Flight Can Not Be Found");
        }
        Ticket ticket = new Ticket(price, true, false, seat, flight);
        ticketRepository.save(ticket);
    }

    //Delete
    public void deleteTicket(Ticket ticket) throws InvalidDataException {
        if (ticket == null) {
            throw new InvalidDataException("Ticket Can Not Be Found");
        }
        ticket = ticketStatus.clearTicketRelationShip(ticket);
        ticketRepository.delete(ticket);
    }

    //Resolve Ticket Price
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

}
