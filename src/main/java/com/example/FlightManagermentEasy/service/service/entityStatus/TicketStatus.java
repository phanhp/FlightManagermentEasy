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

    //************************ PROMOTION TICKET **************************************
    //Reload Status
    public PromotionTicket reloadPromotionTicketStatus(PromotionTicket promotionTicket) {
        LocalDateTime thisMoment = thisMomentSession.getThisMoment();
        if (promotionTicket.getExpiredDate().isBefore(thisMoment)) {
            promotionTicket.setAvailable(false);
        }
        promotionTicketRepository.save(promotionTicket);
        return promotionTicket;
    }

    //Set Used
    public PromotionTicket setUsedPromotionTicket(PromotionTicket promotionTicket, Ticket ticket) {
        promotionTicket.setTicket(ticket);
        promotionTicket.setAvailable(false);
        promotionTicketRepository.save(promotionTicket);
        return promotionTicket;
    }

    //Set Unused
    public PromotionTicket setUnUsedPromotionTicket(PromotionTicket promotionTicket) {
        promotionTicket.setAvailable(true);
        promotionTicket.setTicket(null);
        promotionTicketRepository.save(promotionTicket);
        return promotionTicket;
    }

    //************************ PAYMENT HISTORY **************************************
    //Remove Payment From Ticket
    public PaymentHistory removeTicketFromPaymentHistory(PaymentHistory paymentHistory) {
        paymentHistory.setTicket(null);
        paymentHistoryRepository.save(paymentHistory);
        return paymentHistory;
    }

    //************************ TICKET **************************************
    //Remove All Payment From Ticket, Prepare For Delete
    public Ticket removePaymentHistoryListFromTicket(Ticket ticket) {
        List<PaymentHistory> paymentHistoryList = paymentHistoryRepository.findPaymentHistoryByTicketId(ticket.getId());
        if (paymentHistoryList != null) {
            for (int i = 0; i < paymentHistoryList.size(); i++) {
                if (paymentHistoryList.get(i) != null) {
                    removeTicketFromPaymentHistory(paymentHistoryList.get(i));
                }
            }
        }
        ticket.setPaymentHistoryList(null);
        return ticket;
    }

    //Remove All Promotion Ticket From Ticket
    public Ticket removePromotionTicketListFromTicket(Ticket ticket) throws RuntimeException {
        List<PromotionTicket> promotionTicketList = promotionTicketRepository.findPromotionTicketsByTicketId(ticket.getId());
        if (promotionTicketList != null) {
            for (int i = 0; i < promotionTicketList.size(); i++) {
                if (promotionTicketList.get(i) != null) {
                    setUnUsedPromotionTicket(promotionTicketList.get(i));
                }
            }
        }
        ticket.setPromotionTicketList(null);
        return ticket;
    }

    //Reload Ticket Status
    public Ticket reloadTicketStatus(Ticket ticket) {
        LocalDateTime thisMoment = thisMomentSession.getThisMoment();
        Flight flight = flightRepository.findFlightsByTicketId(ticket.getId());
        if (flight == null) {
            ticket.setAvailable(false);
        }
        if (flight.getDepartureTime().isBefore(thisMoment)) {
            ticket.setAvailable(false);
        } else {
            if (!ticket.isPurchased()) {
                if (!ticket.isAvailable()) {
                    if (ticket.getReopenTime().isBefore(thisMoment)) {
                        ticket.setBooking(null);
                        ticket.setPassenger(null);
                        ticket.setReopenTime(null);
                        ticket.setAvailable(true);
                        ticket = removePromotionTicketListFromTicket(ticket);
                    }
                }
            }
        }
        ticketRepository.save(ticket);
        return ticket;
    }

    //Set UnBooked Ticket
    public Ticket setUnBookedTicket(Ticket ticket) {
        ticket.setBooking(null);
        ticket.setPassenger(null);
        ticket.setReopenTime(null);
        ticket.setAvailable(true);
        ticket = removePromotionTicketListFromTicket(ticket);
        ticketRepository.save(ticket);
        return ticket;
    }

    //Set Booked Ticket
    public Ticket setBookedTicket(Ticket ticket, Booking booking) {
        LocalDateTime thisMoment = thisMomentSession.getThisMoment();
        ticket.setBooking(booking);
        ticket.setAvailable(false);
        ticket.setReopenTime(thisMoment.plusMinutes(10));
        ticketRepository.save(ticket);
        booking.setBookTime(thisMoment);
        bookingRepository.save(booking);
        return ticket;
    }

    //Set Purchase Ticket
    public Ticket setPurchasedTicket(Ticket ticket) {
        ticket.setPurchased(true);
        ticket.setAvailable(false);
        ticket.setReopenTime(null);
        ticketRepository.save(ticket);
        return ticket;
    }

    //Set Refund Ticket
    public Ticket setRefundTicket(Ticket ticket) {
        ticket.setPurchased(false);
        ticket.setAvailable(true);
        ticket.setBooking(null);
        ticket.setPassenger(null);
        ticket.setReopenTime(null);
        ticket = removePromotionTicketListFromTicket(ticket);
        ticketRepository.save(ticket);
        return ticket;
    }

    //Clear RelationShip Of Ticket
    public Ticket clearTicketRelationShip(Ticket ticket) {
        ticket = removePromotionTicketListFromTicket(ticket);
        ticket = removePaymentHistoryListFromTicket(ticket);
        return ticket;
    }

    //Reload Ticket List
    public List<Ticket> reloadTicketListStatus(List<Ticket> ticketList) {
        if (ticketList != null) {
            for (int i = 0; i < ticketList.size(); i++) {
                if (ticketList.get(i) != null) {
                    Ticket ticket = reloadTicketStatus(ticketList.get(i));
                    ticketList.set(i, ticket);
                }
            }
        }
        return ticketList;
    }
}
