package com.example.FlightManagermentEasy.entity.user.bank;

import com.example.FlightManagermentEasy.entity.Ticket;
import com.example.FlightManagermentEasy.entity.user.Account;
import com.example.FlightManagermentEasy.entity.user.booking.Booking;
import com.example.FlightManagermentEasy.entity.user.booking.PromotionTicket;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "payment_histories")
public class PaymentHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "time")
    private LocalDateTime time;
    @Column(name = "amount")
    private double amount;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "source_account_id")
    private BankAccount sourceAccount;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "receive_account_id")
    private BankAccount receiveAccount;
    @ManyToOne
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    public PaymentHistory() {
    }

    public PaymentHistory(long id, LocalDateTime time, double amount, BankAccount sourceAccount, BankAccount receiveAccount, Ticket ticket) {
        this.id = id;
        this.time = time;
        this.amount = amount;
        this.sourceAccount = sourceAccount;
        this.receiveAccount = receiveAccount;
        this.ticket = ticket;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public BankAccount getSourceAccount() {
        return sourceAccount;
    }

    public void setSourceAccount(BankAccount sourceAccount) {
        this.sourceAccount = sourceAccount;
    }

    public BankAccount getReceiveAccount() {
        return receiveAccount;
    }

    public void setReceiveAccount(BankAccount receiveAccount) {
        this.receiveAccount = receiveAccount;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }
}
