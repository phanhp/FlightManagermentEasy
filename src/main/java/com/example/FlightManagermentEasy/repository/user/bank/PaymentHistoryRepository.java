package com.example.FlightManagermentEasy.repository.user.bank;

import com.example.FlightManagermentEasy.entity.user.bank.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {
    List<PaymentHistory> findPaymentHistoryByTicketId(long ticketId);
}
