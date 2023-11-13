package com.example.FlightManagermentEasy.service.data.flight;

import com.example.FlightManagermentEasy.entity.Ticket;
import com.example.FlightManagermentEasy.entity.flight.Flight;
import com.example.FlightManagermentEasy.entity.flight.aircraft.Aircraft;
import com.example.FlightManagermentEasy.entity.flight.aircraft.Cabin;
import com.example.FlightManagermentEasy.entity.flight.aircraft.Seat;
import com.example.FlightManagermentEasy.entity.flight.location.Route;
import com.example.FlightManagermentEasy.repository.TicketRepository;
import com.example.FlightManagermentEasy.repository.flight.FlightRepository;
import com.example.FlightManagermentEasy.repository.flight.aircraft.AircraftRepository;
import com.example.FlightManagermentEasy.repository.flight.aircraft.CabinRepository;
import com.example.FlightManagermentEasy.repository.flight.aircraft.SeatRepository;
import com.example.FlightManagermentEasy.repository.flight.location.RouteRepository;
import com.example.FlightManagermentEasy.service.MUF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FlightDataFunction {
    @Autowired
    RouteRepository routeRepository;
    @Autowired
    FlightRepository flightRepository;
    @Autowired
    CabinRepository cabinRepository;
    @Autowired
    AircraftRepository aircraftRepository;
    @Autowired
    SeatRepository seatRepository;
    @Autowired
    TicketRepository ticketRepository;

    public List<List<Route>> findRouteListByCity() {
        List<Route> Hanoi = routeRepository.findRoutesByAirport_CityName("Hanoi");
        List<Route> Hue = routeRepository.findRoutesByAirport_CityName("Hue");
        List<Route> Danang = routeRepository.findRoutesByAirport_CityName("Danang");
        List<Route> ThaiNguyen = routeRepository.findRoutesByAirport_CityName("ThaiNguyen");
        List<Route> HaiPhong = routeRepository.findRoutesByAirport_CityName("HaiPhong");
        List<Route> ThanhHoa = routeRepository.findRoutesByAirport_CityName("ThanhHoa");
        List<Route> BinhDuong = routeRepository.findRoutesByAirport_CityName("BinhDuong");
        List<Route> Ohio = routeRepository.findRoutesByAirport_CityName("Ohio");
        List<Route> NewYork = routeRepository.findRoutesByAirport_CityName("NewYork");
        List<List<Route>> allRouteList = new ArrayList<>();
        allRouteList.add(Hanoi);
        allRouteList.add(Hue);
        allRouteList.add(Danang);
        allRouteList.add(ThaiNguyen);
        allRouteList.add(HaiPhong);
        allRouteList.add(ThanhHoa);
        allRouteList.add(BinhDuong);
        allRouteList.add(Ohio);
        allRouteList.add(NewYork);
        return allRouteList;
    }

    public void createFlightAndTicketData(Route departureRoute, Route arrivalRoute,
                                          LocalDateTime departureTime, LocalDateTime arrivalTime,
                                          double economyPrice, double businessPrice,
                                          long aircraftId) {
        MUF muf = new MUF();

        Aircraft aircraft = aircraftRepository.findById(aircraftId).get();

        List<Flight> flightList = flightRepository.findAll();
        Set<String> pnrSet = flightList.stream().map(Flight::getPnr).collect(Collectors.toSet());
        String pnr = muf.generateRandomKeyInSet(pnrSet, 6);
        Flight flight = new Flight(pnr, departureTime, departureRoute,
                arrivalTime, arrivalRoute,
                aircraft);
        flightRepository.save(flight);

        List<Cabin> economyCabin = cabinRepository.findEconomyCabinByAircraftId(aircraftId);
        List<Cabin> businessCabin = cabinRepository.findBusinessCabinByAircraftId(aircraftId);

        for (int i = 0; i < economyCabin.size(); i++) {
            List<Seat> seatList = seatRepository.findSeatsByCabinId(economyCabin.get(i).getId());
            for (int j = 0; j < seatList.size(); j++) {
                Ticket ticket = new Ticket(economyPrice, true, false, seatList.get(j), flight);
                ticketRepository.save(ticket);
            }
        }

        for (int i = 0; i < businessCabin.size(); i++) {
            List<Seat> seatList = seatRepository.findSeatsByCabinId(businessCabin.get(i).getId());
            for (int j = 0; j < seatList.size(); j++) {
                Ticket ticket = new Ticket(businessPrice, true, false, seatList.get(j), flight);
                ticketRepository.save(ticket);
            }
        }
    }
}
