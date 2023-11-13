package com.example.FlightManagermentEasy.service.session;

import com.example.FlightManagermentEasy.entity.MyTime;
import com.example.FlightManagermentEasy.repository.MyTimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.time.LocalDateTime;

@Service
@SessionScope
public class ThisMomentSession {
    @Autowired
    MyTimeRepository myTimeRepository;

    private LocalDateTime thisMoment;

    public LocalDateTime getThisMoment() {
        MyTime myTime = myTimeRepository.findById(Long.valueOf(1)).get();
        this.thisMoment = myTime.getTime();
        return this.thisMoment;
    }

    public void setThisMoment(LocalDateTime thisMoment) {
        this.thisMoment = thisMoment;
        MyTime myTime = myTimeRepository.findById(Long.valueOf(1)).get();
        myTime.setTime(this.thisMoment);
        myTimeRepository.save(myTime);
    }
}
