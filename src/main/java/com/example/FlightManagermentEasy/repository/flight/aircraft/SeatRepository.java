package com.example.FlightManagermentEasy.repository.flight.aircraft;


import com.example.FlightManagermentEasy.entity.flight.aircraft.Seat;
import com.example.FlightManagermentEasy.entity.flight.location.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    @Query(value = "select s.* from seats s\n" +
            "join seat_rows sr on s.seat_row_id = sr.id\n" +
            "join cabins c on sr.cabin_id = c.id\n" +
            "where c.id = ?1", nativeQuery = true)
    List<Seat> findSeatsByCabinId(long cabinId);

    @Query(value = "select s.* from seats s\n" +
            "join seat_rows sr on s.seat_row_id = sr.id\n" +
            "join cabins c on sr.cabin_id = c.id\n" +
            "join aircrafts a on a.id = c.aircraft_id\n" +
            "where a.id = ?1", nativeQuery = true)
    List<Seat> findSeatsByAircraftId(long aircarftId);

    @Query(value = "select s.* from seats s\n" +
            "join seat_rows sr on s.seat_row_id = sr.id\n" +
            "join cabins c on sr.cabin_id = c.id\n" +
            "join aircrafts a on a.id = c.aircraft_id\n" +
            "where a.id = ?1 and c.name like '%business%'", nativeQuery = true)
    List<Seat> findBusinessSeatsByAircraftId(long aircraftId);

    @Query(value = "select s.* from seats s\n" +
            "join seat_rows sr on s.seat_row_id = sr.id\n" +
            "join cabins c on sr.cabin_id = c.id\n" +
            "join aircrafts a on a.id = c.aircraft_id\n" +
            "where a.id = ?1 and c.name like '%economy%'", nativeQuery = true)
    List<Seat> findEconomySeatsByAircraftId(long aircraftId);

    @Query(value = "select s.* from seats s\n" +
            "join tickets t on s.id = t.seat_id\n" +
            "join flights f on t.flight_id = f.id\n" +
            "where f.id= ?1", nativeQuery = true)
    List<Seat> findSeatsByFlightId(long flightId);

    @Query(value = "select s.* from seats s\n" +
            "left join tickets t on s.id = t.id\n" +
            "join flights f on t.flight_id = f.id\n" +
            "where f.id = ?1 and s.id is not null and t.id is null", nativeQuery = true)
    List<Seat> findSeatsByFlightIdWasNotTicketed(long flightId);

    List<Seat> findSeatsBySeatRowId(long seatRowId);

    @Query(value = "select s.* from seats s\n" +
            "join tickets t on t.seat_id = s.id\n" +
            "where t.id = ?1",nativeQuery = true)
    Seat findSeatsByTicketId(long ticketId);
}
