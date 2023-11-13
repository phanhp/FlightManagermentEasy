package com.example.FlightManagermentEasy.controller;

import com.example.FlightManagermentEasy.entity.MyTime;
import com.example.FlightManagermentEasy.repository.MyTimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Controller
public class MyTimeController {
    @Autowired
    MyTimeRepository myTimeRepository;
    @GetMapping("/flight-management/mytime-page")
    public String myTimePage(Model model){
        LocalDateTime thisMoment = myTimeRepository.findById(Long.valueOf(1)).get().getTime();
        int year = thisMoment.getYear();
        int month = thisMoment.getMonthValue();
        int day = thisMoment.getDayOfMonth();
        int hour = thisMoment.getHour();
        int minute = thisMoment.getMinute();
        int second = thisMoment.getSecond();
        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("day", day);
        model.addAttribute("hour", hour);
        model.addAttribute("minute", minute);
        model.addAttribute("second", second);
        return "flight-management/myTimeForm";
    }

    @PostMapping("/flight-management/mytime-update")
    public String myTimeUpdate(@RequestParam("year") int year,
                               @RequestParam("month") int month,
                               @RequestParam("day") int day,
                               @RequestParam("hour") int hour,
                               @RequestParam("minute") int minute,
                               @RequestParam("second") int second){
        LocalDateTime thisMoment = LocalDateTime.of(year, month, day, hour, minute, second);
        MyTime myTime = myTimeRepository.findById(Long.valueOf(1)).get();
        myTime.setTime(thisMoment);
        myTimeRepository.save(myTime);
        return "redirect:/flight-management/mytime-page";
    }
}
