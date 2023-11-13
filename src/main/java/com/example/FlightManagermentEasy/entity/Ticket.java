package com.example.FlightManagermentEasy.entity;

import com.example.FlightManagermentEasy.entity.flight.Flight;
import com.example.FlightManagermentEasy.entity.flight.aircraft.Seat;
import com.example.FlightManagermentEasy.entity.user.Passenger;
import com.example.FlightManagermentEasy.entity.user.bank.PaymentHistory;
import com.example.FlightManagermentEasy.entity.user.booking.Booking;
import com.example.FlightManagermentEasy.entity.user.booking.PromotionTicket;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tickets")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "price")
    private double price;
    @Column(name = "available")
    private boolean available;
    @Column(name = "purchased")
    private boolean purchased;
    @Column(name = "reopen_time")
    private LocalDateTime reopenTime;
    @ManyToOne
    @JoinColumn(name = "passenger_id")
    private Passenger passenger;
    @ManyToOne
    @JoinColumn(name = "seat_id")
    private Seat seat;
    @ManyToOne
    @JoinColumn(name = "flight_id")
    private Flight flight;
    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL)
    private List<PaymentHistory> paymentHistoryList;
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL)
    private List<PromotionTicket> promotionTicketList;

    public Ticket() {
    }

    public Ticket(double price, boolean available, boolean purchased, Seat seat, Flight flight) {
        this.price = price;
        this.available = available;
        this.purchased = purchased;
        this.seat = seat;
        this.flight = flight;
    }

    public Ticket(long id, double price, boolean available, boolean purchased, LocalDateTime reopenTime, Passenger passenger, Seat seat, Flight flight, Booking booking, List<PaymentHistory> paymentHistoryList, List<PromotionTicket> promotionTicketList) {
        this.id = id;
        this.price = price;
        this.available = available;
        this.purchased = purchased;
        this.reopenTime = reopenTime;
        this.passenger = passenger;
        this.seat = seat;
        this.flight = flight;
        this.booking = booking;
        this.paymentHistoryList = paymentHistoryList;
        this.promotionTicketList = promotionTicketList;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isPurchased() {
        return purchased;
    }

    public void setPurchased(boolean purchased) {
        this.purchased = purchased;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public LocalDateTime getReopenTime() {
        return reopenTime;
    }

    public void setReopenTime(LocalDateTime reopenTime) {
        this.reopenTime = reopenTime;
    }

    public List<PaymentHistory> getPaymentHistoryList() {
        return paymentHistoryList;
    }

    public void setPaymentHistoryList(List<PaymentHistory> paymentHistoryList) {
        this.paymentHistoryList = paymentHistoryList;
    }

    public List<PromotionTicket> getPromotionTicketList() {
        return promotionTicketList;
    }

    public void setPromotionTicketList(List<PromotionTicket> promotionTicketList) {
        this.promotionTicketList = promotionTicketList;
    }
}
