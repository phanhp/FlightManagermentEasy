package com.example.FlightManagermentEasy.entity.flight.aircraft;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "airlines")
public class Airline {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    @OneToMany(mappedBy = "airline", cascade = CascadeType.ALL)
    private List<Aircraft> aircraftList;

    public Airline(long id, String name, List<Aircraft> aircraftList) {
        this.id = id;
        this.name = name;
        this.aircraftList = aircraftList;
    }

    public Airline(String name) {
        this.name = name;
    }

    public Airline() {
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

    public List<Aircraft> getAircraftList() {
        return aircraftList;
    }

    public void setAircraftList(List<Aircraft> aircraftList) {
        this.aircraftList = aircraftList;
    }
}
