package com.example.FlightManagermentEasy.repository.flight.location;

import com.example.FlightManagermentEasy.entity.flight.location.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    @Query(value = "select r.* from routes r\n" +
            "join flights f on r.id = f.departure_route_id\n" +
            "where f.id = ?1", nativeQuery = true)
    Route findDepartureRouteByFlightId(long flightId);

    @Query(value = "select r.* from routes r\n" +
            "join flights f on r.id = f.arrival_route_id\n" +
            "where f.id = ?1", nativeQuery = true)
    Route findArrivalRouteByFlightId(long flightId);

    List<Route> findRoutesByAirport_CityName(String cityName);

    List<Route> findRoutesByAirportCityCountryId(long countryId);

    List<Route> findRoutesByAirportCityId(long cityId);

    List<Route> findRoutesByAirportId(long airportId);

    //Appropriate Route For Flight
    //By countryId
    @Query(value = "select r.* from (select r.* from routes r\n" +
            "join airports a on r.airport_id = a.id\n" +
            "join cities c on a.city_id = c.id\n" +
            "join countries co on c.country_id = co.id\n" +
            "where co.id = ?1) as r\n" +
            "left join (select r.id as drID from routes r\n" +
            "join flights f on r.id = f.departure_route_id\n" +
            "join airports a on r.airport_id = a.id\n" +
            "join cities c on a.city_id = c.id\n" +
            "join countries co on c.country_id = co.id\n" +
            "where co.id= ?1 and f.departure_time between ?2 and ?3 and f.departure_time > ?4)\n" +
            "as drl on r.id = drID\n" +
            "left join (select r.id as arID from routes r\n" +
            "join flights f on r.id = f.arrival_route_id\n" +
            "join airports a on r.airport_id = a.id\n" +
            "join cities c on a.city_id = c.id\n" +
            "join countries co on c.country_id = co.id\n" +
            "where co.id= ?1 and f.arrival_time between ?2 and ?3 and f.arrival_time > ?4)\n" +
            "as arl on r.id = arID\n" +
            "where drID is null and arID is null", nativeQuery = true)
    List<Route> findAppropriateRoutesByCountryId(long countryId,
                                                 LocalDateTime begin, LocalDateTime end,
                                                 LocalDateTime thisMoment);

    //By cityId
    @Query(value = "select r.* from (select r.* from routes r\n" +
            "join airports a on r.airport_id = a.id\n" +
            "join cities c on a.city_id = c.id\n" +
            "where c.id = ?1) as r\n" +
            "left join (select r.id as drID from routes r\n" +
            "join flights f on r.id = f.departure_route_id\n" +
            "join airports a on r.airport_id = a.id\n" +
            "join cities c on a.city_id = c.id\n" +
            "where c.id= ?1 and f.departure_time between ?2 and ?3 and f.departure_time > ?4)\n" +
            "as drl on r.id = drID\n" +
            "left join (select r.id as arID from routes r\n" +
            "join flights f on r.id = f.arrival_route_id\n" +
            "join airports a on r.airport_id = a.id\n" +
            "join cities c on a.city_id = c.id\n" +
            "where c.id= ?1 and f.arrival_time between ?2 and ?3 and f.arrival_time > ?4)\n" +
            "as arl on r.id = arID\n" +
            "where drID is null and arID is null", nativeQuery = true)
    List<Route> findAppropriateRouteByCityId(long cityId,
                                             LocalDateTime begin, LocalDateTime end,
                                             LocalDateTime thisMoment);

    //By airportId
    @Query(value = "select r.* from (select r.* from routes r\n" +
            "join airports a on r.airport_id = a.id\n" +
            "where a.id = ?1) as r\n" +
            "left join (select r.id as drID from routes r\n" +
            "join flights f on r.id = f.departure_route_id\n" +
            "join airports a on r.airport_id = a.id\n" +
            "where a.id= ?1 and f.departure_time between ?2 and ?3 and f.departure_time > ?4)\n" +
            "as drl on r.id = drID\n" +
            "left join (select r.id as arID from routes r\n" +
            "join flights f on r.id = f.arrival_route_id\n" +
            "join airports a on r.airport_id = a.id\n" +
            "where a.id= ?1 and f.arrival_time between ?2 and ?3 and f.arrival_time > ?4)\n" +
            "as arl on r.id = arID\n" +
            "where drID is null and arID is null", nativeQuery = true)
    List<Route> findAppropriateRoutesByAirportId(long airportId,
                                                 LocalDateTime begin, LocalDateTime end,
                                                 LocalDateTime thisMoment);

    //all appropriateRoute
    @Query(value = "select r.* from routes r\n" +
            "left join (select r.id as drID from routes r\n" +
            "join flights f on r.id = f.departure_route_id\n" +
            "where f.departure_time between ?1 and ?2 and f.departure_time > ?3)\n" +
            "as drl on r.id = drID\n" +
            "left join (select r.id as arID from routes r\n" +
            "join flights f on r.id = f.arrival_route_id\n" +
            "where f.arrival_time between ?1 and ?2 and f.arrival_time > ?3)\n" +
            "as arl on r.id = arID\n" +
            "where drID is null and arID is null", nativeQuery = true)
    List<Route> findAllAppropriateRoutes(LocalDateTime begin, LocalDateTime end, LocalDateTime thisMoment);
}
