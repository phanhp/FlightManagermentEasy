package com.example.FlightManagermentEasy.dto.flight.location;

public class AirportDTO {
    private long id;
    private String name;
    private long cityId;

    public AirportDTO(long id, String name, long cityId) {
        this.id = id;
        this.name = name;
        this.cityId = cityId;
    }

    public AirportDTO() {
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

    public long getCityId() {
        return cityId;
    }

    public void setCityId(long cityId) {
        this.cityId = cityId;
    }
}
