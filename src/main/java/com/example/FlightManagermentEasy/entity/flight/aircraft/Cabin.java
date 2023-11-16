package com.example.FlightManagermentEasy.entity.flight.aircraft;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "cabins")
public class Cabin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "name", nullable = false)
    private String name;
    @ManyToOne
    @JoinColumn(name = "aircraft_id")
    private Aircraft aircraft;
    @OneToMany(mappedBy = "cabin")
    private List<SeatRow> seatRowList;

    public Cabin(String name, Aircraft aircraft) {
        this.name = name;
        this.aircraft = aircraft;
    }

    public Cabin() {
    }

    public Cabin(long id, String name, Aircraft aircraft, List<SeatRow> seatRowList) {
        this.id = id;
        this.name = name;
        this.aircraft = aircraft;
        this.seatRowList = seatRowList;
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

    public Aircraft getAircraft() {
        return aircraft;
    }

    public void setAircraft(Aircraft aircraft) {
        this.aircraft = aircraft;
    }

    public List<SeatRow> getSeatRowList() {
        return seatRowList;
    }

    public void setSeatRowList(List<SeatRow> seatRowList) {
        this.seatRowList = seatRowList;
    }
}
