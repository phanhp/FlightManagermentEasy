package com.example.FlightManagermentEasy.service.service.flight.aircraft;

import com.example.FlightManagermentEasy.exception.FetchDataException;
import com.example.FlightManagermentEasy.exception.InvalidDataException;
import com.example.FlightManagermentEasy.dto.flight.aircraft.AircraftDTO;
import com.example.FlightManagermentEasy.entity.Ticket;
import com.example.FlightManagermentEasy.entity.flight.Flight;
import com.example.FlightManagermentEasy.entity.flight.aircraft.*;
import com.example.FlightManagermentEasy.repository.MyTimeRepository;
import com.example.FlightManagermentEasy.repository.TicketRepository;
import com.example.FlightManagermentEasy.repository.flight.FlightRepository;
import com.example.FlightManagermentEasy.repository.flight.aircraft.*;
import com.example.FlightManagermentEasy.repository.user.bank.PaymentHistoryRepository;
import com.example.FlightManagermentEasy.service.MUF;
import com.example.FlightManagermentEasy.service.dtoconverter.CollectionConverter;
import com.example.FlightManagermentEasy.service.dtoconverter.ObjectConverter;
import com.example.FlightManagermentEasy.service.service.TicketService;
import com.example.FlightManagermentEasy.service.service.flight.FlightService;
import com.example.FlightManagermentEasy.service.session.ThisMomentSession;
import org.hibernate.LazyInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.InsufficientResourcesException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class AircraftService {
    @Autowired
    ObjectConverter objectConverter;
    @Autowired
    CollectionConverter collectionConverter;
    @Autowired
    MUF muf;
    @Autowired
    AirlineRepository airlineRepository;
    @Autowired
    AircraftRepository aircraftRepository;
    @Autowired
    CabinRepository cabinRepository;
    @Autowired
    SeatRowRepository seatRowRepository;
    @Autowired
    SeatRepository seatRepository;
    @Autowired
    MyTimeRepository myTimeRepository;
    @Autowired
    FlightRepository flightRepository;
    @Autowired
    TicketRepository ticketRepository;
    @Autowired
    PaymentHistoryRepository paymentHistoryRepository;
    @Autowired
    TicketService ticketService;
    @Autowired
    ThisMomentSession thisMomentSession;
    @Autowired
    FlightService flightService;

    //View AircraftList
    public Page<AircraftDTO> convertAircraftDTOListToPage(List<AircraftDTO> aircraftDTOList, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), aircraftDTOList.size());
        return new PageImpl<>(aircraftDTOList.subList(start, end), pageable, aircraftDTOList.size());
    }

    public Page<AircraftDTO> viewAllAircraftPage(Pageable pageable) {
        List<Aircraft> aircraftList = aircraftRepository.findAll();
        List<AircraftDTO> aircraftDTOList = collectionConverter.toAircraftDTOList(aircraftList);
        return convertAircraftDTOListToPage(aircraftDTOList, pageable);
    }

    public List<AircraftDTO> findAppropriateAircraftList(String departureTime, String arrivalTime) {
        if (departureTime == null) {
            departureTime = "";
        }
        if (arrivalTime == null) {
            arrivalTime = "";
        }
        if (!departureTime.isEmpty() && !arrivalTime.isEmpty()) {
            LocalDateTime begin = muf.stringToLocalDateTime(departureTime).minusHours(1);
            LocalDateTime end = muf.stringToLocalDateTime(arrivalTime).plusHours(1);
            List<Aircraft> aircraftList = aircraftRepository.findAppropriateAircraftsByTime(begin, end);
            List<AircraftDTO> aircraftDTOList = collectionConverter.toAircraftDTOList(aircraftList);
            return aircraftDTOList;
        }
        if (!departureTime.isEmpty() && arrivalTime.isEmpty()) {
            String newArrivalTime = muf.localDateTimeToString(muf.stringToLocalDateTime(departureTime).plusHours(24));
            return findAppropriateAircraftList(departureTime, newArrivalTime);
        }
        if (departureTime.isEmpty() && !arrivalTime.isEmpty()) {
            String newDepartureTime = muf.localDateTimeToString(muf.stringToLocalDateTime(arrivalTime).minusHours(24));
            return findAppropriateAircraftList(newDepartureTime, arrivalTime);
        }
        List<Aircraft> aircraftList = aircraftRepository.findAll();
        List<AircraftDTO> aircraftDTOList = collectionConverter.toAircraftDTOList(aircraftList);
        return aircraftDTOList;
    }


    //CRUD
    public void createAircraft(Airline airline, String aircraftName,
                               int economySeatRowAmount, int businessSeatRowAmount,
                               int economySeatsPerRow, int businessSeatsPerRow) throws InvalidDataException {
        if (airline == null) {
            throw new InvalidDataException("Airline Can Not Be Found");
        }
        Aircraft aircraft = new Aircraft(aircraftName, airline);
        aircraftRepository.save(aircraft);
        Cabin economy = new Cabin("Economy", aircraft);
        Cabin business = new Cabin("Business", aircraft);
        cabinRepository.save(economy);
        cabinRepository.save(business);
        List<String> economySeatRowStringList = muf.autoGenerateSeatRow(economySeatRowAmount);
        for (int i = 0; i < economySeatRowStringList.size(); i++) {
            SeatRow seatRow = new SeatRow(economySeatRowStringList.get(i), economy);
            seatRowRepository.save(seatRow);
            for (int j = 1; j <= economySeatsPerRow; j++) {
                Seat seat = new Seat(seatRow.getName() + (j + 1), seatRow);
                seatRepository.save(seat);
            }
        }
        List<String> businessSeatRowStringList = muf.autoGenerateSeatRow(businessSeatRowAmount);
        for (int i = 0; i < businessSeatRowStringList.size(); i++) {
            SeatRow seatRow = new SeatRow(businessSeatRowStringList.get(i), economy);
            seatRowRepository.save(seatRow);
            for (int j = 1; j <= businessSeatsPerRow; j++) {
                Seat seat = new Seat(seatRow.getName() + (j + 1), seatRow);
                seatRepository.save(seat);
            }
        }
    }

    public void createAircraft(long airlineId, String aircraftName,
                               int economySeatRowAmount, int businessSeatRowAmount,
                               int economySeatsPerRow, int businessSeatsPerRow) throws InvalidDataException {
        Airline airline = airlineRepository.findById(airlineId).orElse(null);
        try {
            createAircraft(airline, aircraftName, economySeatRowAmount, businessSeatRowAmount, economySeatsPerRow, businessSeatsPerRow);
        } catch (InvalidDataException e) {
            e.printStackTrace();
            throw new InvalidDataException(e.getMessage());
        }


    }

