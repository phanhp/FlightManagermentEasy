package com.example.FlightManagermentEasy.repository;

import com.example.FlightManagermentEasy.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findTicketsByFlightId(long flightId);

    @Query(value = "select t.* from tickets t\n" +
            "join bookings b on t.booking_id = b.id\n" +
            "join accounts a on b.account_id = a.id\n" +
            "where t.purchased=false and a.id = ?1", nativeQuery = true)
    List<Ticket> findNonPurchaseTicketsByAccountId(long accountId);

    @Query(value = "select t.* from tickets t\n" +
            "join bookings b on t.booking_id = b.id\n" +
            "join accounts a on b.account_id = a.id\n" +
            "where t.purchased=true and a.id = ?1", nativeQuery = true)
    List<Ticket> findPurchasedTicketsByAccountId(long accountId);

    @Query(value = "select t.* from tickets t\n" +
            "join bookings b on t.booking_id = b.id\n" +
            "join accounts a on b.account_id = a.id\n" +
            "join flights f on t.flight_id = f.id\n" +
            "where a.id = ?1 and f.departure_time < ?2", nativeQuery = true)
    List<Ticket> findFlightedTicketsByAccountId(long accountId, LocalDateTime thisMoment);

    @Query(value = "select t.* from tickets t\n" +
            "join bookings b\n" +
            "on t.booking_id = b.id\n" +
            "where b.id = ?1", nativeQuery = true)
    List<Ticket> findTicketsByBookingId(long bookingId);

    @Query(value = "select t.* from tickets t\n" +
            "join seats s on t.seat_id = s.id\n" +
            "join seat_rows sr on s.seat_row_id = sr.id\n" +
            "join cabins c on sr.cabin_id = c.id\n" +
            "join aircrafts a on c.aircraft_id = a.id\n" +
            "join flights f on f.id = t.flight_id\n" +
            "where f.id = ?1 and c.name like '%economy%'", nativeQuery = true)
    List<Ticket> findEconomyTicketsByFlightId(long flightId);

    @Query(value = "select t.* from tickets t\n" +
            "join seats s on t.seat_id = s.id\n" +
            "join seat_rows sr on s.seat_row_id = sr.id\n" +
            "join cabins c on sr.cabin_id = c.id\n" +
            "join aircrafts a on c.aircraft_id = a.id\n" +
            "join flights f on f.id = t.flight_id\n" +
            "where f.id = ?1 and c.name like '%business%'", nativeQuery = true)
    List<Ticket> findBusinessTicketByFlightId(long flightId);

    @Query(value = "select t.* from tickets t\n" +
            "join seats s on t.seat_id = s.id\n" +
            "join seat_rows sr on s.seat_row_id = sr.id\n" +
            "join cabins c on sr.cabin_id = c.id\n" +
            "join aircrafts a on a.id = c.aircraft_id\n" +
            "join flights f on t.flight_id = f.id\n" +
            "where f.aircraft_id = a.id and sr.id = ?1 and f.id = ?2", nativeQuery = true)
    List<Ticket> findTicketsBySeatRowIdAndFlightId(long seatRowId, long flightId);

    @Query(value = "select t.* from tickets t\n" +
            "where t.id = ?1 and t.purchased = false", nativeQuery = true)
    Ticket findNonPurchasedTicketById(long ticketId);

    @Query(value = "select t.* from tickets t\n" +
            "where t.id = ?1 and t.purchased = true", nativeQuery = true)
    Ticket findPurchasedTicketById(long ticketId);

    @Query(value = "select t.* from tickets t\n" +
            "join seats s on t.seat_id = s.id\n" +
            "join flights f on t.flight_id = f.id\n" +
            "where s.id = ?1 and f.departure_time > ?2 and t.purchased = true", nativeQuery = true)
    List<Ticket> findPurchasedTicketsBySeatIdAfterNow(long seatId, LocalDateTime thisMoment);

    @Query(value = "select t.* from tickets t\n" +
            "join flights f on t.flight_id = f.id\n" +
            "where f.id= ?1 and t.purchased = true", nativeQuery = true)
    List<Ticket> findPurchasedTicketsByFlightId(long flightId);

    List<Ticket> findTicketsBySeatId(long seatId);

    @Query(value = "select t.* from tickets t\n" +
            "join promotion_tickets pr on t.id = pr.ticket_id\n" +
            "where pr.id = ?1", nativeQuery = true)
    Ticket findTicketByPromotionTicketId(long promotionTicketId);

}
