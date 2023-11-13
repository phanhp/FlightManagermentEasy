package com.example.FlightManagermentEasy.repository.user.user;

import com.example.FlightManagermentEasy.entity.user.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    @Query(value = "select p.* from passengers p\n" +
            "join tickets t on t.passenger_id = p.id\n" +
            "where t.id = ?1", nativeQuery = true)
    Passenger findPassengerByTicketId(long ticketId);

    Passenger findPassengerByIdentity(String identity);
}
