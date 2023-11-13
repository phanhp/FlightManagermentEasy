package com.example.FlightManagermentEasy.service.service.flight.location;

import com.example.FlightManagermentEasy.repository.flight.location.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CityService {
    @Autowired
    CityRepository cityRepository;
}
