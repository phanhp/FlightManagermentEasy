package com.example.FlightManagermentEasy.controller;

import com.example.FlightManagermentEasy.dto.flight.FlightDTO;
import com.example.FlightManagermentEasy.dto.flight.aircraft.AircraftDTO;
import com.example.FlightManagermentEasy.dto.flight.location.AirportDTO;
import com.example.FlightManagermentEasy.dto.flight.location.CityDTO;
import com.example.FlightManagermentEasy.dto.flight.location.CountryDTO;
import com.example.FlightManagermentEasy.dto.flight.location.RouteDTO;
import com.example.FlightManagermentEasy.entity.flight.Flight;
import com.example.FlightManagermentEasy.entity.flight.aircraft.Aircraft;
import com.example.FlightManagermentEasy.entity.flight.location.Airport;
import com.example.FlightManagermentEasy.entity.flight.location.City;
import com.example.FlightManagermentEasy.entity.flight.location.Country;
import com.example.FlightManagermentEasy.entity.flight.location.Route;
import com.example.FlightManagermentEasy.entity.user.Account;
import com.example.FlightManagermentEasy.repository.TicketRepository;
import com.example.FlightManagermentEasy.repository.flight.FlightRepository;
import com.example.FlightManagermentEasy.repository.flight.aircraft.AircraftRepository;
import com.example.FlightManagermentEasy.repository.flight.location.AirportRepository;
import com.example.FlightManagermentEasy.repository.flight.location.CityRepository;
import com.example.FlightManagermentEasy.repository.flight.location.CountryRepository;
import com.example.FlightManagermentEasy.repository.flight.location.RouteRepository;
import com.example.FlightManagermentEasy.service.CommonModel;
import com.example.FlightManagermentEasy.service.MUF;
import com.example.FlightManagermentEasy.service.dtoconverter.CollectionConverter;
import com.example.FlightManagermentEasy.service.dtoconverter.ObjectConverter;
import com.example.FlightManagermentEasy.service.service.flight.FlightService;
import com.example.FlightManagermentEasy.service.service.flight.aircraft.AircraftService;
import com.example.FlightManagermentEasy.service.service.flight.location.LocationService;
import com.example.FlightManagermentEasy.service.session.LoginSession;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class AdminFlightController {
    @Autowired
    MUF muf;
    @Autowired
    ObjectConverter objectConverter;
    @Autowired
    CollectionConverter collectionConverter;
    @Autowired
    FlightService flightService;
    @Autowired
    FlightRepository flightRepository;
    @Autowired
    CityRepository cityRepository;
    @Autowired
    CountryRepository countryRepository;
    @Autowired
    AirportRepository airportRepository;
    @Autowired
    RouteRepository routeRepository;
    @Autowired
    AircraftRepository aircraftRepository;
    @Autowired
    TicketRepository ticketRepository;
    @Autowired
    LocationService locationService;
    @Autowired
    AircraftService aircraftService;
    @Autowired
    LoginSession loginSession;
    @Autowired
    CommonModel commonModel;

    @GetMapping({"/admin/view-flights-page","/admin/view-flights-page/search"})
    public String adminViewFlightPage(Model model,
                                      @RequestParam("page") Optional<Integer> page,
                                      @RequestParam(value = "departureCityId", required = false, defaultValue = "0") Optional<Long> departureCityIdOptional,
                                      @RequestParam(value = "arrivalCityId", required = false, defaultValue = "0") Optional<Long> arrivalCityIdOptional,
                                      @RequestParam(value = "departureTime", required = false, defaultValue = "") Optional<String> departureTimeOptional,
                                      HttpSession session) {
        Pageable pageable = PageRequest.of(page.orElse(0), 4);
        String pageUrlTitle = "adminViewFlightPage";
        String pageUrl = "/admin/view-flights-page";

        commonModel.viewFlightPageModel(model, session,
                departureCityIdOptional, arrivalCityIdOptional, departureTimeOptional,
                page, pageable,
                pageUrlTitle, pageUrl);
        return "adminViewFlights";
    }

    @GetMapping("/admin/create-flight-page")
    public String adminCreateFlightPage(Model model,
                                        HttpSession session) {
        FlightDTO flightDTO = new FlightDTO();
        model.addAttribute("flight", flightDTO);
        model.addAttribute("title", "Create Flight Page");

        List<Aircraft> aircraftList = aircraftRepository.findAll();
        List<AircraftDTO> aircraftDTOList = collectionConverter.toAircraftDTOList(aircraftList);
        model.addAttribute("aircraftList", aircraftDTOList);

        List<Country> countryList = countryRepository.findAll();
        List<CountryDTO> countryDTOList = collectionConverter.toCountryDTOList(countryList);
        model.addAttribute("countryList", countryDTOList);

        List<City> cityList = cityRepository.findAll();
        List<CityDTO> cityDTOList = collectionConverter.toCityDTOList(cityList);
        model.addAttribute("cityList", cityDTOList);

        List<Airport> airportList = airportRepository.findAll();
        List<AirportDTO> airportDTOList = collectionConverter.toAirportDTOList(airportList);
        model.addAttribute("airportList", airportDTOList);

        List<Route> routeList = routeRepository.findAll();
        List<RouteDTO> routeDTOList = collectionConverter.toRouteDTOList(routeList);
        model.addAttribute("routeList", routeList);

        model.addAttribute("action", "/admin/create-flight");
        model.addAttribute("submit", "Creat Flight");

        commonModel.headerModel(model);
        return "adminCreateFlight";
    }

    @PostMapping("/admin/create-flight")
    public String adminCreateFlight(@Valid @ModelAttribute("flight") FlightDTO flightDTO,
                                    @RequestParam("departureRouteId") Optional<Long> optionalDepartureRouteId,
                                    @RequestParam("arrivalRouteId") Optional<Long> optionalArrivalRouteId,
                                    HttpSession session) {
        long departureRouteId = optionalDepartureRouteId.orElse(Long.valueOf(0));
        long arrivalRouteId = optionalArrivalRouteId.orElse(Long.valueOf(0));
        flightDTO.setDepartureRouteId(departureRouteId);
        flightDTO.setArrivalRouteId(arrivalRouteId);
        flightService.createFlight(flightDTO);

        return "redirect:/admin/view-flights-page";
    }

    @GetMapping("/admin/update-flight-page/{flightId}")
    public String adminUpdateFlightPage(@PathVariable("flightId") long flightId,
                                        Model model,
                                        HttpSession session) {
        Flight flight = flightRepository.findById(flightId).get();
        FlightDTO flightDTO = objectConverter.toFlightDTO(flight);
        model.addAttribute("flightDTO", flightDTO);

        List<Aircraft> aircraftList = aircraftRepository.findAll();
        List<AircraftDTO> aircraftDTOList = collectionConverter.toAircraftDTOList(aircraftList);
        model.addAttribute("aircraftList", aircraftDTOList);

        List<Country> countryList = countryRepository.findAll();
        List<CountryDTO> countryDTOList = collectionConverter.toCountryDTOList(countryList);
        model.addAttribute("countryList", countryDTOList);

        List<City> cityList = cityRepository.findAll();
        List<CityDTO> cityDTOList = collectionConverter.toCityDTOList(cityList);
        model.addAttribute("cityList", cityDTOList);

        List<Airport> airportList = airportRepository.findAll();
        List<AirportDTO> airportDTOList = collectionConverter.toAirportDTOList(airportList);
        model.addAttribute("airportList", airportDTOList);

        List<Route> routeList = routeRepository.findAll();
        List<RouteDTO> routeDTOList = collectionConverter.toRouteDTOList(routeList);
        model.addAttribute("routeList", routeList);

        commonModel.headerModel(model);
        return "adminUpdateFlight";
    }

    @PostMapping("/admin/update-flight")
    public String adminUpdateFlight(@Valid @ModelAttribute("flightDTO") FlightDTO flightDTO,
                                    @RequestParam("departureRoute") long departureRouteId,
                                    @RequestParam("arrivalRoute") long arrivalRouteId) {
        flightDTO.setDepartureRouteId(departureRouteId);
        flightDTO.setArrivalRouteId(arrivalRouteId);
        Flight flight = objectConverter.toFlightEntity(flightDTO);
        flightRepository.save(flight);

        return "redirect:/admin/view-flights-page";
    }

    @GetMapping("/admin/delete-flight/{flightId}")
    private String adminDeleteFlight(@PathVariable("flightId") long flightId) {
        flightRepository.deleteById(flightId);
        return "redirect:/admin/view-flights-page";
    }
}
