package com.example.FlightManagermentEasy.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "mytime")
public class MyTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "time")
    private LocalDateTime time;

    public MyTime(long id, LocalDateTime time) {
        this.id = id;
        this.time = time;
    }

    public MyTime(LocalDateTime time) {
        this.time = time;
    }

    public MyTime() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}
