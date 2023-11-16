package com.example.FlightManagermentEasy.service.service.flight;

import com.example.FlightManagermentEasy.exception.FetchDataException;
import com.example.FlightManagermentEasy.exception.InvalidDataException;
import com.example.FlightManagermentEasy.dto.flight.FlightDTO;
import com.example.FlightManagermentEasy.entity.Ticket;
import com.example.FlightManagermentEasy.entity.flight.Flight;
import com.example.FlightManagermentEasy.entity.flight.aircraft.Aircraft;
import com.example.FlightManagermentEasy.entity.flight.aircraft.Cabin;
import com.example.FlightManagermentEasy.entity.flight.aircraft.Seat;
import com.example.FlightManagermentEasy.entity.flight.aircraft.SeatRow;
import com.example.FlightManagermentEasy.entity.flight.location.Route;
import com.example.FlightManagermentEasy.entity.user.bank.PaymentHistory;
import com.example.FlightManagermentEasy.entity.user.booking.PromotionTicket;
import com.example.FlightManagermentEasy.repository.TicketRepository;
import com.example.FlightManagermentEasy.repository.flight.FlightRepository;
import com.example.FlightManagermentEasy.repository.flight.aircraft.AircraftRepository;
import com.example.FlightManagermentEasy.repository.flight.aircraft.CabinRepository;
import com.example.FlightManagermentEasy.repository.flight.aircraft.SeatRepository;
import com.example.FlightManagermentEasy.repository.flight.aircraft.SeatRowRepository;
import com.example.FlightManagermentEasy.repository.flight.location.RouteRepository;
import com.example.FlightManagermentEasy.repository.user.bank.PaymentHistoryRepository;
import com.example.FlightManagermentEasy.repository.user.booking.PromotionTicketRepository;
import com.example.FlightManagermentEasy.service.MUF;
import com.example.FlightManagermentEasy.service.dtoconverter.CollectionConverter;
import com.example.FlightManagermentEasy.service.dtoconverter.ObjectConverter;
import com.example.FlightManagermentEasy.service.service.TicketService;
import com.example.FlightManagermentEasy.service.service.entityStatus.TicketStatus;
import com.example.FlightManagermentEasy.service.session.ThisMomentSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.InsufficientResourcesException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FlightService {
    @Autowired
    ObjectConverter objectConverter;
    @Autowired
    PaymentHistoryRepository paymentHistoryRepository;
    @Autowired
    PromotionTicketRepository promotionTicketRepository;
    @Autowired
    CollectionConverter collectionConverter;
    @Autowired
    FlightRepository flightRepository;
    @Autowired
    RouteRepository routeRepository;
    @Autowired
    TicketRepository ticketRepository;
    @Autowired
    AircraftRepository aircraftRepository;
    @Autowired
    TicketStatus ticketStatus;
    @Autowired
    CabinRepository cabinRepository;
    @Autowired
    SeatRowRepository seatRowRepository;
    @Autowired
    SeatRepository seatRepository;
    @Autowired
    ThisMomentSession thisMomentSession;
    @Autowired
    TicketService ticketService;
    @Autowired
    MUF muf;

    //Convert To Page<FlightDTO>
    public Page<FlightDTO> convertFlightDTOListToPage(List<FlightDTO> flightDTOList, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), flightDTOList.size());
        return new PageImpl<>(flightDTOList.subList(start, end), pageable, flightDTOList.size());
    }

    //User
    //Find All Flight After ThisMoment
    public List<Flight> findAllFLightAfterNow() {
        LocalDateTime thisMoment = thisMomentSession.getThisMoment();
        List<Flight> flightList = flightRepository.findAllFlightsAfterMoment(thisMoment);
        return flightList;
    }

    //Search Flight, Result Give After This Moment
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
        return flightRepository.findAllFlightsAfterMoment(thisMoment);

    }

    //****************************** CRUD FLIGHT **********************************************
    //Create Flight And All Tickets For That Flight
    @Transactional(rollbackFor = InvalidDataException.class)
    public void createFlight(Aircraft aircraft, Route departureRoute, Route arrivalRoute,
                             LocalDateTime departureTime, LocalDateTime arrivalTime,
                             double economyPrice, double businessPrice) throws InvalidDataException {
        if (aircraft == null) {
            throw new InvalidDataException("Aircraft Can Not Be Found");
        }
        if (departureRoute == null) {
            throw new InvalidDataException("Departure Route Can Not Be Found");
        }
        if (arrivalRoute == null) {
            throw new InvalidDataException("Arrival Route Can Not Be Found");
        }
        if (departureTime == null) {
            throw new InvalidDataException("Departure Time Can Not Be Empty");
        }
        if (arrivalTime == null) {
            throw new InvalidDataException("Arrival Time Can Not Be Empty");
        }
        if (departureTime.isAfter(arrivalTime)) {
            throw new InvalidDataException("Departure Time Must Be Before Arrival Time");
        }
        if (economyPrice < 0) {
            throw new InvalidDataException("Economy Price Can Not Be Less Than 0");
        }
        if (businessPrice < 0) {
            throw new InvalidDataException("Business Price Can Not Be Less Than 0");
        }
        if (economyPrice > businessPrice) {
            throw new InvalidDataException("Economy Price Must Be Less Than Business Price");
        }
        Flight flight = new Flight();
        //create String pnr
        List<Flight> flightList = flightRepository.findAll();
        Set<String> pnrSet = flightList.stream().map(Flight::getPnr).collect(Collectors.toSet());
        flight.setPnr(muf.generateRandomKeyInSet(pnrSet, 6));
        //set information for flight
        flight.setAircraft(aircraft);
        flight.setDepartureTime(departureTime);
        flight.setArrivalTime(arrivalTime);
        flight.setDepartureRoute(departureRoute);
        flight.setArrivalRoute(arrivalRoute);
        flightRepository.save(flight);
        //auto generate ticket
        List<Cabin> economyCabinList = cabinRepository.findEconomyCabinByAircraftId(aircraft.getId());
        if (economyCabinList == null) {
            throw new InvalidDataException("Economy Cabin Can Not Be Found");
        }
        if (economyCabinList.isEmpty()) {
            throw new InvalidDataException("Economy Cabin Can Not Be Found");
        }
        List<Cabin> businessCabinList = cabinRepository.findBusinessCabinByAircraftId(aircraft.getId());
        if (businessCabinList == null) {
            throw new InvalidDataException("Business Cabin Can Not Be Found");
        }
        if (businessCabinList.isEmpty()) {
            throw new InvalidDataException("Business Cabin Can Not Be Found");
        }

        Cabin economyCabin = economyCabinList.get(0);
        List<SeatRow> economyRow = seatRowRepository.findSeatRowsByCabinId(economyCabin.getId());
        if (economyRow == null) {
            throw new InvalidDataException("Economy Seat Row Can Not Be Found");
        }
        economyRow.sort(Comparator.comparing(SeatRow::getName));
        for (int i = 0; i < economyRow.size(); i++) {
            List<Seat> seatList = seatRepository.findSeatsBySeatRowId(economyRow.get(i).getId());
            seatList.sort(Comparator.comparing(Seat::getName));
            for (int j = 0; j < seatList.size(); j++) {
                Ticket ticket = new Ticket(economyPrice, true, false, seatList.get(j), flight);
                ticketRepository.save(ticket);
            }
        }

        Cabin businessCabin = businessCabinList.get(0);
        List<SeatRow> businessRow = seatRowRepository.findSeatRowsByCabinId(businessCabin.getId());
        businessRow.sort(Comparator.comparing(SeatRow::getName));
        for (int i = 0; i < businessRow.size(); i++) {
            List<Seat> seatList = seatRepository.findSeatsBySeatRowId(businessRow.get(i).getId());
            seatList.sort(Comparator.comparing(Seat::getName));
            for (int j = 0; j < seatList.size(); j++) {
                Ticket ticket = new Ticket(businessPrice, true, false, seatList.get(j), flight);
                ticketRepository.save(ticket);
            }
        }
    }

    public String createFlightResult(long aircraftId, long departureRouteId, long arrivalRouteId,
                                     String departureTimeString, String arrivalTimeString,
                                     double economyPrice, double businessPrice) {
        Aircraft aircraft = aircraftRepository.findById(aircraftId).orElse(null);
        Route departureRoute = routeRepository.findById(departureRouteId).orElse(null);
        Route arrivalRoute = routeRepository.findById(arrivalRouteId).orElse(null);
        LocalDateTime departureTime = muf.stringToLocalDateTime(departureTimeString);
        LocalDateTime arrivalTime = muf.stringToLocalDateTime(arrivalTimeString);
        try {
            createFlight(aircraft, departureRoute, arrivalRoute, departureTime, arrivalTime, economyPrice, businessPrice);
            return "Flight Created";
        } catch (InvalidDataException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public String createFlightResult(FlightDTO flightDTO) {
        return createFlightResult(flightDTO.getAircraftId(), flightDTO.getDepartureRouteId(),
                flightDTO.getArrivalRouteId(), flightDTO.getDepartureTime(),
                flightDTO.getArrivalTime(), flightDTO.getEcnonomyPrice(),
                flightDTO.getBusinessPrice());
    }

    public void updateFlight(FlightDTO flightDTO) {

    }

    @Transactional(rollbackFor = InvalidDataException.class)
    public void fetchDeleteFlight(Flight flight) throws FetchDataException {
        if (flight != null) {
            try {
                List<Ticket> ticketList = flight.getTicketList();
                if (ticketList != null) {
                    if (isFlightPerformed(flight)) {
                        for (Ticket ticket : ticketList) {
                            ticketService.deleteTicket(ticket);
                        }
                    } else {
                        for (Ticket ticket : ticketList) {
                            if (ticket.isPurchased()) {
                                ticket = ticketService.refundTicket(ticket, 1);
                                ticketService.deleteTicket(ticket);
                            } else {
                                ticketService.deleteTicket(ticket);
                            }
                        }
                    }
                }
                flightRepository.delete(flight);
            } catch (Exception e) {
                throw new FetchDataException(e.getMessage());
            }
        }
    }

    @Transactional(rollbackFor = InvalidDataException.class)
    public void deleteFlight(Flight flight) throws InvalidDataException {
        if (flight == null) {
            throw new InvalidDataException("Flight Can Not Be Found");
        }
        try {
            fetchDeleteFlight(flight);
        } catch (FetchDataException e) {
            List<Ticket> ticketList = ticketRepository.findTicketsByFlightId(flight.getId());
            if (ticketList != null) {
                if (isFlightPerformed(flight)) {
                    for (Ticket ticket : ticketList) {
                        ticketService.deleteTicket(ticket);
                    }
                } else {
                    for (Ticket ticket : ticketList) {
                        if (ticket.isPurchased()) {
                            try {
                                ticket = ticketService.refundTicket(ticket, 1);
                            } catch (InvalidDataException exception) {
                                exception.printStackTrace();
                                throw new InvalidDataException(exception.getMessage());
                            } catch (InsufficientResourcesException exception) {
                                exception.printStackTrace();
                                throw new InvalidDataException(exception.getMessage());
                            }
                            ticketService.deleteTicket(ticket);
                        } else {
                            ticketService.deleteTicket(ticket);
                        }
                    }
                }
            }
            flightRepository.delete(flight);
        }
    }

    public String deleteFlightResult(Flight flight) {
        try {
            deleteFlight(flight);
            return "Flight Delete Success";
        } catch (InvalidDataException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public String deleteFlightResult(long flightId){
        Flight flight = flightRepository.findById(flightId).orElse(null);
        return deleteFlightResult(flight);
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

    public boolean isFlightPerformed(Flight flight) {
        if (flight == null) {
            return false;
        } else {
            if (flight.getDepartureTime().isBefore(thisMoment())) {
                return true;
            } else {
                return false;
            }
        }
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

    public LocalDateTime thisMoment() {
        return thisMomentSession.getThisMoment();
    }
}