//    @Transactional(rollbackFor = InvalidDataException.class)
//    public void updateAircraft(long aircraftId,
//                               long airlineId, String aircraftName,
//                               int economySeatRowAmount, int economySeat,
//                               int businessSeatRow, int businessSeat) {
//        LocalDateTime thisMoment = thisMomentSession.getThisMoment();
//        Airline airline = airlineRepository.findById(airlineId).orElse(null);
//        if (airline == null) {
//            airline = new Airline();
//            airlineRepository.save(airline);
//        }
//        Aircraft aircraft = aircraftRepository.findById(aircraftId).orElse(new Aircraft());
//        if (aircraftName == null) {
//            aircraftName = "";
//        }
//        aircraft.setName(aircraftName);
//        aircraft.setAirline(airline);
//        aircraftRepository.save(aircraft);
//
//        Cabin economy;
//        Cabin business;
//        try {
//            economy = cabinRepository.findEconomyCabinByAircraftId(aircraft.getId()).get(0);
//        } catch (Exception e) {
//            economy = new Cabin("Economy", aircraft);
//            cabinRepository.save(economy);
//        }
//
//        try {
//            business = cabinRepository.findBusinessCabinByAircraftId(aircraft.getId()).get(0);
//        } catch (Exception e) {
//            business = new Cabin("Business", aircraft);
//            cabinRepository.save(business);
//        }
//        try {
//            reformCabin(economySeat, economySeatRow, economy);
//        } catch (InvalidDataException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            reformCabin(businessSeat, businessSeatRow, business);
//        } catch (InvalidDataException e) {
//            e.printStackTrace();
//        }
//
//        List<Flight> flightList = flightRepository.findFlightsByAircraftIdAfterNow(aircraftId, thisMoment);
//        if (flightList != null) {
//            for (int i = 0; i < flightList.size(); i++) {
//                Flight flight = flightList.get(i);
//                List<Seat> seatList = seatRepository.findSeatsByFlightIdWasNotTicketed(flight.getId());
//                if (seatList != null) {
//                    for (int j = 0; j < seatList.size(); j++) {
//                        Seat seat = seatList.get(j);
//                        if (isEconomySeat(seat)) {
//                            double price = flightService.getEconomyPrice(flight);
//                            try {
//                                ticketService.createNewTicket(price, seat, flight);
//                            } catch (Exception e) {
//
//                            }
//                        }
//                        if (isBussinessSeat(seat)) {
//                            double price = flightService.getBusinessPrice(flight);
//                            try {
//                                ticketService.createNewTicket(price, seat, flight);
//                            } catch (Exception e) {
//
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }

