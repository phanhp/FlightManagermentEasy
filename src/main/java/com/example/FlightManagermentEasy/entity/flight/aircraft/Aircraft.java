package com.example.FlightManagermentEasy.entity.flight.aircraft;


import com.example.FlightManagermentEasy.entity.flight.Flight;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "aircrafts")
public class Aircraft {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    @ManyToOne
    @JoinColumn(name = "airline_id")
    private Airline airline;
    @OneToMany(mappedBy = "aircraft", cascade = CascadeType.ALL)
    private List<Cabin> cabinList;
    @OneToMany(mappedBy = "aircraft", cascade = CascadeType.ALL)
    private List<Flight> flightList;

    public Aircraft(long id, String name, Airline airline, List<Cabin> cabinList, List<Flight> flightList) {
        this.id = id;
        this.name = name;
        this.airline = airline;
        this.cabinList = cabinList;
        this.flightList = flightList;
    }

    public Aircraft(String name, Airline airline) {
        this.name = name;
        this.airline = airline;
    }

    public Aircraft() {
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

    public Airline getAirline() {
        return airline;
    }

    public void setAirline(Airline airline) {
        this.airline = airline;
    }

    public List<Cabin> getCabinList() {
        return cabinList;
    }

    public void setCabinList(List<Cabin> cabinList) {
        this.cabinList = cabinList;
    }

    public List<Flight> getFlightList() {
        return flightList;
    }

    public void setFlightList(List<Flight> flightList) {
        this.flightList = flightList;
    }
}
