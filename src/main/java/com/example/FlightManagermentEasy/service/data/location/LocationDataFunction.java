package com.example.FlightManagermentEasy.service.data.location;

import com.example.FlightManagermentEasy.entity.flight.location.Airport;
import com.example.FlightManagermentEasy.entity.flight.location.City;
import com.example.FlightManagermentEasy.entity.flight.location.Country;
import com.example.FlightManagermentEasy.repository.flight.location.AirportRepository;
import com.example.FlightManagermentEasy.repository.flight.location.CityRepository;
import com.example.FlightManagermentEasy.repository.flight.location.CountryRepository;
import com.example.FlightManagermentEasy.service.MUF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LocationDataFunction {
    @Autowired
    CountryRepository countryRepository;
    @Autowired
    CityRepository cityRepository;
    @Autowired
    AirportRepository airportRepository;

    public void createCountry(String countries){
        MUF muf = new MUF();
        ArrayList<String> list = muf.toArrayString(countries);
        for (int i=0; i<list.size();i++){
            Country country = new Country(list.get(i));
            countryRepository.save(country);
        }
    }

    public void createCity(String cities, String countryName){
        MUF muf = new MUF();
        Country country = countryRepository.findCountryByNameIgnoreCase(countryName);
        List<String> list = muf.toArrayString(cities);
        for (int i=0; i<list.size();i++){
            City city = new City(list.get(i), country);
            cityRepository.save(city);
        }
    }

    public void createAirport(String airportName, City city) {
        Airport airport = new Airport(airportName, city);
        airportRepository.save(airport);
    }
}
