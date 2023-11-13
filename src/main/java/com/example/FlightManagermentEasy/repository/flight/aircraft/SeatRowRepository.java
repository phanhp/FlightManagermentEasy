package com.example.FlightManagermentEasy.repository.flight.aircraft;


import com.example.FlightManagermentEasy.entity.flight.aircraft.SeatRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRowRepository extends JpaRepository<SeatRow, Long> {
    @Query(value = "select sr.* from seat_rows sr\n" +
            "join cabins c on sr.cabin_id = c.id\n" +
            "join aircrafts a on c.aircraft_id = a.id\n" +
            "where a.id = ?1", nativeQuery = true)
    List<SeatRow> findSeatRowsByAircraftId(long aircraftId);

    @Query(value = "select sr.* from seat_rows sr\n" +
            "join cabins c on c.id = sr.cabin_id\n" +
            "join aircrafts a on c.aircraft_id = a.id\n" +
            "join flights f on a.id = f.aircraft_id\n" +
            "where f.id = ?1", nativeQuery = true)
    List<SeatRow> findSeatRowsByFlightId(long flightId);

    @Query(value = "select sr.* from seat_rows sr\n" +
            "join cabins c on sr.cabin_id = c.id\n" +
            "join aircrafts a on c.aircraft_id = a.id\n" +
            "where a.id= ?1 and c.name like'%economy%'", nativeQuery = true)
    List<SeatRow> findEconomySeatRowsByAircraftId(long aircraftId);

    @Query(value = "select sr.* from seat_rows sr\n" +
            "join cabins c on sr.cabin_id = c.id\n" +
            "join aircrafts a on c.aircraft_id = a.id\n" +
            "where a.id=?1 and c.name like'%business%'", nativeQuery = true)
    List<SeatRow> findBusinessSeatRowsByAircraftId(long aircraftId);

    List<SeatRow> findSeatRowsByCabinId(long cabinId);
}
