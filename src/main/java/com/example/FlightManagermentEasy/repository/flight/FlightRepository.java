package com.example.FlightManagermentEasy.repository.flight;

import com.example.FlightManagermentEasy.entity.flight.Flight;
import com.example.FlightManagermentEasy.entity.flight.aircraft.Aircraft;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {
    @Query(value = "select f.* from flights f\n" +
            "join tickets t on f.id = t.flight_id\n" +
            "where t.id = ?1", nativeQuery = true)
    Flight findFlightsByTicketId(long ticketId);

    @Query(value = "select f.* from flights f\n" +
            "join aircrafts a on f.aircraft_id = a.id\n" +
            "where a.id = ?1 and f.departure_time > ?2", nativeQuery = true)
    List<Flight> findFlightsByAircraftIdAfterNow(long aircraftId, LocalDateTime thisMoment);

    @Query(value = "select f.* from flights f\n" +
            "where f.departure_time> ?1", nativeQuery = true)
    List<Flight> findAllFlightsAfterMoment(LocalDateTime thisMoment);

    @Query(value = "select f.* from flights f\n" +
            "join routes dr on f.departure_route_id = dr.id\n" +
            "join airports da on dr.airport_id = da.id\n" +
            "join cities dc on da.city_id = dc.id\n" +
            "join routes ar on f.arrival_route_id = ar.id\n" +
            "join airports aa on ar.airport_id = aa.id\n" +
            "join cities ac on aa.city_id = ac.id\n" +
            "where dc.id = ?1 and ac.id =  ?2 and f.departure_time between ?3 and ?4 and f.departure_time > ?5", nativeQuery = true)
    List<Flight> findFlightsByDCandACandDT(long departureCityId,
                                           long arrivalCityId,
                                           LocalDateTime departureTime,
                                           LocalDateTime nextDay,
                                           LocalDateTime thisMoment);

    @Query(value = "select f.* from flights f\n" +
            "join routes dr on f.departure_route_id = dr.id\n" +
            "join airports da on dr.airport_id = da.id\n" +
            "join cities dc on da.city_id = dc.id\n" +
            "join routes ar on f.arrival_route_id = ar.id\n" +
            "join airports aa on ar.airport_id = aa.id\n" +
            "join cities ac on aa.city_id = ac.id\n" +
            "where dc.id = ?1 and ac.id =  ?2 and f.departure_time > ?3", nativeQuery = true)
    List<Flight> findFlightsByDCandAc(long departureCityId, long arrivalCityId, LocalDateTime thisMoment);

    @Query(value = "select f.* from flights f\n" +
            "join routes dr on f.departure_route_id = dr.id\n" +
            "join airports da on dr.airport_id = da.id\n" +
            "join cities dc on da.city_id = dc.id\n" +
            "where dc.id = ?1 and f.departure_time between ?2 and ?3 and f.departure_time > ?4", nativeQuery = true)
    List<Flight> findFlightsByDCandDT(long departureCityId, LocalDateTime departureTime, LocalDateTime nextDay, LocalDateTime thisMoment);

    @Query(value = "select f.* from flights f\n" +
            "join routes dr on f.departure_route_id = dr.id\n" +
            "join airports da on dr.airport_id = da.id\n" +
            "join cities dc on da.city_id = dc.id\n" +
            "where dc.id = ?1 and f.departure_time > ?2", nativeQuery = true)
    List<Flight> findFlightsByDC(long departureCityId, LocalDateTime thisMoment);

    @Query(value = "select f.* from flights f\n" +
            "join routes ar on f.arrival_route_id = ar.id\n" +
            "join airports aa on ar.airport_id = aa.id\n" +
            "join cities ac on aa.city_id = ac.id\n" +
            "where ac.id =  ?1 and f.departure_time between ?2 and ?3 and f.departure_time > ?4", nativeQuery = true)
    List<Flight> findFlightsByACandDT(long arrivalCityId, LocalDateTime departureTime, LocalDateTime nextDay, LocalDateTime thisMoment);

    @Query(value = "select f.* from flights f\n" +
            "join routes ar on f.arrival_route_id = ar.id\n" +
            "join airports aa on ar.airport_id = aa.id\n" +
            "join cities ac on aa.city_id = ac.id\n" +
            "where ac.id =  ?1 and f.departure_time > ?2", nativeQuery = true)
    List<Flight> findFlightsByAC(long arrivalCityId, LocalDateTime thisMoment);

    @Query(value = "select f.* from flights f\n" +
            "where f.departure_time between ?1 and ?2 and f.departure_time > ?3", nativeQuery = true)
    List<Flight> findFlightsByDT(LocalDateTime departureTime, LocalDateTime nextDay, LocalDateTime thisMoment);

    @Query(value = "select f.* from flights f\n" +
            "join aircrafts a on f.aircraft_id = a.id\n" +
            "where a.id = ?1", nativeQuery = true)
    List<Flight> findFlightsByAircraftId(long aircraftId);
}
