package com.example.FlightManagermentEasy.entity.flight.aircraft;


import com.example.FlightManagermentEasy.entity.Ticket;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "seats")
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "name", nullable = false)
    private String name;
    @ManyToOne
    @JoinColumn(name = "seat_row_id", nullable = false)
    private SeatRow seatRow;
    @OneToMany(mappedBy = "seat", cascade = CascadeType.ALL)
    private List<Ticket> ticketList;

    public Seat(long id, String name, SeatRow seatRow, List<Ticket> ticketList) {
        this.id = id;
        this.name = name;
        this.seatRow = seatRow;
        this.ticketList = ticketList;
    }

    public Seat(String name, SeatRow seatRow) {
        this.name = name;
        this.seatRow = seatRow;
    }

    public Seat() {
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

    public SeatRow getSeatRow() {
        return seatRow;
    }

    public void setSeatRow(SeatRow seatRow) {
        this.seatRow = seatRow;
    }

    public List<Ticket> getTicketList() {
        return ticketList;
    }

    public void setTicketList(List<Ticket> ticketList) {
        this.ticketList = ticketList;
    }
}
