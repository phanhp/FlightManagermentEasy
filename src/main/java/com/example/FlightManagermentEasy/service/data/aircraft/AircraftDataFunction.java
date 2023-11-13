package com.example.FlightManagermentEasy.service.data.aircraft;

import com.example.FlightManagermentEasy.entity.flight.aircraft.Aircraft;
import com.example.FlightManagermentEasy.entity.flight.aircraft.Airline;
import com.example.FlightManagermentEasy.repository.flight.aircraft.AircraftRepository;
import com.example.FlightManagermentEasy.repository.flight.aircraft.AirlineRepository;
import com.example.FlightManagermentEasy.service.MUF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class AircraftDataFunction {
    @Autowired
    AirlineRepository airlineRepository;
    @Autowired
    AircraftRepository aircraftRepository;
    public void createAirline(String s){
        MUF muf = new MUF();
        ArrayList<String> list = muf.toArrayString(s);
        for (int i=0; i<list.size();i++) {
            Airline airline = new Airline(list.get(i));
            airlineRepository.save(airline);
        }
    }

    public void createRandomAircraft(int numberOfAircraft){
        List<Airline> airlineList = airlineRepository.findAll();
        for (int i=1; i<numberOfAircraft;i++){
            Collections.shuffle(airlineList);
            Airline airline = airlineList.get(0);
            String aircraftName = "Number_"+i+"_"+airline.getName();
            Aircraft aircraft = new Aircraft(aircraftName, airline);
            aircraftRepository.save(aircraft);
        }
    }
}
