package com.example.FlightManagermentEasy.controller;

import com.example.FlightManagermentEasy.entity.user.bank.BankAccount;
import com.example.FlightManagermentEasy.repository.user.bank.BankAccountRepository;
import com.example.FlightManagermentEasy.service.MUF;
import com.example.FlightManagermentEasy.service.data.MyTimeData;
import com.example.FlightManagermentEasy.service.data.aircraft.AircraftData;
import com.example.FlightManagermentEasy.service.data.flight.FlightData;
import com.example.FlightManagermentEasy.service.data.location.LocationData;
import com.example.FlightManagermentEasy.service.data.person.AccountData;
import com.example.FlightManagermentEasy.service.data.promotionTicket.PromotionTicketData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class DataGenerateController {
    @Autowired
    MUF muf;
    @Autowired
    LocationData locationData;
    @Autowired
    AircraftData aircraftData;
    @Autowired
    FlightData flightData;
    @Autowired
    PromotionTicketData promotionTicketData;
    @Autowired
    MyTimeData myTimeData;
    @Autowired
    AccountData accountData;
    @Autowired
    BankAccountRepository bankAccountRepository;

    @GetMapping("/flight-management/data-generate-page")
    public String dataGeneratePage(Model model) {
        return "flight-management/dataGenerate";
    }

    @GetMapping("/flight-management/location-data-generate")
    public String locationDataGenerate() {
        locationData.createCountryData();
        locationData.createCityData();
        locationData.creatAirportData();
        locationData.createRouteData();
        return "redirect:/flight-management/data-generate-page";
    }

    @GetMapping("/flight-management/aircraft-data-generate")
    public String aircraftDataGenerate() {
        aircraftData.createAirlineData();
        aircraftData.createAircraftData();
        return "redirect:/flight-management/data-generate-page";
    }

    @GetMapping("/flight-management/flight-ticket-generate")
    public String flightAndTicketDataGenerate() {
        flightData.createFlightAndTicketData();
        return "redirect:/flight-management/data-generate-page";
    }

    @GetMapping("/flight-management/promotion-ticket-generate")
    public String promotionTicketDataGenerate() {
        promotionTicketData.createPromotionTicketData();
        return "redirect:/flight-management/data-generate-page";
    }

    @GetMapping("/flight-management/mytime-generate")
    public String myTimeDataGenerate() {
        myTimeData.createMyTimeData();
        return "redirect:/flight-management/data-generate-page";
    }

    @GetMapping("/flight-management/account-generate")
    public String accountDataGenerate() {
        accountData.generateAccountData();
        return "redirect:/flight-management/data-generate-page";
    }

    @GetMapping("/flight-management/bank-account-generate")
    public String myBankAccountGenerate() {
        BankAccount flightManagement = new BankAccount("FlightManagement", 100000000);
        bankAccountRepository.save(flightManagement);
        List<String> moneyString = muf.toArrayString("30, 100, 6000, 9000, 15000");
        List<Double> moneyList = moneyString.stream().map(Double::parseDouble).collect(Collectors.toList());
        for (int i = 1; i <= 100; i++) {
            Collections.shuffle(moneyList);
            BankAccount bankAccount = new BankAccount("Bank" + i, moneyList.get(0));
            bankAccountRepository.save(bankAccount);
        }
        return "redirect:/flight-management/data-generate-page";
    }


}
