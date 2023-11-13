package com.example.FlightManagermentEasy.service.data;

import com.example.FlightManagermentEasy.entity.MyTime;
import com.example.FlightManagermentEasy.repository.MyTimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MyTimeData {
    @Autowired
    MyTimeRepository myTimeRepository;

    public void createMyTimeData(){
        LocalDateTime time = LocalDateTime.of(2023, 12,1,0,0,0);
        MyTime myTime = new MyTime(time);
        myTimeRepository.save(myTime);
    }
}
