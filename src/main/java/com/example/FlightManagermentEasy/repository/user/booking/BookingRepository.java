package com.example.FlightManagermentEasy.repository.user.booking;

import com.example.FlightManagermentEasy.entity.user.booking.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query(value = "select b.* from bookings b\n" +
            "join accounts a on b.account_id = a.id\n" +
            "where a.id = ?1 and b.purchased = false", nativeQuery = true)
    List<Booking> findNonPurchasedBookingsByAccountId(long accountId);

    @Query(value = "select b.* from bookings b\n" +
            "join accounts a on b.account_id = a.id\n" +
            "where a.id = ?1 and b.purchased= true", nativeQuery = true)
    List<Booking> findPurchasedBookingByAccountId(long accountId);
}
