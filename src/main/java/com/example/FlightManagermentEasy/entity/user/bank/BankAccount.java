package com.example.FlightManagermentEasy.entity.user.bank;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "bank_accounts")
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    @Column(name = "balance")
    private double balance;
    @OneToMany(mappedBy = "sourceAccount", cascade = CascadeType.ALL)
    private List<PaymentHistory> sourceHistoryList;
    @OneToMany(mappedBy = "receiveAccount", cascade = CascadeType.ALL)
    private List<PaymentHistory> receiveHistoryList;

    public BankAccount() {
    }

    public BankAccount(String name, double balance) {
        this.name = name;
        this.balance = balance;
    }

    public BankAccount(long id, String name, double balance, List<PaymentHistory> sourceHistoryList, List<PaymentHistory> receiveHistoryList) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.sourceHistoryList = sourceHistoryList;
        this.receiveHistoryList = receiveHistoryList;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public List<PaymentHistory> getSourceHistoryList() {
        return sourceHistoryList;
    }

    public void setSourceHistoryList(List<PaymentHistory> sourceHistoryList) {
        this.sourceHistoryList = sourceHistoryList;
    }

    public List<PaymentHistory> getReceiveHistoryList() {
        return receiveHistoryList;
    }

    public void setReceiveHistoryList(List<PaymentHistory> receiveHistoryList) {
        this.receiveHistoryList = receiveHistoryList;
    }
}
