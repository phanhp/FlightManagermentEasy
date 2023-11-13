package com.example.FlightManagermentEasy.service.service.flight.aircraft;

import com.example.FlightManagermentEasy.dto.flight.FlightDTO;
import com.example.FlightManagermentEasy.dto.flight.aircraft.AircraftDTO;
import com.example.FlightManagermentEasy.entity.MyTime;
import com.example.FlightManagermentEasy.entity.Ticket;
import com.example.FlightManagermentEasy.entity.flight.Flight;
import com.example.FlightManagermentEasy.entity.flight.aircraft.*;
import com.example.FlightManagermentEasy.entity.user.bank.PaymentHistory;
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
    public void createAircraft(long airlineId, String aircraftName,
                               int economySeatRowAmount, int economySeatAmountPerRow,
                               int businessSeatRowAmount, int businessSeatAmountPerRow) {
        Airline airline = airlineRepository.findById(airlineId).orElse(null);
        Aircraft aircraft = new Aircraft(aircraftName, airline);
        aircraftRepository.save(aircraft);
        Cabin economyCabin = new Cabin("Economy", aircraft);
        Cabin businessCabin = new Cabin("Business", aircraft);
        cabinRepository.save(economyCabin);
        cabinRepository.save(businessCabin);
        List<String> economySeatRowStringList = muf.autoGenerateSeatRow(economySeatRowAmount);
        List<String> businessSeatRowStringList = muf.autoGenerateSeatRow(businessSeatRowAmount);
        for (int i = 0; i < economySeatRowStringList.size(); i++) {
            SeatRow seatRow = new SeatRow(economySeatRowStringList.get(i), economyCabin);
            seatRowRepository.save(seatRow);
            for (int j = 0; j < economySeatAmountPerRow; j++) {
                Seat seat = new Seat(seatRow.getName() + (j + 1), seatRow);
                seatRepository.save(seat);
            }
        }
        for (int i = 0; i < businessSeatRowStringList.size(); i++) {
            SeatRow seatRow = new SeatRow(businessSeatRowStringList.get(i), businessCabin);
            seatRowRepository.save(seatRow);
            for (int j = 0; j < economySeatAmountPerRow; j++) {
                Seat seat = new Seat(seatRow.getName() + (j + 1), seatRow);
                seatRepository.save(seat);
            }
        }
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public void updateAircraft(long aircraftId,
                               long airlineId, String aircraftName,
                               int economySeatRow, int economySeat,
                               int businessSeatRow, int businessSeat) {
        LocalDateTime thisMoment = thisMomentSession.getThisMoment();
        Airline airline = airlineRepository.findById(airlineId).orElse(null);
        if (airline == null) {
            airline = new Airline();
            airlineRepository.save(airline);
        }
        Aircraft aircraft = aircraftRepository.findById(aircraftId).orElse(new Aircraft());
        if (aircraftName == null) {
            aircraftName = "";
        }
        aircraft.setName(aircraftName);
        aircraft.setAirline(airline);
        aircraftRepository.save(aircraft);

        Cabin economy;
        Cabin business;
        try {
            economy = cabinRepository.findEconomyCabinByAircraftId(aircraft.getId()).get(0);
        } catch (Exception e) {
            economy = new Cabin("Economy", aircraft);
            cabinRepository.save(economy);
        }

        try {
            business = cabinRepository.findBusinessCabinByAircraftId(aircraft.getId()).get(0);
        } catch (Exception e) {
            business = new Cabin("Business", aircraft);
            cabinRepository.save(business);
        }
        reformCabin(economySeat, economySeatRow, economy);
        reformCabin(businessSeat, businessSeatRow, business);

        List<Flight> flightList = flightRepository.findFlightsByAircraftIdAfterNow(aircraftId, thisMoment);
        if (flightList != null) {
            for (int i = 0; i < flightList.size(); i++) {
                Flight flight = flightList.get(i);
                List<Seat> seatList = seatRepository.findSeatsByFlightIdWasNotTicketed(flight.getId());
                if (seatList != null) {
                    for (int j = 0; j < seatList.size(); j++) {
                        Seat seat = seatList.get(j);
                        if (isEconomySeat(seat)) {
                            double price = flightService.getEconomyPrice(flight);
                            ticketService.createNewTicket(price, seat, flight);
                        }
                        if (isBussinessSeat(seat)) {
                            double price = flightService.getBusinessPrice(flight);
                            ticketService.createNewTicket(price, seat, flight);
                        }
                    }
                }
            }
        }
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public void reformCabin(int seatNumber, int seatRowNumber, Cabin cabin) throws RuntimeException {
        List<SeatRow> seatRowList = seatRowRepository.findSeatRowsByCabinId(cabin.getId());
        if (seatRowList == null) {
            seatRowList = new ArrayList<>();
        }
        int oldSeatRowNumber = seatRowList.size();

        for (int i = 0; i < seatRowList.size(); i++) {
            try {
                reformSeatRow(seatNumber, seatRowList.get(i));
            } catch (Exception e) {
                System.out.println("-----reformCabin(int seatNumber, int seatRowNumber, Cabin cabin) fail-----");
                throw new RuntimeException();
            }
        }

        if (oldSeatRowNumber <= seatRowNumber) {
            List<String> newSeatRowList = differentRowList(seatRowNumber, oldSeatRowNumber);
            for (int i = 0; i < newSeatRowList.size(); i++) {
                SeatRow seatRow = new SeatRow(newSeatRowList.get(i), cabin);
                seatRowRepository.save(seatRow);
                try {
                    reformSeatRow(seatRowNumber, seatRow);
                } catch (Exception e) {
                    throw new RuntimeException();
                }
            }
        }

        if (oldSeatRowNumber > seatRowNumber) {
            for (int i = seatRowNumber; i < seatRowList.size(); i++) {
                try {
                    removeSeatRow(seatRowList.get(i));
                    seatRowList.remove(seatRowList.get(i));
                } catch (RuntimeException e) {
                    System.out.println("-----reformCabin(int seatNumber, int seatRowNumber, Cabin cabin)-----");
                    throw new RuntimeException();
                }
            }
        }
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public void reformSeatRow(int seatNumber, SeatRow seatRow) throws RuntimeException {
        List<Seat> seatList = seatRepository.findSeatsBySeatRowId(seatRow.getId());
        if (seatList == null) {
            seatList = new ArrayList<>();
        }
        int oldSeatNumber = seatList.size();

        if (oldSeatNumber <= seatNumber) {
            List<Integer> newSeatList = differentSeatPerRowList(seatNumber, oldSeatNumber);
            for (int i = 0; i < newSeatList.size(); i++) {
                String name = seatRow.getName() + newSeatList.get(i);
                Seat seat = new Seat(name, seatRow);
                seatRepository.save(seat);
            }
        }

        if (oldSeatNumber > seatNumber) {
            for (int i = seatNumber; i < seatList.size(); i++) {
                try {
                    removeSeat(seatList.get(i));
                } catch (RuntimeException e) {
                    System.out.println("-----reformSeatRow(int seatNumber, SeatRow seatRow)-----");
                    throw new RuntimeException();
                }
            }
        }

    }

    public void removeSeat(Seat seat) throws RuntimeException {
        LocalDateTime thisMoment = thisMomentSession.getThisMoment();
        List<Ticket> ticketList = ticketRepository.findPurchasedTicketsBySeatIdAfterNow(seat.getId(), thisMoment);
        if (ticketList != null) {
            for (int i = 0; i < ticketList.size(); i++) {
                try {
                    ticketService.deleteTicket(ticketList.get(i));
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    throw new RuntimeException();
                }
            }
            seatRepository.deleteById(seat.getId());
        }
    }

    public void removeSeatRow(SeatRow seatRow) throws RuntimeException {
        List<Seat> seatList = seatRepository.findSeatsBySeatRowId(seatRow.getId());
        if (seatList != null) {
            for (int i = 0; i < seatList.size(); i++) {
                try {
                    removeSeat(seatList.get(i));
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    throw new RuntimeException();
                }
            }
        }
        seatRowRepository.delete(seatRow);
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public void removeCabin(Cabin cabin) throws RuntimeException {
        List<SeatRow> seatRowList = seatRowRepository.findSeatRowsByCabinId(cabin.getId());
        if (seatRowList != null) {
            for (int i = 0; i < seatRowList.size(); i++) {
                try {
                    removeSeatRow(seatRowList.get(i));
                } catch (RuntimeException e) {
                    throw new RuntimeException();
                }
            }
        }
        cabin.setAircraft(null);
        cabinRepository.delete(cabin);
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public void removeAircraft(Aircraft aircraft) throws RuntimeException {
        List<Cabin> cabinList = cabinRepository.findCabinsByAircraftId(aircraft.getId());
        if (cabinList != null) {
            for (int i = 0; i < cabinList.size(); i++) {
                try {
                    removeCabin(cabinList.get(i));
                } catch (RuntimeException e) {
                    throw new RuntimeException();
                }
            }
        }
        aircraftRepository.delete(aircraft);
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public void removeAirline(Airline airline) throws RuntimeException {
        List<Aircraft> aircraftList = aircraftRepository.findAircraftsByAirlineId(airline.getId());
        if (aircraftList != null) {
            for (int i = 0; i < aircraftList.size(); i++) {
                try {
                    removeAircraft(aircraftList.get(i));
                } catch (RuntimeException e) {
                    throw new RuntimeException();
                }
            }
        }
        airlineRepository.delete(airline);
    }

    public void deleteAircraft(long aircraftId) {
        Aircraft aircraft = aircraftRepository.findById(aircraftId).orElse(null);
        if (aircraft != null) {
            try {
                removeAircraft(aircraft);
            } catch (Exception e) {
            }
        }
    }

    public void deleteAirline(long airlineId) {
        Airline airline = airlineRepository.findById(airlineId).orElse(null);
        if (airline != null) {
            try {
                removeAirline(airline);
            } catch (Exception e) {
            }
        }
    }

    public List<Cabin> findCabinListByAircraftId(long aicraftId) {
        List<Cabin> cabinList = cabinRepository.findCabinsByAircraftId(aicraftId);
        return cabinList;
    }

    public List<SeatRow> findSeatRowListByCabinId(long cabinId) {
        List<SeatRow> seatRowList = seatRowRepository.findSeatRowsByCabinId(cabinId);
        return seatRowList;
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
}
