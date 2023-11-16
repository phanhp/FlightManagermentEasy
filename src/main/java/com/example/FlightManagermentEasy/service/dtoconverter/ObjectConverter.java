package com.example.FlightManagermentEasy.service.dtoconverter;

import com.example.FlightManagermentEasy.dto.flight.FlightDTO;
import com.example.FlightManagermentEasy.dto.flight.aircraft.AircraftDTO;
import com.example.FlightManagermentEasy.dto.flight.location.AirportDTO;
import com.example.FlightManagermentEasy.dto.flight.location.CityDTO;
import com.example.FlightManagermentEasy.dto.flight.location.CountryDTO;
import com.example.FlightManagermentEasy.dto.flight.location.RouteDTO;
import com.example.FlightManagermentEasy.dto.user.AccountDTO;
import com.example.FlightManagermentEasy.dto.user.booking.PromotionTicketDTO;
import com.example.FlightManagermentEasy.entity.Ticket;
import com.example.FlightManagermentEasy.entity.flight.Flight;
import com.example.FlightManagermentEasy.entity.flight.aircraft.Aircraft;
import com.example.FlightManagermentEasy.entity.flight.aircraft.Airline;
import com.example.FlightManagermentEasy.entity.flight.aircraft.Seat;
import com.example.FlightManagermentEasy.entity.flight.location.Airport;
import com.example.FlightManagermentEasy.entity.flight.location.City;
import com.example.FlightManagermentEasy.entity.flight.location.Country;
import com.example.FlightManagermentEasy.entity.flight.location.Route;
import com.example.FlightManagermentEasy.entity.user.Account;
import com.example.FlightManagermentEasy.entity.user.booking.PromotionTicket;
import com.example.FlightManagermentEasy.repository.TicketRepository;
import com.example.FlightManagermentEasy.repository.flight.FlightRepository;
import com.example.FlightManagermentEasy.repository.flight.aircraft.AircraftRepository;
import com.example.FlightManagermentEasy.repository.flight.aircraft.AirlineRepository;
import com.example.FlightManagermentEasy.repository.flight.aircraft.CabinRepository;
import com.example.FlightManagermentEasy.repository.flight.aircraft.SeatRepository;
import com.example.FlightManagermentEasy.repository.flight.location.AirportRepository;
import com.example.FlightManagermentEasy.repository.flight.location.CityRepository;
import com.example.FlightManagermentEasy.repository.flight.location.CountryRepository;
import com.example.FlightManagermentEasy.repository.flight.location.RouteRepository;
import com.example.FlightManagermentEasy.repository.user.booking.PromotionTicketRepository;
import com.example.FlightManagermentEasy.repository.user.user.AccountRepository;
import com.example.FlightManagermentEasy.service.MUF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.Blob;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ObjectConverter {
    @Autowired
    MUF muf;
    @Autowired
    AircraftRepository aircraftRepository;
    @Autowired
    AirlineRepository airlineRepository;
    @Autowired
    CabinRepository cabinRepository;
    @Autowired
    SeatRepository seatRepository;
    @Autowired
    FlightRepository flightRepository;
    @Autowired
    TicketRepository ticketRepository;
    @Autowired
    CityRepository cityRepository;
    @Autowired
    CountryRepository countryRepository;
    @Autowired
    AirportRepository airportRepository;
    @Autowired
    RouteRepository routeRepository;
    @Autowired
    PromotionTicketRepository promotionTicketRepository;
    @Autowired
    AccountRepository accountRepository;

    //Flight
    //Flight
    public Flight toFlightEntity(FlightDTO flightDTO) {
//        private long id;
        Flight flight;
        if (flightDTO.getId() == 0) {
            flight = new Flight();
        } else {
            flight = flightRepository.findById(flightDTO.getId()).orElse(new Flight());
        }
//        private String pnr;
        flight.setPnr(flightDTO.getPnr());
//        private String departureTime;
        flight.setDepartureTime(muf.stringToLocalDateTime(flightDTO.getDepartureTime()));
//        private long departureRouteId;
        if (flightDTO.getDepartureRouteId() != 0) {
            Route route = routeRepository.findById(flightDTO.getDepartureRouteId()).orElse(null);
            flight.setDepartureRoute(route);
        }
//        private String arrivalTime;
        flight.setArrivalTime(muf.stringToLocalDateTime(flightDTO.getArrivalTime()));
//        private long arrivalRouteId;
        if (flightDTO.getArrivalRouteId() != 0) {
            Route route = routeRepository.findById(flightDTO.getArrivalRouteId()).orElse(null);
            flight.setArrivalRoute(route);
        }
//        private long aircraftId;
        if (flightDTO.getAircraftId() != 0) {
            Aircraft aircraft = aircraftRepository.findById(flightDTO.getAircraftId()).orElse(null);
            flight.setAircraft(aircraft);
        }

        return flight;
    }

    public FlightDTO toFlightDTO(Flight flight) {
//        private long id;
        FlightDTO flightDTO = new FlightDTO();
        flightDTO.setId(flight.getId());
//        private String pnr;
        flightDTO.setPnr(flight.getPnr());
//        private double ecnonomyPrice and businessPrice;
        List<Ticket> economyTicketList = ticketRepository.findEconomyTicketsByFlightId(flight.getId());
        if (economyTicketList != null) {
            if (!economyTicketList.isEmpty()) {
                double economyPrice = economyTicketList.get(0).getPrice();
                flightDTO.setEcnonomyPrice(economyPrice);
            }
        }
        List<Ticket> businessTicketList = ticketRepository.findBusinessTicketByFlightId(flight.getId());
        if (businessTicketList != null) {
            if (!businessTicketList.isEmpty()) {
                double businessPrice = businessTicketList.get(0).getPrice();
                flightDTO.setBusinessPrice(businessPrice);
            }
        }
//        private String departureTime;
        flightDTO.setDepartureTime(muf.localDateTimeToString(flight.getDepartureTime()));
//        private long departureRouteId; private String departureCity;
        try {
            Route dr = routeRepository.findDepartureRouteByFlightId(flight.getId());
            flightDTO.setDepartureRouteId(dr.getId());
            try {
                City city = cityRepository.findCityByRouteId(dr.getId());
                flightDTO.setDepartureCity(city.getName());
            } catch (Exception e) {
            }
        } catch (Exception e) {
        }
//        private String arrivalTime;
        flightDTO.setArrivalTime(muf.localDateTimeToString(flight.getArrivalTime()));
//        private long arrivalRouteId; String arrivalCity;
        try {
            Route ar = routeRepository.findArrivalRouteByFlightId(flight.getId());
            flightDTO.setArrivalRouteId(ar.getId());
            try {
                City city = cityRepository.findCityByRouteId(ar.getId());
                flightDTO.setArrivalCity(city.getName());
            } catch (Exception e) {
            }
        } catch (Exception e) {
        }
//        private long aircraftId;
        Aircraft aircraft;
        aircraft = aircraftRepository.findAircraftByFlightId(flight.getId());
        if (aircraft != null) {
            flightDTO.setAircraftId(aircraft.getId());
        }
        return flightDTO;
    }

    //Aircraft
    public Aircraft toAircraftEntity(AircraftDTO aircraftDTO) {
//        private long id;
        Aircraft aircraft;
        if (aircraftDTO.getId() == 0) {
            aircraft = new Aircraft();
        } else {
            try {
                aircraft = aircraftRepository.findById(aircraftDTO.getId()).get();
            } catch (Exception e) {
                aircraft = new Aircraft();
            }
        }
//        private String name;
        if (aircraftDTO.getName() != null) {
            aircraft.setName(aircraftDTO.getName());
        }
//        private String airlineName and long airlineId;
        try {
            Airline airline = airlineRepository.findById(aircraftDTO.getAirlineId()).get();
            aircraft.setAirline(airline);
        } catch (Exception e) {
        }
//        private int businessSeatsAmount;
//        private int economySeatsAmount;

        return aircraft;
    }

    public AircraftDTO toAircraftDTO(Aircraft aircraft) {
//        private long id;
        AircraftDTO aircraftDTO = new AircraftDTO();
        aircraftDTO.setId(aircraft.getId());
//        private String name;
        if (aircraft.getName() != null) {
            aircraftDTO.setName(aircraft.getName());
        }
//        private String airlineName and long airlineId;
        try {
            Airline airline = airlineRepository.findAirlineByAircraftId(aircraft.getId());
            aircraftDTO.setAirlineId(airline.getId());
            aircraftDTO.setAirlineName(airline.getName());
        } catch (Exception e) {
        }
//        private int businessSeatsAmount private int economySeatsAmount;
        try {
            List<Seat> businessSeatList = seatRepository.findBusinessSeatsByAircraftId(aircraft.getId());
            List<Seat> economySeatList = seatRepository.findEconomySeatsByAircraftId(aircraft.getId());
            aircraftDTO.setBusinessSeatsAmount(businessSeatList.size());
            aircraftDTO.setEconomySeatsAmount(economySeatList.size());
        } catch (Exception e) {
        }
        return aircraftDTO;
    }

    //Location
    //Country
    public Country toCountryEntity(CountryDTO countryDTO) {
//        private long id;
        Country country;
        if (countryDTO.getId() == 0) {
            country = new Country();
        } else {
            try {
                country = countryRepository.findById(countryDTO.getId()).get();
            } catch (Exception e) {
                country = new Country();
            }
        }
//        private String name;
        if (countryDTO.getName() != null) {
            country.setName(countryDTO.getName());
        }
        return country;
    }

    public CountryDTO toCountryDTO(Country country) {
//        private long id;
        CountryDTO countryDTO = new CountryDTO();
        countryDTO.setId(country.getId());
//        private String name;
        if (country.getName() != null) {
            countryDTO.setName(country.getName());
        }
        return countryDTO;
    }

    //City
    public City toCityEntity(CityDTO cityDTO) {
//        long id;
        City city;
        if (cityDTO.getId() == 0) {
            city = new City();
        } else {
            try {
                city = cityRepository.findById(cityDTO.getId()).get();
            } catch (Exception e) {
                city = new City();
            }
        }
//        String name;
        if (cityDTO.getName() != null) {
            city.setName(cityDTO.getName());
        }
//        long countryId;
        if (cityDTO.getCountryId() != 0) {
            try {
                Country country = countryRepository.findById(cityDTO.getCountryId()).get();
                city.setCountry(country);
            } catch (Exception e) {
            }
        }
        return city;
    }

    public CityDTO toCityDTO(City city) {
//        long id;
        CityDTO cityDTO = new CityDTO();
        cityDTO.setId(city.getId());
//        String name;
        if (city.getName() != null) {
            cityDTO.setName(city.getName());
        }
//        long countryId;
        try {
            Country country = countryRepository.findCountryByCityId(city.getId());
            cityDTO.setCountryId(country.getId());
        } catch (Exception e) {
        }
        return cityDTO;
    }

    //Airport
    public Airport toAirportEntity(AirportDTO airportDTO) {
//        private long id;
        Airport airport;
        if (airportDTO.getId() == 0) {
            airport = new Airport();
        } else {
            try {
                airport = airportRepository.findById(airportDTO.getId()).get();
            } catch (Exception e) {
                airport = new Airport();
            }
        }
//        private String name;
        if (airportDTO.getName() != null) {
            airport.setName(airportDTO.getName());
        }
//        private long cityId;
        if (airportDTO.getCityId() != 0) {
            try {
                City city = cityRepository.findById(airportDTO.getCityId()).get();
                airport.setCity(city);
            } catch (Exception e) {
            }
        }
        return airport;
    }

    public AirportDTO toAirportDTO(Airport airport) {
//        private long id;
        AirportDTO airportDTO = new AirportDTO();
        airportDTO.setId(airport.getId());
//        private String name;
        if (airport.getName() != null) {
            airportDTO.setName(airport.getName());
        }
//        private long cityId;
        try {
            City city = cityRepository.findCityByAirportId(airport.getId());
            airportDTO.setCityId(city.getId());
        } catch (Exception e) {
        }
        return airportDTO;
    }

    //Route
    public Route toRouteEntity(RouteDTO routeDTO) {
//        private long id;
        Route route;
        if (routeDTO.getId() == 0) {
            route = new Route();
        } else {
            try {
                route = routeRepository.findById(routeDTO.getId()).get();
            } catch (Exception e) {
                route = new Route();
            }
        }
//        private String name;
        if (routeDTO.getName() != null) {
            route.setName(routeDTO.getName());
        }
//        private long airportId;
        if (routeDTO.getAirportId() != 0) {
            try {
                Airport airport = airportRepository.findById(routeDTO.getAirportId()).get();
                route.setAirport(airport);
            } catch (Exception e) {
            }
        }
        return route;
    }

    public RouteDTO toRouteDTO(Route route) {
//        private long id;
        RouteDTO routeDTO = new RouteDTO();
        routeDTO.setId(route.getId());
//        private String name;
        if (route.getName() != null) {
            routeDTO.setName(route.getName());
        }
//        private long airportId;
        try {
            Airport airport = airportRepository.findAirportByRouteId(route.getId());
            routeDTO.setAirportId(airport.getId());
        } catch (Exception e) {
        }
        return routeDTO;
    }

    //Booking
    //PromotionTicket
    public PromotionTicket toPromotionTicketEntity(PromotionTicketDTO promotionTicketDTO) {
//        private long id;
        PromotionTicket promotionTicket;
        if (promotionTicketDTO.getId() == 0) {
            promotionTicket = new PromotionTicket();
        } else {
            try {
                promotionTicket = promotionTicketRepository.findById(promotionTicketDTO.getId()).get();
            } catch (Exception e) {
                promotionTicket = new PromotionTicket();
            }
        }
//        private String code;
        if (promotionTicketDTO.getCode() != null && !promotionTicketDTO.getCode().isEmpty()) {
            promotionTicket.setCode(promotionTicketDTO.getCode());
        } else {
            try {
                List<PromotionTicket> promotionTicketList = promotionTicketRepository.findAll();
                if (!promotionTicketList.isEmpty()) {
                    Set<String> codeSet = promotionTicketList.stream().map(PromotionTicket::getCode).collect(Collectors.toSet());
                    String code = muf.generateRandomKeyInSet(codeSet, 10);
                    promotionTicket.setCode(code);
                } else {
                    Set<String> codeSet = new HashSet<>();
                    String code = muf.generateRandomKeyInSet(codeSet, 10);
                    promotionTicket.setCode(code);
                }
            } catch (Exception e) {
                Set<String> codeSet = new HashSet<>();
                String code = muf.generateRandomKeyInSet(codeSet, 10);
                promotionTicket.setCode(code);
            }
        }
//        private double value;
        promotionTicket.setValue(promotionTicketDTO.getValue());
//        private String available;
        if (promotionTicketDTO.getAvailable().equalsIgnoreCase("True")) {
            promotionTicket.setAvailable(true);
        } else {
            promotionTicket.setAvailable(false);
        }
        return promotionTicket;
    }

    public PromotionTicketDTO toPromotionTicketDTO(PromotionTicket promotionTicket) {
//        private long id;
        PromotionTicketDTO promotionTicketDTO = new PromotionTicketDTO();
        promotionTicketDTO.setId(promotionTicket.getId());
//        private String code;
        if (promotionTicket.getCode() != null && !promotionTicket.getCode().isEmpty()) {
            promotionTicketDTO.setCode(promotionTicket.getCode());
        } else {
            try {
                List<PromotionTicket> promotionTicketList = promotionTicketRepository.findAll();
                if (!promotionTicketList.isEmpty()) {
                    Set<String> codeSet = promotionTicketList.stream().map(PromotionTicket::getCode).collect(Collectors.toSet());
                    String code = muf.generateRandomKeyInSet(codeSet, 10);
                    promotionTicketDTO.setCode(code);
                } else {
                    Set<String> codeSet = new HashSet<>();
                    String code = muf.generateRandomKeyInSet(codeSet, 10);
                    promotionTicketDTO.setCode(code);
                }
            } catch (Exception e) {
                Set<String> codeSet = new HashSet<>();
                String code = muf.generateRandomKeyInSet(codeSet, 10);
                promotionTicketDTO.setCode(code);
            }
        }
//        private double value;
        promotionTicketDTO.setValue(promotionTicket.getValue());
//        private String available;
        if (promotionTicket.isAvailable()) {
            promotionTicketDTO.setAvailable("True");
        } else {
            promotionTicketDTO.setAvailable("False");
        }
        return promotionTicketDTO;
    }

    //Person
    //Account
    public Account toAccountEntity(AccountDTO accountDTO) {
//        private long id;
        Account account;
        if (accountDTO.getId() == 0) {
            account = new Account();
        } else {
            account = accountRepository.findById(accountDTO.getId()).orElse(new Account());
        }
//        private String name;
        account.setName(accountDTO.getName());
//        private String dob;
        account.setDob(muf.stringToLocalDate(accountDTO.getDob()));
//        private String address;
        account.setAddress(accountDTO.getAddress());
//        private String identity;
        account.setIdentity(accountDTO.getIdentity());
//        private String gender;
        account.setGender(accountDTO.getGender());
//        private String accountName;
        account.setAccountName(accountDTO.getAccountName());
//        private String password;
        account.setPassword(accountDTO.getPassword());
//        private String email;
        account.setEmail(accountDTO.getEmail());
//        private String phone;
        account.setPhone(accountDTO.getPhone());
//        private Blob image;
        if (accountDTO.getImage() != null) {
            try {
                byte[] imgData = Base64.getDecoder().decode(accountDTO.getImage());
                Blob image = new SerialBlob(imgData);
                account.setImage(image);
            } catch (Exception e) {
            }
        }
        return account;
    }

    public AccountDTO toAccountDTO(Account account) {
//      private long id;
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setId(account.getId());
//      private String name;
        accountDTO.setName(account.getName());
//      private String dob;
        accountDTO.setDob(muf.localDateToString(account.getDob()));
//      private String address;
        accountDTO.setAddress(account.getAddress());
//      private String identity;
        accountDTO.setIdentity(account.getIdentity());
//      private String gender;
        accountDTO.setGender(account.getGender());
//      private String accountName;
        accountDTO.setAccountName(account.getAccountName());
//      private String password;
        accountDTO.setPassword(account.getPassword());
//      private String email;
        accountDTO.setEmail(account.getEmail());
//      private String phone;
        accountDTO.setPhone(account.getPhone());
//      private Blob image;
        if (account.getImage() != null) {
            try {
                byte[] imgData = account.getImage().getBytes(1L, (int) account.getImage().length());
                String base64String = Base64.getEncoder().encodeToString(imgData);
                accountDTO.setImage(base64String);
            } catch (Exception e) {
            }
        }
        return accountDTO;
    }
}