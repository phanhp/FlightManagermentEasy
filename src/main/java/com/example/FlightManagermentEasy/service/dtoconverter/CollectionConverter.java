package com.example.FlightManagermentEasy.service.dtoconverter;

import com.example.FlightManagermentEasy.dto.flight.FlightDTO;
import com.example.FlightManagermentEasy.dto.flight.aircraft.AircraftDTO;
import com.example.FlightManagermentEasy.dto.flight.location.AirportDTO;
import com.example.FlightManagermentEasy.dto.flight.location.CityDTO;
import com.example.FlightManagermentEasy.dto.flight.location.CountryDTO;
import com.example.FlightManagermentEasy.dto.flight.location.RouteDTO;
import com.example.FlightManagermentEasy.dto.user.AccountDTO;
import com.example.FlightManagermentEasy.entity.flight.Flight;
import com.example.FlightManagermentEasy.entity.flight.aircraft.Aircraft;
import com.example.FlightManagermentEasy.entity.flight.location.Airport;
import com.example.FlightManagermentEasy.entity.flight.location.City;
import com.example.FlightManagermentEasy.entity.flight.location.Country;
import com.example.FlightManagermentEasy.entity.flight.location.Route;
import com.example.FlightManagermentEasy.entity.user.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CollectionConverter {
    @Autowired
    ObjectConverter objectConverter;

    //Flight
    //Flight
    public List<Flight> toFlightEntityList(List<FlightDTO> flightDTOList) {
        List<Flight> flightList = new ArrayList<>();
        if (flightDTOList == null | flightDTOList.isEmpty()) {
            return flightList;
        } else {
            for (int i = 0; i < flightDTOList.size(); i++) {
                Flight flight = objectConverter.toFlightEntity(flightDTOList.get(i));
                flightList.add(flight);
            }
            return flightList;
        }
    }

    public List<FlightDTO> toFlightDTOList(List<Flight> flightList) {
        List<FlightDTO> flightDTOList = new ArrayList<>();
        if (flightList == null | flightList.isEmpty()) {
            return flightDTOList;
        } else {
            for (int i = 0; i < flightList.size(); i++) {
                FlightDTO flightDTO = objectConverter.toFlightDTO(flightList.get(i));
                flightDTOList.add(flightDTO);
            }
            return flightDTOList;
        }
    }

    //Aircraft
    public List<Aircraft> toAircraftEntityList(List<AircraftDTO> aircraftDTOList) {
        List<Aircraft> aircraftList = new ArrayList<>();
        if (aircraftDTOList != null | !aircraftDTOList.isEmpty()) {
            for (int i = 0; i < aircraftDTOList.size(); i++) {
                Aircraft aircraft = objectConverter.toAircraftEntity(aircraftDTOList.get(i));
                aircraftList.add(aircraft);
            }
        }
        return aircraftList;
    }

    public List<AircraftDTO> toAircraftDTOList(List<Aircraft> aircraftList) {
        List<AircraftDTO> aircraftDTOList = new ArrayList<>();
        if (aircraftList != null | !aircraftList.isEmpty()) {
            for (int i = 0; i < aircraftList.size(); i++) {
                AircraftDTO aircraftDTO = objectConverter.toAircraftDTO(aircraftList.get(i));
                aircraftDTOList.add(aircraftDTO);
            }
        }
        return aircraftDTOList;
    }

    //Location
    //Country
    public List<Country> toCountryEntityList(List<CountryDTO> countryDTOList) {
        List<Country> countryList = new ArrayList<>();
        if (countryDTOList != null && !countryDTOList.isEmpty()) {
            for (int i = 0; i < countryDTOList.size(); i++) {
                Country country = objectConverter.toCountryEntity(countryDTOList.get(i));
                countryList.add(country);
            }
        }
        return countryList;
    }

    public List<CountryDTO> toCountryDTOList(List<Country> countryList) {
        List<CountryDTO> countryDTOList = new ArrayList<>();
        if (countryList != null && !countryList.isEmpty()) {
            for (int i = 0; i < countryList.size(); i++) {
                CountryDTO countryDTO = objectConverter.toCountryDTO(countryList.get(i));
                countryDTOList.add(countryDTO);
            }
        }
        return countryDTOList;
    }

    //City
    public List<City> toCityEntityList(List<CityDTO> cityDTOList) {
        List<City> cityList = new ArrayList<>();
        if (cityDTOList == null | cityDTOList.isEmpty()) {
            return cityList;
        } else {
            for (int i = 0; i < cityDTOList.size(); i++) {
                City city = objectConverter.toCityEntity(cityDTOList.get(i));
                cityList.add(city);
            }
            return cityList;
        }
    }

    public List<CityDTO> toCityDTOList(List<City> cityList) {
        List<CityDTO> cityDTOList = new ArrayList<>();
        if (cityList == null | cityList.isEmpty()) {
            return cityDTOList;
        } else {
            for (int i = 0; i < cityList.size(); i++) {
                CityDTO cityDTO = objectConverter.toCityDTO(cityList.get(i));
                cityDTOList.add(cityDTO);
            }
            return cityDTOList;
        }
    }

    //Aiport
    public List<Airport> toAirportEntityList(List<AirportDTO> airportDTOList) {
        List<Airport> airportList = new ArrayList<>();
        if (airportDTOList != null && !airportDTOList.isEmpty()) {
            for (int i = 0; i < airportDTOList.size(); i++) {
                Airport airport = objectConverter.toAirportEntity(airportDTOList.get(i));
                airportList.add(airport);
            }
        }
        return airportList;
    }

    public List<AirportDTO> toAirportDTOList(List<Airport> airportList) {
        List<AirportDTO> airportDTOList = new ArrayList<>();
        if (airportList != null && !airportList.isEmpty()) {
            for (int i = 0; i < airportList.size(); i++) {
                AirportDTO airportDTO = objectConverter.toAirportDTO(airportList.get(i));
                airportDTOList.add(airportDTO);
            }
        }
        return airportDTOList;
    }

    //Route
    public List<Route> toRouteEntityList(List<RouteDTO> routeDTOList) {
        List<Route> routeList = new ArrayList<>();
        if (routeDTOList != null && !routeDTOList.isEmpty()) {
            for (int i = 0; i < routeDTOList.size(); i++) {
                Route route = objectConverter.toRouteEntity(routeDTOList.get(i));
                routeList.add(route);
            }
        }
        return routeList;
    }

    public List<RouteDTO> toRouteDTOList(List<Route> routeList) {
        List<RouteDTO> routeDTOList = new ArrayList<>();
        if (routeList != null && !routeList.isEmpty()) {
            for (int i = 0; i < routeList.size(); i++) {
                RouteDTO routeDTO = objectConverter.toRouteDTO(routeList.get(i));
                routeDTOList.add(routeDTO);
            }
        }
        return routeDTOList;
    }

    //Person
    //Account
    public List<Account> toAccountEntityList(List<AccountDTO> accountDTOList) {
        List<Account> accountList = new ArrayList<>();
        if (accountDTOList != null && !accountDTOList.isEmpty()) {
            for (int i = 0; i < accountDTOList.size(); i++) {
                Account account = objectConverter.toAccountEntity(accountDTOList.get(i));
                accountList.add(account);
            }
        }
        return accountList;
    }

    public List<AccountDTO> toAccountDTOList(List<Account> accountList) {
        List<AccountDTO> accountDTOList = new ArrayList<>();
        if (accountList != null && !accountList.isEmpty()) {
            for (int i = 0; i < accountList.size(); i++) {
                AccountDTO accountDTO = objectConverter.toAccountDTO(accountList.get(i));
                accountDTOList.add(accountDTO);
            }
        }
        return accountDTOList;
    }
}