//    @Transactional(rollbackFor = InvalidDataException.class)
//    public void reformCabin(int seatNumber, int seatRowNumber, Cabin cabin) throws InvalidDataException {
//        List<SeatRow> seatRowList = seatRowRepository.findSeatRowsByCabinId(cabin.getId());
//        if (seatRowList == null) {
//            seatRowList = new ArrayList<>();
//        }
//        int oldSeatRowNumber = seatRowList.size();
//
//        for (int i = 0; i < seatRowList.size(); i++) {
//            try {
//                reformSeatRow(seatNumber, seatRowList.get(i));
//            } catch (InvalidDataException e) {
//                System.out.println("-----reformCabin(int seatNumber, int seatRowNumber, Cabin cabin) fail-----");
//                throw new InvalidDataException(e.getMessage());
//            }
//        }
//
//        if (oldSeatRowNumber <= seatRowNumber) {
//            List<String> newSeatRowList = differentRowList(seatRowNumber, oldSeatRowNumber);
//            for (int i = 0; i < newSeatRowList.size(); i++) {
//                SeatRow seatRow = new SeatRow(newSeatRowList.get(i), cabin);
//                seatRowRepository.save(seatRow);
//                try {
//                    reformSeatRow(seatRowNumber, seatRow);
//                } catch (InvalidDataException e) {
//                    throw new InvalidDataException(e.getMessage());
//                }
//            }
//        }
//
//        if (oldSeatRowNumber > seatRowNumber) {
//            for (int i = seatRowNumber; i < seatRowList.size(); i++) {
//                try {
//                    removeSeatRow(seatRowList.get(i));
//                    seatRowList.remove(seatRowList.get(i));
//                } catch (InvalidDataException e) {
//                    System.out.println("-----reformCabin(int seatNumber, int seatRowNumber, Cabin cabin)-----");
//                    throw new InvalidDataException(e.getMessage());
//                }
//            }
//        }
//    }

