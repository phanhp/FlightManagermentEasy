package com.example.FlightManagermentEasy.entity.user;

import com.example.FlightManagermentEasy.entity.Ticket;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "passengers")
public class Passenger extends Person {
    @Column(name = "available")
    private boolean available = true;
    @OneToMany(mappedBy = "passenger", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Ticket> ticketList;

    public Passenger(boolean available) {
        this.available = available;
    }

    public Passenger() {
    }
}
