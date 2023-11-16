package com.example.FlightManagermentEasy.restcontroller;

import com.example.FlightManagermentEasy.dto.flight.FlightDTO;
import com.example.FlightManagermentEasy.entity.flight.Flight;
import com.example.FlightManagermentEasy.service.dtoconverter.CollectionConverter;
import com.example.FlightManagermentEasy.service.dtoconverter.ObjectConverter;
import com.example.FlightManagermentEasy.service.service.flight.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class HomeRestController {
    @Autowired
    FlightService flightService;
    @Autowired
    ObjectConverter objectConverter;
    @Autowired
    CollectionConverter collectionConverter;

    @GetMapping("/ajax/flightSearch")
    public Page<FlightDTO> flightSearch (@RequestParam(value = "departureCityId", required = false, defaultValue = "0") Optional<Long> departureCityIdOptional,
                                         @RequestParam(value = "arrivalCityId", required = false, defaultValue = "0") Optional<Long> arrivalCityIdOptional,
                                         @RequestParam(value = "departureTime", required = false, defaultValue = "") Optional<String> departureTimeOptional,
                                         @RequestParam("page") Optional<Integer> page){
        Pageable pageable = PageRequest.of(page.orElse(0), 3);
        long departureCityId = departureCityIdOptional.orElse(Long.valueOf(0));
        long arrivalCityId = arrivalCityIdOptional.orElse(Long.valueOf(0));
        String departureTimeString = departureTimeOptional.orElse("");

        List<Flight> flightList = flightService.searchFlight(departureCityId, arrivalCityId, departureTimeString);
        if (departureCityId == 0 && arrivalCityId == 0 && departureTimeString.isEmpty()) {
            flightList = flightService.findAllFLightAfterNow();
        }
        if (flightList == null) {
            flightList = new ArrayList<>();
        }
        List<FlightDTO> flightDTOList = collectionConverter.toFlightDTOList(flightList);
        Page<FlightDTO> flightDTOPage = flightService.convertFlightDTOListToPage(flightDTOList, pageable);
        return flightDTOPage;
    }

}
