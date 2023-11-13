package com.example.FlightManagermentEasy.dto.flight.location;

public class CountryDTO {
    private long id;
    private String name;

    public CountryDTO(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public CountryDTO() {
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
}
