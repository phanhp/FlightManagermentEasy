package com.example.FlightManagermentEasy.entity.user.booking;

import com.example.FlightManagermentEasy.entity.Ticket;
import com.example.FlightManagermentEasy.entity.user.Account;
import com.example.FlightManagermentEasy.entity.user.bank.PaymentHistory;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "book_time")
    private LocalDateTime bookTime;
    @Column(name = "purchased")
    private boolean purchased;
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Ticket> ticketList;

    public Booking() {
    }

    public Booking(LocalDateTime bookTime, boolean purchased, Account account) {
        this.bookTime = bookTime;
        this.purchased = purchased;
        this.account = account;
    }

    public Booking(long id, LocalDateTime bookTime, boolean purchased, Account account, List<Ticket> ticketList) {
        this.id = id;
        this.bookTime = bookTime;
        this.purchased = purchased;
        this.account = account;
        this.ticketList = ticketList;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getBookTime() {
        return bookTime;
    }

    public void setBookTime(LocalDateTime bookTime) {
        this.bookTime = bookTime;
    }

    public boolean isPurchased() {
        return purchased;
    }

    public void setPurchased(boolean purchased) {
        this.purchased = purchased;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public List<Ticket> getTicketList() {
        return ticketList;
    }

    public void setTicketList(List<Ticket> ticketList) {
        this.ticketList = ticketList;
    }
}
