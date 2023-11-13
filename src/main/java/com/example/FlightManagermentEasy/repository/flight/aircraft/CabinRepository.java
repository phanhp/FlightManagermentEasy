package com.example.FlightManagermentEasy.repository.flight.aircraft;

import com.example.FlightManagermentEasy.entity.flight.aircraft.Cabin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CabinRepository extends JpaRepository<Cabin, Long> {
    @Query(value = "select c.* from cabins c\n" +
            "join aircrafts a on c.aircraft_id = a.id\n" +
            "where a.id = ?1 and c.name like '%Economy%'", nativeQuery = true)
    List<Cabin> findEconomyCabinByAircraftId(long aircraftId);

    @Query(value = "select c.* from cabins c\n" +
            "join aircrafts a on c.aircraft_id = a.id\n" +
            "where a.id = ?1 and c.name like '%Business%'", nativeQuery = true)
    List<Cabin> findBusinessCabinByAircraftId(long aircraftId);

    @Query(value = "select c.* from cabins c\n" +
            "join aircrafts a on c.aircraft_id = a.id\n" +
            "join flights f on a.id = f.aircraft_id\n" +
            "where f.id = ?1", nativeQuery = true)
    List<Cabin> findCabinsByFlightId(long flightId);

    List<Cabin> findCabinsByAircraftId(long aircraftId);

    @Query(value = "select c.* from cabins c\n" +
            "join seat_rows sr on c.id = sr.cabin_id\n" +
            "join seats s on s.seat_row_id = sr.id\n" +
            "where s.id = ?1", nativeQuery = true)
    Cabin findCabinBySeatId(long seatId);

    @Query(value = "select c.* from cabins c\n" +
            "join seat_rows sr on c.id = sr.cabin_id\n" +
            "join seats s on s.seat_row_id = sr.id\n" +
            "join tickets t on t.seat_id = s.id\n" +
            "where t.id = ?1", nativeQuery = true)
    Cabin findCabinByTicketId (long ticketId);
}
