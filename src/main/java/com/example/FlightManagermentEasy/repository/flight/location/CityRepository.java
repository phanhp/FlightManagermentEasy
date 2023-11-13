package com.example.FlightManagermentEasy.repository.flight.location;

import com.example.FlightManagermentEasy.entity.flight.location.City;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    @Query(value = "select c.* from cities c\n" +
            "join airports a on c.id = a.city_id\n" +
            "join routes r on a.id = r.airport_id\n" +
            "join flights f on f.departure_route_id = r.id\n" +
            "where f.id = ?1", nativeQuery = true)
    City findDepartureCityByFlightId(long flightId);

    @Query(value = "select c.* from cities c\n" +
            "join airports a on c.id = a.city_id\n" +
            "join routes r on a.id = r.airport_id\n" +
            "join flights f on f.arrival_route_id = r.id\n" +
            "where f.id = ?1", nativeQuery = true)
    City findArrivalCityByFlightId(long flightId);

    @Query(value = "select c.* from cities c\n" +
            "join airports a on c.id = a.city_id\n" +
            "where a.id = ?1", nativeQuery = true)
    City findCityByAirportId(long airportId);

    @Query(value = "select c.* from cities c\n" +
            "join airports a on c.id= a.city_id\n" +
            "join routes r on r.airport_id = a.id\n" +
            "where r.id = ?1", nativeQuery = true)
    City findCityByRouteId(long routeId);

    @Query(value = "select c.* from cities c\n" +
            "where c.id <> ?1", nativeQuery = true)
    List<City> findCitiesNotHaveId(long cityId);

    List<City> findCitiesByCountryId(long countryId);
}
