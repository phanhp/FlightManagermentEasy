package com.example.FlightManagermentEasy.dto.flight.location;

public class RouteDTO {
    private long id;
    private String name;
    private long airportId;

    public RouteDTO(long id, String name, long airportId) {
        this.id = id;
        this.name = name;
        this.airportId = airportId;
    }

    public RouteDTO() {
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

    public long getAirportId() {
        return airportId;
    }

    public void setAirportId(long airportId) {
        this.airportId = airportId;
    }
}
