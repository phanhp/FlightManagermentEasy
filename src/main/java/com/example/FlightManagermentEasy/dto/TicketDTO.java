package com.example.FlightManagermentEasy.dto;

public class TicketDTO {
    private long id;
    private long passengerId;
    private long seatId;
    private long flightId;
    private long bookingId;

    public TicketDTO() {
    }

    public TicketDTO(long id, long passengerId, long seatId, long flightId, long bookingId) {
        this.id = id;
        this.passengerId = passengerId;
        this.seatId = seatId;
        this.flightId = flightId;
        this.bookingId = bookingId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(long passengerId) {
        this.passengerId = passengerId;
    }

    public long getSeatId() {
        return seatId;
    }

    public void setSeatId(long seatId) {
        this.seatId = seatId;
    }

    public long getFlightId() {
        return flightId;
    }

    public void setFlightId(long flightId) {
        this.flightId = flightId;
    }

    public long getBookingId() {
        return bookingId;
    }

    public void setBookingId(long bookingId) {
        this.bookingId = bookingId;
    }
}
