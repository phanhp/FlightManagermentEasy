package com.example.FlightManagermentEasy.entity.user.booking;

import com.example.FlightManagermentEasy.entity.Ticket;
import com.example.FlightManagermentEasy.entity.user.bank.PaymentHistory;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;

import java.time.LocalDateTime;

@Entity
@Table(name = "promotion_tickets")
public class PromotionTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "code", unique = true, nullable = false)
    private String code;
    @Column(name = "value", nullable = false)
    @DecimalMax("1.0")
    private double value;
    @Column(name = "expired_date", nullable = false)
    private LocalDateTime expiredDate;
    @Column(name = "available", nullable = false)
    private boolean available;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    public PromotionTicket() {
    }

    public PromotionTicket(String code, double value, LocalDateTime expiredDate, boolean available) {
        this.code = code;
        this.value = value;
        this.expiredDate = expiredDate;
        this.available = available;
    }

    public PromotionTicket(long id, String code, double value, LocalDateTime expiredDate, boolean available, Ticket ticket) {
        this.id = id;
        this.code = code;
        this.value = value;
        this.expiredDate = expiredDate;
        this.available = available;
        this.ticket = ticket;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public LocalDateTime getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(LocalDateTime expiredDate) {
        this.expiredDate = expiredDate;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }
}
