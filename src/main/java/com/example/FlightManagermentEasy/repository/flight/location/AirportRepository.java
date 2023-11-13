package com.example.FlightManagermentEasy.repository.flight.location;


import com.example.FlightManagermentEasy.entity.flight.location.Airport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AirportRepository extends JpaRepository<Airport, Long> {
    @Query(value = "select a.* from routes r\n" +
            "join airports a on r.airport_id = a.id\n" +
            "where r.id = ?1", nativeQuery = true)
    Airport findAirportByRouteId(long routeId);

    List<Airport> findAirportsByCityCountryId(long countryId);

    List<Airport> findAirportsByCityId(long cityId);



}
