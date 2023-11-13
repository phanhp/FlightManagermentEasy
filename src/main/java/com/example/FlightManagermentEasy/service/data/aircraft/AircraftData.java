package com.example.FlightManagermentEasy.service.data.aircraft;

import com.example.FlightManagermentEasy.entity.flight.aircraft.*;
import com.example.FlightManagermentEasy.repository.flight.aircraft.*;
import com.example.FlightManagermentEasy.service.MUF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AircraftData {
    @Autowired
    AircraftDataFunction aircraftDataFunction;
    @Autowired
    AircraftRepository aircraftRepository;
    @Autowired
    CabinRepository cabinRepository;
    @Autowired
    AirlineRepository airlineRepository;
    @Autowired
    SeatRowRepository seatRowRepository;
    @Autowired
    SeatRepository seatRepository;

    public void createAirlineData(){
        aircraftDataFunction.createAirline("VietName Airline, VietJet, Bamboo");
    }

    public void createAircraftData(){
        MUF muf = new MUF();
        List<Airline> airlineList = airlineRepository.findAll();

        List<String> randomSeatPerRowString = muf.toArrayString("4,5,6");
        List<Integer> randomSeatPerRow = randomSeatPerRowString.stream().map(Integer::parseInt).collect(Collectors.toList());

        List<String> randomSeatRowPerCabinString = muf.toArrayString("2,3,4");
        List<Integer> randomSeatRowPerCabin = randomSeatRowPerCabinString.stream().map(Integer::parseInt).collect(Collectors.toList());

        List<String> alphabet = muf.toArrayString1("ABCDEFGHIJKLMNOPQRSTRUVWXYZ");

        for (int i = 1; i <= 20; i++) {
            Collections.shuffle(airlineList);
            Airline airline = airlineList.get(0);
            String aircraftName = "Number_" + i + "_" + airline.getName();
            Aircraft aircraft = new Aircraft(aircraftName, airline);
            aircraftRepository.save(aircraft);
            Cabin cabin = new Cabin("Economy", aircraft);
            Cabin cabin1 = new Cabin("Business", aircraft);
            cabinRepository.save(cabin);
            cabinRepository.save(cabin1);
            Collections.shuffle(randomSeatRowPerCabin);
            Collections.shuffle(randomSeatPerRow);
            for (int j = 0; j <= randomSeatRowPerCabin.get(0); j++) {
                String seatRowName = alphabet.get(j);
                SeatRow seatRow = new SeatRow(seatRowName, cabin1);
                seatRowRepository.save(seatRow);
                int seatPerRow = randomSeatPerRow.get(0);
                for (int k = 1; k <= seatPerRow; k++) {
                    String seatName = seatRowName + k;
                    Seat seat = new Seat(seatName, seatRow);
                    seatRepository.save(seat);
                }
            }
            for (int j = 0; j < randomSeatRowPerCabin.get(0)+8; j++) {
                String seatRowName = alphabet.get(j);
                SeatRow seatRow = new SeatRow(seatRowName, cabin);
                seatRowRepository.save(seatRow);
                int seatPerRow = randomSeatPerRow.get(0);
                for (int k = 1; k <= seatPerRow; k++) {
                    String seatName = seatRowName + k;
                    Seat seat = new Seat(seatName, seatRow);
                    seatRepository.save(seat);
                }
            }
        }
    }
}