//    @Transactional(rollbackFor = InvalidDataException.class)
//    public void reformSeatRow(int seatNumber, SeatRow seatRow) throws InvalidDataException {
//        List<Seat> seatList = seatRepository.findSeatsBySeatRowId(seatRow.getId());
//        if (seatList == null) {
//            seatList = new ArrayList<>();
//        }
//        int oldSeatNumber = seatList.size();
//
//        if (oldSeatNumber <= seatNumber) {
//            List<Integer> newSeatList = differentSeatPerRowList(seatNumber, oldSeatNumber);
//            for (int i = 0; i < newSeatList.size(); i++) {
//                String name = seatRow.getName() + newSeatList.get(i);
//                Seat seat = new Seat(name, seatRow);
//                seatRepository.save(seat);
//            }
//        }
//
//        if (oldSeatNumber > seatNumber) {
//            for (int i = seatNumber; i < seatList.size(); i++) {
//                try {
//                    removeSeat(seatList.get(i));
//                } catch (InvalidDataException e) {
//                    System.out.println("-----reformSeatRow(int seatNumber, SeatRow seatRow)-----");
//                    throw new InvalidDataException(e.getMessage());
//                }
//            }
//        }
//
//    }

    public void fetchRemoveTicketListFromSeat(Seat seat) throws FetchDataException {
        if (seat != null) {
            try {
                List<Ticket> ticketList = seat.getTicketList();
                if (ticketList != null) {
                    for (Ticket ticket : ticketList) {
                        ticket.setSeat(null);
                        ticketRepository.save(ticket);
                    }
                }
            } catch (LazyInitializationException e) {
                throw new FetchDataException("Fetch Remove Fail");
            }
        }
    }

    public void removeTicketListFromSeat(Seat seat) {
        if (seat != null) {
            try {
                fetchRemoveTicketListFromSeat(seat);
            } catch (FetchDataException e) {
                List<Ticket> ticketList = ticketRepository.findTicketsBySeatId(seat.getId());
                if (ticketList != null) {
                    for (Ticket ticket : ticketList) {
                        ticket.setSeat(null);
                        ticketRepository.save(ticket);
                    }
                }
            }
        }
    }

    public void deleteSeat(Seat seat) {
        if (seat != null) {
            removeTicketListFromSeat(seat);
            seatRepository.delete(seat);
        }
    }

    public void deleteSeatRow(SeatRow seatRow) {
        if (seatRow != null) {
            try {
                List<Seat> seatList = seatRow.getSeatList();
                if (seatList != null) {
                    for (Seat seat : seatList) {
                        deleteSeat(seat);
                    }
                }
                seatRowRepository.delete(seatRow);
            } catch (LazyInitializationException e) {
                List<Seat> seatList = seatRepository.findSeatsBySeatRowId(seatRow.getId());
                if (seatList != null) {
                    for (Seat seat : seatList) {
                        deleteSeat(seat);
                    }
                }
                seatRowRepository.delete(seatRow);
            }
        }
    }

    public void deleteCabin(Cabin cabin) {
        if (cabin != null) {
            try {
                List<SeatRow> seatRowList = cabin.getSeatRowList();
                if (seatRowList != null) {
                    for (SeatRow seatRow : seatRowList) {
                        deleteSeatRow(seatRow);
                    }
                }
                cabinRepository.delete(cabin);
            } catch (LazyInitializationException e) {
                List<SeatRow> seatRowList = seatRowRepository.findSeatRowsByCabinId(cabin.getId());
                if (seatRowList != null) {
                    for (SeatRow seatRow : seatRowList) {
                        deleteSeatRow(seatRow);
                    }
                }
                cabinRepository.delete(cabin);
            }
        }
    }

    public void fetchDeleteFlightFromAircraft(Aircraft aircraft) throws FetchDataException, InvalidDataException {
        if (aircraft != null) {
            try {
                List<Flight> flightList = aircraft.getFlightList();
                if (flightList != null) {
                    for (Flight flight : flightList) {
                        try {
                            flightService.deleteFlight(flight);
                        } catch (InvalidDataException e) {
                            e.printStackTrace();
                            throw new InvalidDataException(e.getMessage());
                        }
                    }
                }
            } catch (LazyInitializationException e) {
                throw new FetchDataException("Fetch Remove Fail");
            }
        }
    }

    public void deleteFlightFromAircraft(Aircraft aircraft) throws InvalidDataException {
        if (aircraft != null) {
            try {
                fetchDeleteFlightFromAircraft(aircraft);
            } catch (FetchDataException e) {
                List<Flight> flightList = flightRepository.findFlightsByAircraftId(aircraft.getId());
                if (flightList != null) {
                    for (Flight flight : flightList) {
                        try {
                            flightService.deleteFlight(flight);
                        } catch (InvalidDataException exception) {
                            e.printStackTrace();
                            throw new InvalidDataException(e.getMessage());
                        }
                    }
                }
            } catch (InvalidDataException e) {
                e.printStackTrace();
                throw new InvalidDataException(e.getMessage());
            }
        }
    }

    @Transactional(rollbackFor = InvalidDataException.class)
    public void deleteAircraft(Aircraft aircraft) throws InvalidDataException {
        if (aircraft != null) {
            try {
                List<Cabin> cabinList = aircraft.getCabinList();
                if (cabinList != null) {
                    for (Cabin cabin : cabinList) {
                        deleteCabin(cabin);
                    }
                }
                try {
                    deleteFlightFromAircraft(aircraft);
                } catch (InvalidDataException e) {
                    e.printStackTrace();
                    throw new InvalidDataException(e.getMessage());
                }
                aircraftRepository.delete(aircraft);
            } catch (LazyInitializationException e) {
                List<Cabin> cabinList = cabinRepository.findCabinsByAircraftId(aircraft.getId());
                if (cabinList != null) {
                    for (Cabin cabin : cabinList) {
                        deleteCabin(cabin);
                    }
                }
                try {
                    deleteFlightFromAircraft(aircraft);
                } catch (InvalidDataException exception) {
                    e.printStackTrace();
                    throw new InvalidDataException(e.getMessage());
                }
                aircraftRepository.delete(aircraft);
            }
        }
    }

    @Transactional(rollbackFor = InvalidDataException.class)
    public void deleteAirline(Airline airline) throws InvalidDataException {
        if (airline != null) {
            try {
                List<Aircraft> aircraftList = airline.getAircraftList();
                if (aircraftList != null) {
                    for (Aircraft aircraft : aircraftList) {
                        try {
                            deleteAircraft(aircraft);
                        } catch (InvalidDataException e) {
                            e.printStackTrace();
                            throw new InvalidDataException(e.getMessage());
                        }
                    }
                }
            } catch (LazyInitializationException e) {
                List<Aircraft> aircraftList = aircraftRepository.findAircraftsByAirlineId(airline.getId());
                if (aircraftList != null) {
                    for (Aircraft aircraft : aircraftList) {
                        try {
                            deleteAircraft(aircraft);
                        } catch (InvalidDataException exception) {
                            e.printStackTrace();
                            throw new InvalidDataException(e.getMessage());
                        }
                    }
                }
            }
        }
    }

    public String deleteAircraftResult(Aircraft aircraft) {
        try {
            deleteAircraft(aircraft);
            return "Delete Aircraft Successfully";
        } catch (InvalidDataException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public String deleteAircraftResult(long aircraftId) {
        Aircraft aircraft = aircraftRepository.findById(aircraftId).orElse(null);
        return deleteAircraftResult(aircraft);
    }


    public List<Integer> differentSeatPerRowList(int larger, int smaller) {
        if (larger <= smaller) {
            return new ArrayList<>();
        } else {
            List<Integer> smallerList = IntStream.rangeClosed(1, smaller).boxed().collect(Collectors.toList());
            List<Integer> largerList = IntStream.rangeClosed(1, larger).boxed().collect(Collectors.toList());
            List<Integer> result = new ArrayList<>();
            for (Integer seat : largerList) {
                if (!smallerList.contains(seat)) {
                    result.add(seat);
                }
            }
            return result;
        }
    }

    public List<String> differentRowList(int larger, int smaller) {
        if (larger <= smaller) {
            return new ArrayList<>();
        } else {
            List<String> largerList = muf.autoGenerateSeatRow(larger);
            List<String> smallerList = muf.autoGenerateSeatRow(smaller);
            List<String> result = new ArrayList<>();
            for (String num : largerList) {
                if (!smallerList.contains(num)) {
                    result.add(num);
                }
            }
            return result;
        }
    }

    public boolean isEconomySeat(Seat seat) {
        Cabin cabin = cabinRepository.findCabinBySeatId(seat.getId());
        if (cabin != null) {
            if (cabin.getName().equalsIgnoreCase("Economy")) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public boolean isBussinessSeat(Seat seat) {
        Cabin cabin = cabinRepository.findCabinBySeatId(seat.getId());
        if (cabin != null) {
            if (cabin.getName().equalsIgnoreCase("Business")) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public List<Cabin> findCabinListByAircraftId(long aircraftId){
        List<Cabin> cabinList = cabinRepository.findCabinsByFlightId(aircraftId);
        return cabinList;
    }

    public List<SeatRow> findSeatRowListByCabinId(long cabinId){
        List<SeatRow> seatRowList = seatRowRepository.findSeatRowsByCabinId(cabinId);
        return seatRowList;
    }



    public LocalDateTime thisMoment() {
        return thisMomentSession.getThisMoment();
    }
}
