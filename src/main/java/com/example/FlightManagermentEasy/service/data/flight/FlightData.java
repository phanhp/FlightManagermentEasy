package com.example.FlightManagermentEasy.service.data.flight;

import com.example.FlightManagermentEasy.entity.flight.aircraft.Aircraft;
import com.example.FlightManagermentEasy.entity.flight.location.Route;
import com.example.FlightManagermentEasy.service.MUF;
import com.example.FlightManagermentEasy.service.service.flight.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FlightData {
    @Autowired
    FlightDataFunction flightDataFunction;
    @Autowired
    MUF muf;
    @Autowired
    FlightService flightService;

    public void createFlightAndTicketData() {
        List<String> randomPriceListString = muf.toArrayString("100, 110, 120, 130, 140, 150");
        List<Double> randomPriceList = randomPriceListString.stream().map(Double::parseDouble).collect(Collectors.toList());

        List<String> randomFlightDurationListString = muf.toArrayString("90, 120, 150, 180");
        List<Integer> randomFlightDurationList = randomFlightDurationListString.stream().map(Integer::parseInt).collect(Collectors.toList());

        List<List<Route>> routeListByCity = flightDataFunction.findRouteListByCity();

        LocalDateTime seatTime = LocalDateTime.of(2024, 1, 1, 0, 0, 0);

        for (int i = 1; i <= 40; i++) {
            long aircraftId = i % 20;
            if (aircraftId == 0) {
                aircraftId = 20;
            }

            Collections.shuffle(randomPriceList);
            double economyPrice = randomPriceList.get(0);
            double businessPrice = Math.round(economyPrice * 100 * 1.3) / 100;

            Collections.shuffle(randomFlightDurationList);
            LocalDateTime departureTime = seatTime;
            LocalDateTime arrivalTime = departureTime.plusMinutes(randomFlightDurationList.get(0));

            Collections.shuffle(routeListByCity);
            List<Route> departureRouteList = routeListByCity.get(0);
            Collections.shuffle(departureRouteList);
            Route departureRoute = departureRouteList.get(0);
            List<Route> arrivalRouteList = routeListByCity.get(1);
            Collections.shuffle(arrivalRouteList);
            Route arrivalRoute = arrivalRouteList.get(0);

            flightDataFunction.createFlightAndTicketData(departureRoute, arrivalRoute, departureTime, arrivalTime, economyPrice, businessPrice, aircraftId);
            seatTime = seatTime.plusHours(1);
        }
//        flightService.createFlightResult(1,1,100,
//                "01/01/2024 18:00","01/01/2024 20:00",
//                120, 150);
    }
}
