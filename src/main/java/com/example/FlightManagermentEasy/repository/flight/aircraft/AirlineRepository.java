package com.example.FlightManagermentEasy.repository.flight.aircraft;


import com.example.FlightManagermentEasy.entity.flight.aircraft.Airline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AirlineRepository extends JpaRepository<Airline, Long> {
    @Query(value = "select al.* from airlines al\n" +
            "join aircrafts a on al.id = a.airline_id\n" +
            "where a.id = ?1", nativeQuery = true)
    Airline findAirlineByAircraftId(long aircraftId);
}
