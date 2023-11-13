package com.example.FlightManagermentEasy.entity.user;

import com.example.FlightManagermentEasy.service.MUF;
import jakarta.persistence.*;

import java.time.LocalDate;

@MappedSuperclass
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "name")
    private String name;
    @Column(name = "dob")
    private LocalDate dob;
    @Column(name = "address")
    private String address;
    @Column(name = "identity", unique = true)
    private String identity;
    @Column(name = "gender")
    private String gender;

    public Person() {
    }

    public Person(String name, LocalDate dob, String address, String identity, String gender) {
        this.name = name;
        this.dob = dob;
        this.address = address;
        this.identity = identity;
        this.gender = gender;
    }

    public Person(long id, String name, LocalDate dob, String address, String identity, String gender) {
        this.id = id;
        this.name = name;
        this.dob = dob;
        this.address = address;
        this.identity = identity;
        this.gender = gender;
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

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
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

    public String getDobString(){
        MUF muf = new MUF();
        String dobString = muf.localDateToString(this.dob);
        return dobString;
    }

    public void setDobByString(String dobString){
        MUF muf = new MUF();
        LocalDate dobFromString = muf.stringToLocalDate(dobString);
        this.dob = dobFromString;
    }
}
