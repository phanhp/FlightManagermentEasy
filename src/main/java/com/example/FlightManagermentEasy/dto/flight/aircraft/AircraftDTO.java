package com.example.FlightManagermentEasy.dto.flight.aircraft;

public class AircraftDTO {
    private long id;
    private String name;
    private String airlineName;
    private long airlineId;
    private int businessSeatsAmount;
    private int economySeatsAmount;

    public AircraftDTO() {
    }

    public AircraftDTO(long id, String name, String airlineName, long airlineId, int businessSeatsAmount, int economySeatsAmount) {
        this.id = id;
        this.name = name;
        this.airlineName = airlineName;
        this.airlineId = airlineId;
        this.businessSeatsAmount = businessSeatsAmount;
        this.economySeatsAmount = economySeatsAmount;
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

    public String getAirlineName() {
        return airlineName;
    }

    public void setAirlineName(String airlineName) {
        this.airlineName = airlineName;
    }

    public long getAirlineId() {
        return airlineId;
    }

    public void setAirlineId(long airlineId) {
        this.airlineId = airlineId;
    }

    public int getBusinessSeatsAmount() {
        return businessSeatsAmount;
    }

    public void setBusinessSeatsAmount(int businessSeatsAmount) {
        this.businessSeatsAmount = businessSeatsAmount;
    }

    public int getEconomySeatsAmount() {
        return economySeatsAmount;
    }

    public void setEconomySeatsAmount(int economySeatsAmount) {
        this.economySeatsAmount = economySeatsAmount;
    }
}
