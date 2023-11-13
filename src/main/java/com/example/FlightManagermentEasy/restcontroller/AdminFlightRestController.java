package com.example.FlightManagermentEasy.restcontroller;

import com.example.FlightManagermentEasy.dto.flight.aircraft.AircraftDTO;
import com.example.FlightManagermentEasy.dto.flight.location.AirportDTO;
import com.example.FlightManagermentEasy.dto.flight.location.CityDTO;
import com.example.FlightManagermentEasy.dto.flight.location.CountryDTO;
import com.example.FlightManagermentEasy.dto.flight.location.RouteDTO;
import com.example.FlightManagermentEasy.entity.flight.location.Airport;
import com.example.FlightManagermentEasy.entity.flight.location.City;
import com.example.FlightManagermentEasy.entity.flight.location.Route;
import com.example.FlightManagermentEasy.repository.flight.location.AirportRepository;
import com.example.FlightManagermentEasy.repository.flight.location.CityRepository;
import com.example.FlightManagermentEasy.repository.flight.location.CountryRepository;
import com.example.FlightManagermentEasy.repository.flight.location.RouteRepository;
import com.example.FlightManagermentEasy.service.dtoconverter.CollectionConverter;
import com.example.FlightManagermentEasy.service.dtoconverter.ObjectConverter;
import com.example.FlightManagermentEasy.service.service.flight.aircraft.AircraftService;
import com.example.FlightManagermentEasy.service.service.flight.location.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class AdminFlightRestController {
    @Autowired
    CountryRepository countryRepository;
    @Autowired
    CityRepository cityRepository;
    @Autowired
    AirportRepository airportRepository;
    @Autowired
    RouteRepository routeRepository;
    @Autowired
    ObjectConverter objectConverter;
    @Autowired
    CollectionConverter collectionConverter;
    @Autowired
    LocationService locationService;
    @Autowired
    AircraftService aircraftService;

    //Time
    @GetMapping("/ajax/getRouteListByTime")
    @ResponseBody
    public List<RouteDTO> getRoutesByTime(@RequestParam(value = "idCountryVal") Optional<Long> optionalCountryId,
                                          @RequestParam(value = "idCityVal") Optional<Long> optionalCityId,
                                          @RequestParam(value = "idAirportVal") Optional<Long> optionalAirportId,
                                          @RequestParam(value = "idTimeVal") Optional<String> optionalTimeId) {
        long countryId = optionalCountryId.orElse(Long.valueOf(0));
        long cityId = optionalCityId.orElse(Long.valueOf(0));
        long airportId = optionalAirportId.orElse(Long.valueOf(0));
        String inputTime = optionalTimeId.orElse("");
        return locationService.findAllAppropriateRoutesByOptional(countryId, cityId, airportId, inputTime);
    }

    @GetMapping("/ajax/getAircraftListByTime")
    @ResponseBody
    public List<AircraftDTO> getAppropriateAircraftList(@RequestParam(value = "departureTime") Optional<String> optionalDepartureTime,
                                                        @RequestParam(value = "arrivalTime") Optional<String> optionalArrivalTime) {
        String departureTime = optionalDepartureTime.orElse("");
        String arrivalTime = optionalArrivalTime.orElse("");
        return aircraftService.findAppropriateAircraftList(departureTime, arrivalTime);
    }

    //CountryId
    @GetMapping("/ajax/getCityListByCountryId")
    @ResponseBody
    public List<CityDTO> getCityListByCountryId(@RequestParam(value = "idChangeVal") Optional<Long> optionalCountryId) {
        long countryId = optionalCountryId.orElse(Long.valueOf(0));
        return locationService.findCityListByCountryId(countryId);
    }

    @GetMapping("/ajax/getAirportListByCountryId")
    @ResponseBody
    public List<AirportDTO> getAirportListByCountryId(@RequestParam(value = "idChangeVal") Optional<Long> optionalCountryId) {
        long countryId = optionalCountryId.orElse(Long.valueOf(0));
        return locationService.findAirportListByCountryId(countryId);
    }

    @GetMapping("/ajax/getRouteListByCountryId")
    @ResponseBody
    public List<RouteDTO> getRouteListByCountryId(@RequestParam(value = "idChangeVal") Optional<Long> optionalCountryId,
                                                  @RequestParam(value = "idTimeVal") Optional<String> optionalInputTime) {
        long countryId = optionalCountryId.orElse(Long.valueOf(0));
        String inputTime = optionalInputTime.orElse("");
        return locationService.findAppropriateRouteListByCountryId(countryId, inputTime);
    }

    //CityId
    @GetMapping("/ajax/getCountryByCityId")
    @ResponseBody
    public CountryDTO getCountryByCityId(@RequestParam("idChangeVal") Optional<Long> optionalCityId) {
        long cityId = optionalCityId.orElse(Long.valueOf(0));
        return locationService.findCountryByCityId(cityId);
    }

    @GetMapping("/ajax/getAirportListByCityId")
    @ResponseBody
    public List<AirportDTO> getAirportListByCityId(@RequestParam("idChangeVal") Optional<Long> optionalCityId) {
        long cityId = optionalCityId.orElse(Long.valueOf(0));
        return locationService.findAirportListByCityId(cityId);
    }

    @GetMapping("/ajax/getRouteListByCityId")
    @ResponseBody
    public List<RouteDTO> getRouteListByCityId(@RequestParam("idChangeVal") Optional<Long> optionalCityId,
                                               @RequestParam(value = "idTimeVal") Optional<String> optionalInputTime) {
        long cityId = optionalCityId.orElse(Long.valueOf(0));
        String inputTime = optionalInputTime.orElse("");
        return locationService.findAppropriateRouteListByCityId(cityId, inputTime);
    }

    //AirportId
    @GetMapping("/ajax/getCountryByAirportId")
    @ResponseBody
    public CountryDTO getCountryByAirportId(@RequestParam("idChangeVal") Optional<Long> optionalAirportId) {
        long airportId = optionalAirportId.orElse(Long.valueOf(0));
        return locationService.findCountryByAirportId(airportId);
    }

    @GetMapping("/ajax/getCityByAirportId")
    @ResponseBody
    public CityDTO getCityByAirportId(@RequestParam("idChangeVal") Optional<Long> optionalAirportId) {
        long airportId = optionalAirportId.orElse(Long.valueOf(0));
        return locationService.findCityByAirportId(airportId);
    }

    @GetMapping("/ajax/getRouteListByAirportId")
    @ResponseBody
    public List<RouteDTO> getRouteListByAirportId(@RequestParam("idChangeVal") Optional<Long> optionalAirportId,
                                                  @RequestParam("idTimeVal") Optional<String> optionalInputTime) {
        long airportId = optionalAirportId.orElse(Long.valueOf(0));
        String inputTime = optionalInputTime.orElse("");
        return locationService.findAppropriateRouteListByAirportId(airportId, inputTime);
    }

    //RouteId
    @GetMapping("/ajax/getCountryByRouteId")
    @ResponseBody
    public CountryDTO getCountryByRouteId(@RequestParam("idChangeVal") Optional<Long> optionalRouteId) {
        long routeId = optionalRouteId.orElse(Long.valueOf(0));
        return locationService.findCountryByRouteId(routeId);
    }

    @GetMapping("/ajax/getCityByRouteId")
    @ResponseBody
    public CityDTO getCityByRouteId(@RequestParam("idChangeVal") Optional<Long> optionalRouteId) {
        long routeId = optionalRouteId.orElse(Long.valueOf(0));
        return locationService.findCityByRouteId(routeId);
    }

    @GetMapping("/ajax/getAirportByRouteId")
    @ResponseBody
    public AirportDTO getAirportByRouteId(@RequestParam("idChangeVal") Optional<Long> optionalRouteId) {
        long routeId = optionalRouteId.orElse(Long.valueOf(0));
        return locationService.findAirportByRouteId(routeId);
    }

}
