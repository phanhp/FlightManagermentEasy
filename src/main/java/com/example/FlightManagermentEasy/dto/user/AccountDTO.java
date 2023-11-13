package com.example.FlightManagermentEasy.dto.user;

public class AccountDTO {
    private long id;
    private String name;
    private String dob;
    private String address;
    private String identity;
    private String gender;
    private String accountName;
    private String password;
    private String email;
    private String phone;
    private String image;

    public AccountDTO() {
    }

    public AccountDTO(String name, String dob, String address, String identity, String gender, String accountName, String password, String email, String phone, String image) {
        this.name = name;
        this.dob = dob;
        this.address = address;
        this.identity = identity;
        this.gender = gender;
        this.accountName = accountName;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.image = image;
    }

    public AccountDTO(long id, String name, String dob, String address, String identity, String gender, String accountName, String password, String email, String phone, String image) {
        this.id = id;
        this.name = name;
        this.dob = dob;
        this.address = address;
        this.identity = identity;
        this.gender = gender;
        this.accountName = accountName;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.image = image;
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

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
