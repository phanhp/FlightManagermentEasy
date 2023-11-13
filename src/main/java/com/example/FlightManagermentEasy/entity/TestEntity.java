package com.example.FlightManagermentEasy.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "test_entity")
public class TestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "data")
    private String data;

    public TestEntity(long id, String data) {
        this.id = id;
        this.data = data;
    }

    public TestEntity(String data) {
        this.data = data;
    }

    public TestEntity() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
