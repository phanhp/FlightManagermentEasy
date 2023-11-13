package com.example.FlightManagermentEasy.service.data.promotionTicket;

import com.example.FlightManagermentEasy.entity.user.booking.PromotionTicket;
import com.example.FlightManagermentEasy.repository.user.booking.PromotionTicketRepository;
import com.example.FlightManagermentEasy.service.MUF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PromotionTicketData {
    @Autowired
    PromotionTicketRepository promotionTicketRepository;
    @Autowired
    MUF muf;
    public void createPromotionTicketData(){
        List<String> randomValueListString = muf.toArrayString("0.02, 0.05, 0.1, 0.2, 0.5");
        List<Double> randomValueList = randomValueListString.stream().map(Double::parseDouble).collect(Collectors.toList());
        Set<String> codeSet = new HashSet<>();
        List<String> alphabet = muf.toArrayString1("qwertyuiopasdfghjklzxcvbnm1234567890");
        for (int i = 1; i <= 200; i++) {
            String code = "";
            do {
                for (int j = 1; j <= 10; j++) {
                    Collections.shuffle(alphabet);
                    code = code + alphabet.get(0);
                }
            } while (codeSet.add(code) == false);
            Collections.shuffle(randomValueList);
            double value = randomValueList.get(0);
            LocalDateTime expiredDate = LocalDateTime.now().plusMonths(9);
            PromotionTicket promotionTicket = new PromotionTicket(code,value, expiredDate, true);
            promotionTicketRepository.save(promotionTicket);
        }
    }
}
