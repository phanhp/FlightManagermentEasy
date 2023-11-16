package com.example.FlightManagermentEasy.entity.flight.aircraft;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "seat_rows")
public class SeatRow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "name", nullable = false)
    private String name;
    @ManyToOne
    @JoinColumn(name = "cabin_id")
    private Cabin cabin;
    @OneToMany(mappedBy = "seatRow", cascade = CascadeType.ALL)
    private List<Seat> seatList;

    public SeatRow() {
    }

    public SeatRow(String name, Cabin cabin) {
        this.name = name;
        this.cabin = cabin;
    }

    public SeatRow(long id, String name, Cabin cabin, List<Seat> seatList) {
        this.id = id;
        this.name = name;
        this.cabin = cabin;
        this.seatList = seatList;
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

    public Cabin getCabin() {
        return cabin;
    }

    public void setCabin(Cabin cabin) {
        this.cabin = cabin;
    }

    public List<Seat> getSeatList() {
        return seatList;
    }

    public void setSeatList(List<Seat> seatList) {
        this.seatList = seatList;
    }
}
