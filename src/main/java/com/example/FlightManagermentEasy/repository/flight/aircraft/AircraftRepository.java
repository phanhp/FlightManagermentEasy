package com.example.FlightManagermentEasy.repository.flight.aircraft;


import com.example.FlightManagermentEasy.entity.flight.aircraft.Aircraft;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AircraftRepository extends JpaRepository<Aircraft, Long> {
    @Query(value = "select a.* from aircrafts a\n" +
            "join flights f on a.id = f.aircraft_id\n" +
            "where f.id = ?1", nativeQuery = true)
    Aircraft findAircraftByFlightId(long flightId);

    @Query(value = "select a.* from aircrafts a\n" +
            "left join (select a.id as id from aircrafts a\n" +
            "join flights f on a.id = f.aircraft_id \n" +
            "where f.departure_time >= ?1 and f.arrival_time <= ?2) as working on a.id = working.id\n" +
            "left join (select a.id as id from aircrafts a\n" +
            "left join flights f on a.id=f.aircraft_id\n" +
            "where f.id is null) as lazy on a.id = lazy.id\n" +
            "where working.id is null\n", nativeQuery = true)
    List<Aircraft> findAppropriateAircraftsByTime(LocalDateTime beginTime, LocalDateTime endTime);

    List<Aircraft> findAircraftsByAirlineId(long airlineId);
}
