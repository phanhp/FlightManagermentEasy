package com.example.FlightManagermentEasy.repository.user.user;

import com.example.FlightManagermentEasy.entity.user.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findAccountByAccountName(String accountName);

    Account findAccountByEmail(String email);

    Account findAccountByIdentity(String identity);

    @Query(value = "select a.* from accounts a\n" +
            "join bookings b on a.id = b.account_id\n" +
            "join tickets t on t.booking_id = b.id\n" +
            "where t.id = ?1", nativeQuery = true)
    Account findAccountByTicketId(long ticketId);

    @Query(value = "select a.* from accounts a\n" +
            "where a.account_name = ?1 and a.password=?2", nativeQuery = true)
    Account findAccountByAccountNameAndPassword(String username, String password);
}
