package com.example.FlightManagermentEasy.service.service.flight;

import com.example.FlightManagermentEasy.dto.flight.FlightDTO;
import com.example.FlightManagermentEasy.entity.Ticket;
import com.example.FlightManagermentEasy.entity.flight.Flight;
import com.example.FlightManagermentEasy.entity.flight.aircraft.Aircraft;
import com.example.FlightManagermentEasy.entity.flight.aircraft.Cabin;
import com.example.FlightManagermentEasy.entity.flight.aircraft.Seat;
import com.example.FlightManagermentEasy.entity.flight.aircraft.SeatRow;
import com.example.FlightManagermentEasy.repository.TicketRepository;
import com.example.FlightManagermentEasy.repository.flight.FlightRepository;
import com.example.FlightManagermentEasy.repository.flight.aircraft.AircraftRepository;
import com.example.FlightManagermentEasy.repository.flight.aircraft.CabinRepository;
import com.example.FlightManagermentEasy.repository.flight.aircraft.SeatRepository;
import com.example.FlightManagermentEasy.repository.flight.aircraft.SeatRowRepository;
import com.example.FlightManagermentEasy.service.MUF;
import com.example.FlightManagermentEasy.service.dtoconverter.CollectionConverter;
import com.example.FlightManagermentEasy.service.dtoconverter.ObjectConverter;
import com.example.FlightManagermentEasy.service.session.ThisMomentSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class FlightService {
    @Autowired
    ObjectConverter objectConverter;
    @Autowired
    CollectionConverter collectionConverter;
    @Autowired
    FlightRepository flightRepository;
    @Autowired
    TicketRepository ticketRepository;
    @Autowired
    AircraftRepository aircraftRepository;
    @Autowired
    CabinRepository cabinRepository;
    @Autowired
    SeatRowRepository seatRowRepository;
    @Autowired
    SeatRepository seatRepository;
    @Autowired
    ThisMomentSession thisMomentSession;
    @Autowired
    MUF muf;

    public Page<FlightDTO> convertFlightDTOListToPage(List<FlightDTO> flightDTOList, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), flightDTOList.size());
        return new PageImpl<>(flightDTOList.subList(start, end), pageable, flightDTOList.size());
    }

    //User
    public List<Flight> findAllFLightAfterNow() {
        LocalDateTime thisMoment = thisMomentSession.getThisMoment();
        List<Flight> flightList = flightRepository.findAllFlightsAfterNow(thisMoment);
        return flightList;
    }

    public List<Flight> searchFlight(long departureCity, long arrivalCity, String departureTimeString) {
        LocalDateTime thisMoment = thisMomentSession.getThisMoment();
        List<Flight> flightList = new ArrayList<>();
        if (departureTimeString == null) {
            departureTimeString = "";
        }
        if (departureCity != 0 && arrivalCity != 0 && !departureTimeString.isEmpty()) {
            LocalDateTime departureTime = muf.localDateToLocalDateTime(muf.stringToLocalDate(departureTimeString));
            LocalDateTime nextDay = departureTime.plusDays(1);
            flightList = flightRepository.findFlightsByDCandACandDT(departureCity, arrivalCity, departureTime, nextDay, thisMoment);
            return flightList;
        }
        if (departureCity != 0 && arrivalCity != 0) {
            flightList = flightRepository.findFlightsByDCandAc(departureCity, arrivalCity, thisMoment);
            return flightList;
        }
        if (departureCity != 0 && !departureTimeString.isEmpty()) {
            LocalDateTime departureTime = muf.localDateToLocalDateTime(muf.stringToLocalDate(departureTimeString));
            LocalDateTime nextDay = departureTime.plusDays(1);
            flightList = flightRepository.findFlightsByDCandDT(departureCity, departureTime, nextDay, thisMoment);
            return flightList;
        }
        if (departureCity != 0) {
            flightList = flightRepository.findFlightsByDC(departureCity, thisMoment);
            return flightList;
        }
        if (arrivalCity != 0 && !departureTimeString.isEmpty()) {
            LocalDateTime departureTime = muf.localDateToLocalDateTime(muf.stringToLocalDate(departureTimeString));
            LocalDateTime nextDay = departureTime.plusDays(1);
            flightList = flightRepository.findFlightsByACandDT(arrivalCity, departureTime, nextDay, thisMoment);
            return flightList;
        }
        if (arrivalCity != 0) {
            flightList = flightRepository.findFlightsByAC(arrivalCity, thisMoment);
            return flightList;
        }
        if (!departureTimeString.isEmpty()) {
            LocalDateTime departureTime = muf.localDateToLocalDateTime(muf.stringToLocalDate(departureTimeString));
            LocalDateTime nextDay = departureTime.plusDays(1);
            flightList = flightRepository.findFlightsByDT(departureTime, nextDay, thisMoment);
            return flightList;
        }
        return flightRepository.findAllFlightsAfterNow(thisMoment);
    }

    public void createFlight(FlightDTO flightDTO) {
        Flight flight = objectConverter.toFlightEntity(flightDTO);
        flightRepository.save(flight);
        long aircraftId = flight.getAircraft().getId();
        List<Cabin> economyCabin = cabinRepository.findEconomyCabinByAircraftId(aircraftId);
        List<Cabin> businessCabin = cabinRepository.findBusinessCabinByAircraftId(aircraftId);

        for (int i = 0; i < economyCabin.size(); i++) {
            List<Seat> seatList = seatRepository.findSeatsByCabinId(economyCabin.get(i).getId());
            for (int j = 0; j < seatList.size(); j++) {
                Ticket ticket = new Ticket(flightDTO.getEcnonomyPrice(), true, true, seatList.get(j), flight);
                ticketRepository.save(ticket);
            }
        }

        for (int i = 0; i < businessCabin.size(); i++) {
            List<Seat> seatList = seatRepository.findSeatsByCabinId(businessCabin.get(i).getId());
            for (int j = 0; j < seatList.size(); j++) {
                Ticket ticket = new Ticket(flightDTO.getBusinessPrice(), true, true, seatList.get(j), flight);
                ticketRepository.save(ticket);
            }
        }
    }



    public void updateFlight(FlightDTO flightDTO) {

    }

    public void deleteFlight(long flightId) {
    }

    public FlightDTO findFlightDTOById(long flightId) {
        Flight flight = flightRepository.findById(flightId).get();
        FlightDTO flightDTO = objectConverter.toFlightDTO(flight);
        return flightDTO;
    }

    public Aircraft findAircraftByFlightId(long flightId) {
        Aircraft aircraft = aircraftRepository.findAircraftByFlightId(flightId);
        return aircraft;
    }

    public List<Cabin> findCabinListByFlightId(long flightId) {
        List<Cabin> cabinList = cabinRepository.findCabinsByFlightId(flightId);
        return cabinList;
    }

    public List<SeatRow> findSeatRowListByFlightId(long flightId) {
        List<SeatRow> seatRowList = seatRowRepository.findSeatRowsByFlightId(flightId);
        return seatRowList;
    }

    public List<Seat> findSeatByFlightId(long flightId) {
        List<Seat> seatList = seatRepository.findSeatsByFlightId(flightId);
        return seatList;
    }

    public double getEconomyPrice(Flight flight) {
        double result = 0;
        List<Ticket> ticketList = ticketRepository.findEconomyTicketsByFlightId(flight.getId());
        if (ticketList != null) {
            result = ticketList.get(0).getPrice();
        }
        return result;
    }

    public double getBusinessPrice(Flight flight) {
        double result = 0;
        List<Ticket> ticketList = ticketRepository.findBusinessTicketByFlightId(flight.getId());
        if (ticketList != null) {
            result = ticketList.get(0).getPrice();
        }
        return result;
    }
}
