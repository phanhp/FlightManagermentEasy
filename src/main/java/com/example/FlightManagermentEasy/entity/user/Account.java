package com.example.FlightManagermentEasy.entity.user;

import jakarta.persistence.*;
import org.springframework.stereotype.Component;

import java.sql.Blob;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "accounts")
@Component
public class Account extends Person {
    @Column(name = "account_name", unique = true)
    private String accountName;
    @Column(name = "password")
    private String password;
    @Column(name = "email", unique = true)
    private String email;
    @Column(name = "phone")
    private String phone;
    @Lob
    @Column(name = "image")
    private Blob image;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "account", fetch = FetchType.EAGER)
    private List<AccountRole> accountRoleList;

    public Account(long id, String name, LocalDate dob, String address, String identity, String gender, String accountName, String password, String email, String phone, Blob image, List<AccountRole> accountRoleList) {
        super(id, name, dob, address, identity, gender);
        this.accountName = accountName;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.image = image;
        this.accountRoleList = accountRoleList;
    }

    public Account(String name, LocalDate dob, String address, String identity, String gender, String accountName, String password, String email, String phone, Blob image) {
        super(name, dob, address, identity, gender);
        this.accountName = accountName;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.image = image;
    }

    public Account(String accountName, String password) {
        this.accountName = accountName;
        this.password = password;
    }

    public Account() {
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Blob getImage() {
        return image;
    }

    public void setImage(Blob image) {
        this.image = image;
    }

    public List<AccountRole> getAccountRoleList() {
        return accountRoleList;
    }

    public void setAccountRoleList(List<AccountRole> accountRoleList) {
        this.accountRoleList = accountRoleList;
    }
}
