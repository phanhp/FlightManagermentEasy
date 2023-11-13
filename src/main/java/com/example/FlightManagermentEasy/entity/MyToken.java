package com.example.FlightManagermentEasy.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tokens")
public class MyToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "encode_key", unique = true)
    private String encodeKey;
    @Column(name = "encode_string", columnDefinition = "TEXT")
    private String encodeString;

    public MyToken() {
    }

    public MyToken(String encodeKey, String encodeString) {
        this.encodeKey = encodeKey;
        this.encodeString = encodeString;
    }

    public MyToken(long id, String encodeKey, String encodeString) {
        this.id = id;
        this.encodeKey = encodeKey;
        this.encodeString = encodeString;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEncodeKey() {
        return encodeKey;
    }

    public void setEncodeKey(String encodeKey) {
        this.encodeKey = encodeKey;
    }

    public String getEncodeString() {
        return encodeString;
    }

    public void setEncodeString(String encodeString) {
        this.encodeString = encodeString;
    }
}
