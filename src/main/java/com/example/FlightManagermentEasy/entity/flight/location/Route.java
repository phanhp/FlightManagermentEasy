package com.example.FlightManagermentEasy.entity.flight.location;

import com.example.FlightManagermentEasy.entity.flight.Flight;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table (name = "routes")
public class Route {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "id")
    private long id;
    @Column(name = "name", nullable = false)
    private String name;
    @ManyToOne
    @JoinColumn(name = "airport_id")
    private Airport airport;
    @OneToMany(mappedBy = "departureRoute", cascade = CascadeType.ALL)
    private List<Flight> departureRoute;
    @OneToMany(mappedBy = "arrivalRoute", cascade = CascadeType.ALL)
    private List<Flight> arrivalRoute;

    public Route() {
    }

    public Route(String name, Airport airport) {
        this.name = name;
        this.airport = airport;
    }

    public Route(long id, String name, Airport airport, List<Flight> departureRoute, List<Flight> arrivalRoute) {
        this.id = id;
        this.name = name;
        this.airport = airport;
        this.departureRoute = departureRoute;
        this.arrivalRoute = arrivalRoute;
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

    public Airport getAirport() {
        return airport;
    }

    public void setAirport(Airport airport) {
        this.airport = airport;
    }

    public List<Flight> getDepartureRoute() {
        return departureRoute;
    }

    public void setDepartureRoute(List<Flight> departureRoute) {
        this.departureRoute = departureRoute;
    }

    public List<Flight> getArrivalRoute() {
        return arrivalRoute;
    }

    public void setArrivalRoute(List<Flight> arrivalRoute) {
        this.arrivalRoute = arrivalRoute;
    }
}
