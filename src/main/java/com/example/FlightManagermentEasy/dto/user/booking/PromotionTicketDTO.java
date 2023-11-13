package com.example.FlightManagermentEasy.dto.user.booking;

public class PromotionTicketDTO {
    private long id;
    private String code;
    private double value;
    private String available;

    public PromotionTicketDTO(long id, String code, double value, String available) {
        this.id = id;
        this.code = code;
        this.value = value;
        this.available = available;
    }

    public PromotionTicketDTO() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }
}
