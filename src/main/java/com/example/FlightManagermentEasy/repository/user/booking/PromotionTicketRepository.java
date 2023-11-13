package com.example.FlightManagermentEasy.repository.user.booking;

import com.example.FlightManagermentEasy.entity.user.booking.PromotionTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PromotionTicketRepository extends JpaRepository<PromotionTicket, Long> {
    PromotionTicket findPromotionTicketByCode(String code);

    List<PromotionTicket> findPromotionTicketsByTicketId(long ticketId);
}
