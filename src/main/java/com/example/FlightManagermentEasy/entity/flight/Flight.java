package com.example.FlightManagermentEasy.entity.flight;

import com.example.FlightManagermentEasy.entity.Ticket;
import com.example.FlightManagermentEasy.entity.flight.aircraft.Aircraft;
import com.example.FlightManagermentEasy.entity.flight.location.Route;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "flights")
public class Flight {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "pnr")
    private String pnr;
    @Column(name = "departure_time")
    private LocalDateTime departureTime;
    @ManyToOne
    @JoinColumn(name = "departure_route_id")
    private Route departureRoute;
    @Column(name = "arrival_time")
    private LocalDateTime arrivalTime;
    @ManyToOne
    @JoinColumn(name = "arrival_route_id")
    private Route arrivalRoute;
    @ManyToOne
    @JoinColumn(name = "aircraft_id")
    private Aircraft aircraft;
    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL)
    private List<Ticket> ticketList;

    public Flight() {
    }

    public Flight(String pnr, LocalDateTime departureTime, Route departureRoute, LocalDateTime arrivalTime, Route arrivalRoute, Aircraft aircraft) {
        this.pnr = pnr;
        this.departureTime = departureTime;
        this.departureRoute = departureRoute;
        this.arrivalTime = arrivalTime;
        this.arrivalRoute = arrivalRoute;
        this.aircraft = aircraft;
    }

    public Flight(long id, String pnr, LocalDateTime departureTime, Route departureRoute, LocalDateTime arrivalTime, Route arrivalRoute, Aircraft aircraft, List<Ticket> ticketList) {
        this.id = id;
        this.pnr = pnr;
        this.departureTime = departureTime;
        this.departureRoute = departureRoute;
        this.arrivalTime = arrivalTime;
        this.arrivalRoute = arrivalRoute;
        this.aircraft = aircraft;
        this.ticketList = ticketList;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPnr() {
        return pnr;
    }

    public void setPnr(String pnr) {
        this.pnr = pnr;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public Route getDepartureRoute() {
        return departureRoute;
    }

    public void setDepartureRoute(Route departureRoute) {
        this.departureRoute = departureRoute;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public Route getArrivalRoute() {
        return arrivalRoute;
    }

    public void setArrivalRoute(Route arrivalRoute) {
        this.arrivalRoute = arrivalRoute;
    }

    public Aircraft getAircraft() {
        return aircraft;
    }

    public void setAircraft(Aircraft aircraft) {
        this.aircraft = aircraft;
    }

    public List<Ticket> getTicketList() {
        return ticketList;
    }

    public void setTicketList(List<Ticket> ticketList) {
        this.ticketList = ticketList;
    }
}
