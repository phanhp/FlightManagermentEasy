package com.example.FlightManagermentEasy.dto.flight;

public class FlightDTO {
    private long id;
    private String pnr;
    private double ecnonomyPrice;
    private double businessPrice;
    private String departureTime;
    private long departureRouteId;
    private String arrivalTime;
    private long arrivalRouteId;
    private long aircraftId;
    private String departureCity;
    private String arrivalCity;

    public FlightDTO() {
    }

    public FlightDTO(long id, String pnr, double ecnonomyPrice, double businessPrice, String departureTime, long departureRouteId, String arrivalTime, long arrivalRouteId, long aircraftId, String departureCity, String arrivalCity) {
        this.id = id;
        this.pnr = pnr;
        this.ecnonomyPrice = ecnonomyPrice;
        this.businessPrice = businessPrice;
        this.departureTime = departureTime;
        this.departureRouteId = departureRouteId;
        this.arrivalTime = arrivalTime;
        this.arrivalRouteId = arrivalRouteId;
        this.aircraftId = aircraftId;
        this.departureCity = departureCity;
        this.arrivalCity = arrivalCity;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPnr() {
        return pnr;
    }

    public void setPnr(String pnr) {
        this.pnr = pnr;
    }

    public double getEcnonomyPrice() {
        return ecnonomyPrice;
    }

    public void setEcnonomyPrice(double ecnonomyPrice) {
        this.ecnonomyPrice = ecnonomyPrice;
    }

    public double getBusinessPrice() {
        return businessPrice;
    }

    public void setBusinessPrice(double businessPrice) {
        this.businessPrice = businessPrice;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public long getDepartureRouteId() {
        return departureRouteId;
    }

    public void setDepartureRouteId(long departureRouteId) {
        this.departureRouteId = departureRouteId;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public long getArrivalRouteId() {
        return arrivalRouteId;
    }

    public void setArrivalRouteId(long arrivalRouteId) {
        this.arrivalRouteId = arrivalRouteId;
    }

    public long getAircraftId() {
        return aircraftId;
    }

    public void setAircraftId(long aircraftId) {
        this.aircraftId = aircraftId;
    }

    public String getDepartureCity() {
        return departureCity;
    }

    public void setDepartureCity(String departureCity) {
        this.departureCity = departureCity;
    }

    public String getArrivalCity() {
        return arrivalCity;
    }

    public void setArrivalCity(String arrivalCity) {
        this.arrivalCity = arrivalCity;
    }
}
