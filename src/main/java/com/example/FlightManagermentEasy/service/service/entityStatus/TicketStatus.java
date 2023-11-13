package com.example.FlightManagermentEasy.service.service.entityStatus;

import com.example.FlightManagermentEasy.entity.Ticket;
import com.example.FlightManagermentEasy.entity.flight.Flight;
import com.example.FlightManagermentEasy.entity.user.bank.PaymentHistory;
import com.example.FlightManagermentEasy.entity.user.booking.Booking;
import com.example.FlightManagermentEasy.entity.user.booking.PromotionTicket;
import com.example.FlightManagermentEasy.repository.TicketRepository;
import com.example.FlightManagermentEasy.repository.flight.FlightRepository;
import com.example.FlightManagermentEasy.repository.user.bank.PaymentHistoryRepository;
import com.example.FlightManagermentEasy.repository.user.booking.BookingRepository;
import com.example.FlightManagermentEasy.repository.user.booking.PromotionTicketRepository;
import com.example.FlightManagermentEasy.service.session.ThisMomentSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TicketStatus {
    @Autowired
    TicketRepository ticketRepository;
    @Autowired
    FlightRepository flightRepository;
    @Autowired
    PromotionTicketRepository promotionTicketRepository;
    @Autowired
    ThisMomentSession thisMomentSession;
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    PaymentHistoryRepository paymentHistoryRepository;

    //Promotion Ticket
    //reload status
    public PromotionTicket reloadPromotionTicketStatus(PromotionTicket promotionTicket) {
        LocalDateTime thisMoment = thisMomentSession.getThisMoment();
        if (promotionTicket.isAvailable()) {
            if (promotionTicket.getExpiredDate().isBefore(thisMoment)) {
                promotionTicket.setAvailable(false);
            }
        }
        promotionTicketRepository.save(promotionTicket);
        return promotionTicket;
    }

    public PromotionTicket reloadPromotionTicketStatus(long promotionTicketId) {
        PromotionTicket promotionTicket = promotionTicketRepository.findById(promotionTicketId).orElse(null);
        if (promotionTicket != null) {
            promotionTicket = reloadPromotionTicketStatus(promotionTicket);
        }
        return promotionTicket;
    }

    //PaymentHistory
    public PaymentHistory removeTicketFromPaymentHistory(PaymentHistory paymentHistory) {
        paymentHistory.setTicket(null);
        paymentHistoryRepository.save(paymentHistory);
        return paymentHistory;
    }

    public List<PaymentHistory> removeAllPaymentHistoryFromTicket(Ticket ticket) {
        List<PaymentHistory> paymentHistoryList = paymentHistoryRepository.findPaymentHistoryByTicketId(ticket.getId());
        if (paymentHistoryList != null) {
            for (int i = 0; i < paymentHistoryList.size(); i++) {
                PaymentHistory paymentHistory = removeTicketFromPaymentHistory(paymentHistoryList.get(i));
                paymentHistoryList.set(i, paymentHistory);
            }
        }
        return paymentHistoryList;
    }

    //set used status
    public PromotionTicket setUsedPromotionTicket(PromotionTicket promotionTicket, Ticket ticket) {
        promotionTicket = reloadPromotionTicketStatus(promotionTicket);
        if (!ticket.isPurchased()) {
            if (promotionTicket.isAvailable()) {
                promotionTicket.setAvailable(false);
                promotionTicket.setTicket(ticket);
            }
        }
        promotionTicketRepository.save(promotionTicket);
        return promotionTicket;
    }

    public PromotionTicket setUsedPromotionTicket(long promotionTicketId, Ticket ticket) {
        PromotionTicket promotionTicket = promotionTicketRepository.findById(promotionTicketId).orElse(null);
        if (promotionTicket != null) {
            promotionTicket = setUsedPromotionTicket(promotionTicket, ticket);
        }
        return promotionTicket;
    }

    public PromotionTicket setUsedPromotionTicket(long promotionTicketId, long ticketId) {
        PromotionTicket promotionTicket = promotionTicketRepository.findById(promotionTicketId).orElse(null);
        Ticket ticket = ticketRepository.findById(ticketId).orElse(null);
        if (promotionTicket != null && ticket != null) {
            promotionTicket = setUsedPromotionTicket(promotionTicket, ticket);
        }
        return promotionTicket;
    }

    //set unUsed status
    public PromotionTicket setUnUsedPromotionTicket(PromotionTicket promotionTicket) {
        promotionTicket = reloadPromotionTicketStatus(promotionTicket);
        LocalDateTime thisMoment = thisMomentSession.getThisMoment();
        if (promotionTicket.getExpiredDate().isAfter(thisMoment)) {
            promotionTicket.setAvailable(true);
            promotionTicket.setTicket(null);
        } else {
            promotionTicket.setTicket(null);
        }
        promotionTicketRepository.save(promotionTicket);
        return promotionTicket;
    }

    public PromotionTicket setUnUsedPromotionTicket(long promotionTicketId) {
        PromotionTicket promotionTicket = promotionTicketRepository.findById(promotionTicketId).orElse(new PromotionTicket());
        if (promotionTicket != null) {
            promotionTicket = setUnUsedPromotionTicket(promotionTicket);
        }
        return promotionTicket;
    }

    public List<PromotionTicket> setUnUsedPromotionTicketListFromTicket(Ticket ticket) {
        List<PromotionTicket> promotionTicketList = promotionTicketRepository.findPromotionTicketsByTicketId(ticket.getId());
        if (promotionTicketList != null) {
            for (int i = 0; i < promotionTicketList.size(); i++) {
                PromotionTicket promotionTicket = setUnUsedPromotionTicket(promotionTicketList.get(i));
                promotionTicketList.set(i, promotionTicket);
            }
        }
        return promotionTicketList;
    }

    //Ticket
    //reload ticket status
    public Ticket reloadTicketStatus(Ticket ticket) {
        LocalDateTime thisMoment = thisMomentSession.getThisMoment();
        if (!ticket.isPurchased()) {
            if (!ticket.isAvailable()) {
                if (ticket.getReopenTime().isBefore(thisMoment)) {
                    ticket.setBooking(null);
                    ticket.setPassenger(null);
                    ticket.setReopenTime(null);
                    ticket.setAvailable(true);
                    List<PromotionTicket> promotionTicketList = setUnUsedPromotionTicketListFromTicket(ticket);
                }
            }
        }
        Flight flight = flightRepository.findFlightsByTicketId(ticket.getId());
        if (flight == null) {
            ticket.setAvailable(false);
        }
        if (flight.getDepartureTime().isBefore(thisMoment)) {
            ticket.setAvailable(false);
        }
        ticketRepository.save(ticket);
        return ticket;
    }

    public Ticket reloadTicketStatus(long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId).orElse(null);
        if (ticket != null) {
            ticket = reloadTicketStatus(ticket);
        }
        return ticket;
    }

    //set unBooked ticket
    public Ticket setUnBookedTicket(Ticket ticket) {
        if (!ticket.isPurchased()) {
            ticket.setBooking(null);
            ticket.setPassenger(null);
            ticket.setReopenTime(null);
            ticket.setAvailable(true);
            List<PromotionTicket> promotionTicketList = setUnUsedPromotionTicketListFromTicket(ticket);
        }
        ticketRepository.save(ticket);
        return ticket;
    }

    public Ticket setUnBookedTicket(long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId).orElse(null);
        if (ticket != null) {
            ticket = setUnBookedTicket(ticket);
        }
        return ticket;
    }

    //set booked ticket
    public Ticket setBookedTicket(Ticket ticket, Booking booking) {
        ticket = reloadTicketStatus(ticket);
        LocalDateTime thisMoment = thisMomentSession.getThisMoment();
        if (!ticket.isPurchased()) {
            ticket.setBooking(booking);
            ticket.setAvailable(false);
            ticket.setReopenTime(thisMoment.plusMinutes(10));
        }
        ticketRepository.save(ticket);
        booking.setBookTime(thisMoment);
        bookingRepository.save(booking);
        return ticket;
    }

    //set purchased ticket
    public Ticket setPurchasedTicket(Ticket ticket) {
        if (!ticket.isPurchased()) {
            ticket.setPurchased(true);
            ticket.setAvailable(false);
            ticket.setReopenTime(null);
        }
        ticketRepository.save(ticket);
        return ticket;
    }

    public Ticket setPurchasedTicket(long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId).orElse(null);
        if (ticket != null) {
            setPurchasedTicket(ticket);
        }
        return ticket;
    }

    //set refund ticket
    public Ticket setRefundTicket(Ticket ticket) {
        LocalDateTime thisMoment = thisMomentSession.getThisMoment();
        if (ticket.isPurchased()) {
            Flight flight = flightRepository.findFlightsByTicketId(ticket.getId());
            ticket.setPurchased(false);
            if (flight != null && flight.getDepartureTime().isAfter(thisMoment)) {
                ticket.setAvailable(true);
            }
            ticket.setBooking(null);
            ticket.setPassenger(null);
            ticket.setReopenTime(null);
            List<PromotionTicket> promotionTicketList = setUnUsedPromotionTicketListFromTicket(ticket);
        }
        ticketRepository.save(ticket);
        return ticket;
    }

    public Ticket setRefundTicket(long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId).orElse(null);
        if (ticket != null) {
            ticket = setRefundTicket(ticket);
        }
        return ticket;
    }

    //clear ticket relationship on ticket
    public void clearTicketRelationShip(Ticket ticket) {
        List<PromotionTicket> promotionTicketList = setUnUsedPromotionTicketListFromTicket(ticket);
        List<PaymentHistory> paymentHistoryList = removeAllPaymentHistoryFromTicket(ticket);
    }

    //reload ticket list
    public List<Ticket> reloadTicketListStatus(List<Ticket> ticketList) {
        for (int i = 0; i < ticketList.size(); i++) {
            Ticket ticket = reloadTicketStatus(ticketList.get(i));
            ticketList.set(i, ticket);
        }
        return ticketList;
    }
}
