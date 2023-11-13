package com.example.FlightManagermentEasy.entity.flight.location;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "countries")
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    @OneToMany (mappedBy = "country", cascade = CascadeType.ALL)
    private List<City> cityList;

    public Country(long id, String name, List<City> cityList) {
        this.id = id;
        this.name = name;
        this.cityList = cityList;
    }

    public Country(String name) {
        this.name = name;
    }

    public Country() {
    }

    public Country(String name, List<City> cityList) {
        this.name = name;
        this.cityList = cityList;
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

    public List<City> getCityList() {
        return cityList;
    }

    public void setCityList(List<City> cityList) {
        this.cityList = cityList;
    }
}
