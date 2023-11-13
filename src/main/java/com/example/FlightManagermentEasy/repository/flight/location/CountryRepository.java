package com.example.FlightManagermentEasy.repository.flight.location;

import com.example.FlightManagermentEasy.entity.flight.location.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
    Country findCountryByNameIgnoreCase(String countryName);

    @Query(value = "select c.* from countries c\n" +
            "join cities ct on c.id = ct.country_id\n" +
            "join airports a on ct.id = a.city_id\n" +
            "where a.id = ?1", nativeQuery = true)
    Country findCountryByAirportId(long airportId);

    @Query(value = "select c.* from countries c\n" +
            "join cities ct on c.id = ct.country_id\n" +
            "join airports a on ct.id = a.city_id\n" +
            "join routes r on a.id = r.airport_id\n" +
            "where r.id=?1", nativeQuery = true)
    Country findCountryByRouteId(long routeId);

    @Query(value = "select co.* from cities ci\n" +
            "join countries co on ci.country_id = co.id\n" +
            "where ci.id = ?1", nativeQuery = true)
    Country findCountryByCityId(long cityId);
}
