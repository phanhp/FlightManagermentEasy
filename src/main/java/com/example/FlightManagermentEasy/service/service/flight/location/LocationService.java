package com.example.FlightManagermentEasy.service.service.flight.location;

import com.example.FlightManagermentEasy.dto.flight.location.AirportDTO;
import com.example.FlightManagermentEasy.dto.flight.location.CityDTO;
import com.example.FlightManagermentEasy.dto.flight.location.CountryDTO;
import com.example.FlightManagermentEasy.dto.flight.location.RouteDTO;
import com.example.FlightManagermentEasy.entity.flight.location.Airport;
import com.example.FlightManagermentEasy.entity.flight.location.City;
import com.example.FlightManagermentEasy.entity.flight.location.Country;
import com.example.FlightManagermentEasy.entity.flight.location.Route;
import com.example.FlightManagermentEasy.repository.flight.location.AirportRepository;
import com.example.FlightManagermentEasy.repository.flight.location.CityRepository;
import com.example.FlightManagermentEasy.repository.flight.location.CountryRepository;
import com.example.FlightManagermentEasy.repository.flight.location.RouteRepository;
import com.example.FlightManagermentEasy.service.MUF;
import com.example.FlightManagermentEasy.service.dtoconverter.CollectionConverter;
import com.example.FlightManagermentEasy.service.dtoconverter.ObjectConverter;
import com.example.FlightManagermentEasy.service.session.ThisMomentSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LocationService {
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
    ThisMomentSession thisMomentSession;
    @Autowired
    MUF muf;

    //Find by countryId
    public List<CountryDTO> findAllCountryDTO() {
        return collectionConverter.toCountryDTOList(countryRepository.findAll());
    }

    public List<CityDTO> findCityListByCountryId(long countryId) {
        List<City> cityList;
        if (countryId == 0) {
            cityList = cityRepository.findAll();
        } else {
            cityList = cityRepository.findCitiesByCountryId(countryId);
        }
        if (cityList == null) {
            cityList = new ArrayList<>();
        }
        List<CityDTO> cityDTOList = collectionConverter.toCityDTOList(cityList);
        return cityDTOList;
    }

    public List<AirportDTO> findAirportListByCountryId(long countryId) {
        List<Airport> airportList;
        if (countryId == 0) {
            airportList = airportRepository.findAll();
        } else {
            airportList = airportRepository.findAirportsByCityCountryId(countryId);
        }
        if (airportList == null) {
            airportList = new ArrayList<>();
        }
        List<AirportDTO> airportDTOList = collectionConverter.toAirportDTOList(airportList);
        return airportDTOList;
    }

    public List<RouteDTO> findRouteListByCountryId(long countryId) {
        List<Route> routeList;
        if (countryId == 0) {
            routeList = routeRepository.findAll();
        } else {
            routeList = routeRepository.findRoutesByAirportCityCountryId(countryId);
        }
        if (routeList == null) {
            routeList = new ArrayList<>();
        }
        List<RouteDTO> routeDTOList = collectionConverter.toRouteDTOList(routeList);
        return routeDTOList;
    }

    public List<RouteDTO> findAppropriateRouteListByCountryId(long countryId, String inputTime) {
        List<Route> routeList;
        List<RouteDTO> routeDTOList;
        if (countryId == 0) {
            routeDTOList = findAllAppropriateRoute(inputTime);
            return routeDTOList;
        }
        LocalDateTime thisMoment = thisMomentSession.getThisMoment();
        LocalDateTime begin;
        if (inputTime == null) {
            inputTime = "";
        }
        if (inputTime.isEmpty()) {
            routeDTOList = findRouteListByCountryId(countryId);
            return routeDTOList;
        } else {
            begin = muf.stringToLocalDateTime(inputTime).minusHours(1);
            LocalDateTime end = begin.plusHours(2);

            routeList = routeRepository.findAppropriateRoutesByCountryId(countryId, begin, end, thisMoment);
            routeDTOList = collectionConverter.toRouteDTOList(routeList);
            return routeDTOList;
        }
    }

    //By cityId
    public List<CityDTO> findAllCityDTO() {
        return collectionConverter.toCityDTOList(cityRepository.findAll());
    }

    public List<CityDTO> findAllCityDTONotHaveId(long cityId) {
        return collectionConverter.toCityDTOList(cityRepository.findCitiesNotHaveId(cityId));
    }

    public CountryDTO findCountryByCityId(long cityId) {
        Country country;
        if (cityId == 0) {
            country = new Country();
        } else {
            country = countryRepository.findCountryByCityId(cityId);
        }
        if (country == null) {
            country = new Country();
        }
        CountryDTO countryDTO = objectConverter.toCountryDTO(country);
        return countryDTO;
    }

    public List<AirportDTO> findAirportListByCityId(long cityId) {
        List<Airport> airportList;
        if (cityId == 0) {
            airportList = airportRepository.findAll();
        } else {
            airportList = airportRepository.findAirportsByCityId(cityId);
        }
        if (airportList == null) {
            airportList = new ArrayList<>();
        }
        List<AirportDTO> airportDTOList = collectionConverter.toAirportDTOList(airportList);
        return airportDTOList;
    }

    public List<RouteDTO> findRouteListByCityId(long cityId) {
        List<Route> routeList;
        if (cityId == 0) {
            routeList = routeRepository.findAll();
        } else {
            routeList = routeRepository.findRoutesByAirportCityId(cityId);
        }
        if (routeList == null) {
            routeList = new ArrayList<>();
        }
        List<RouteDTO> routeDTOList = collectionConverter.toRouteDTOList(routeList);
        return routeDTOList;
    }

    public List<RouteDTO> findAppropriateRouteListByCityId(long cityId, String inputTime) {
        List<Route> routeList;
        List<RouteDTO> routeDTOList;
        if (cityId == 0) {
            routeDTOList = findAllAppropriateRoute(inputTime);
            return routeDTOList;
        }
        LocalDateTime thisMoment = thisMomentSession.getThisMoment();
        LocalDateTime begin;
        if (inputTime == null) {
            inputTime = "";
        }
        if (inputTime.isEmpty()) {
            routeDTOList = findRouteListByCityId(cityId);
            return routeDTOList;
        } else {
            begin = muf.stringToLocalDateTime(inputTime).minusHours(1);
            LocalDateTime end = begin.plusHours(2);

            routeList = routeRepository.findAppropriateRouteByCityId(cityId, begin, end, thisMoment);
            routeDTOList = collectionConverter.toRouteDTOList(routeList);
            return routeDTOList;
        }
    }

    //By airportId
    public List<AirportDTO> findAllAirportDTO() {
        return collectionConverter.toAirportDTOList(airportRepository.findAll());
    }

    public CountryDTO findCountryByAirportId(long airportId) {
        Country country;
        if (airportId == 0) {
            country = new Country();
        } else {
            country = countryRepository.findCountryByAirportId(airportId);
        }
        if (country == null) {
            country = new Country();
        }
        CountryDTO countryDTO = objectConverter.toCountryDTO(country);
        return countryDTO;
    }

    public CityDTO findCityByAirportId(long airportId) {
        City city;
        if (airportId == 0) {
            city = new City();
        } else {
            city = cityRepository.findCityByAirportId(airportId);
        }
        if (city == null) {
            city = new City();
        }
        CityDTO cityDTO = objectConverter.toCityDTO(city);
        return cityDTO;
    }

    public List<RouteDTO> findRouteListByAirportId(long airportId) {
        List<Route> routeList;
        if (airportId == 0) {
            routeList = routeRepository.findAll();
        } else {
            routeList = routeRepository.findRoutesByAirportId(airportId);
        }
        if (routeList == null) {
            routeList = new ArrayList<>();
        }
        List<RouteDTO> routeDTOList = collectionConverter.toRouteDTOList(routeList);
        return routeDTOList;
    }

    public List<RouteDTO> findAppropriateRouteListByAirportId(long airportId, String inputTime) {
        List<Route> routeList;
        List<RouteDTO> routeDTOList;
        if (airportId == 0) {
            routeDTOList = findAllAppropriateRoute(inputTime);
            return routeDTOList;
        }
        LocalDateTime thisMoment = thisMomentSession.getThisMoment();
        LocalDateTime begin;
        if (inputTime == null) {
            inputTime = "";
        }
        if (inputTime.isEmpty()) {
            routeDTOList = findRouteListByAirportId(airportId);
            return routeDTOList;
        } else {
            begin = muf.stringToLocalDateTime(inputTime).minusHours(1);
            LocalDateTime end = begin.plusHours(2);

            routeList = routeRepository.findAppropriateRoutesByAirportId(airportId, begin, end, thisMoment);
            routeDTOList = collectionConverter.toRouteDTOList(routeList);
            return routeDTOList;
        }
    }

    //By routeId
    public CountryDTO findCountryByRouteId(long routeId) {
        Country country;
        if (routeId == 0) {
            country = new Country();
        } else {
            country = countryRepository.findCountryByRouteId(routeId);
        }
        if (country == null) {
            country = new Country();
        }
        CountryDTO countryDTO = objectConverter.toCountryDTO(country);
        return countryDTO;
    }

    public CityDTO findCityByRouteId(long routeId) {
        City city;
        if (routeId == 0) {
            city = new City();
        } else {
            city = cityRepository.findCityByRouteId(routeId);
        }
        if (city == null) {
            city = new City();
        }
        CityDTO cityDTO = objectConverter.toCityDTO(city);
        return cityDTO;
    }


    public AirportDTO findAirportByRouteId(long routeId) {
        Airport airport;
        if (routeId == 0) {
            airport = new Airport();
        } else {
            airport = airportRepository.findAirportByRouteId(routeId);
        }
        if (airport == null) {
            airport = new Airport();
        }
        AirportDTO airportDTO = objectConverter.toAirportDTO(airport);
        return airportDTO;
    }

    public List<RouteDTO> findAllAppropriateRoute(String inputTime) {
        LocalDateTime thisMoment = thisMomentSession.getThisMoment();
        LocalDateTime begin;

        if (inputTime == null) {
            inputTime = "";
        }
        if (inputTime.isEmpty()) {
            List<Route> routeList = routeRepository.findAll();
            List<RouteDTO> routeDTOList = collectionConverter.toRouteDTOList(routeList);
            return routeDTOList;
        } else {
            begin = muf.stringToLocalDateTime(inputTime).minusHours(1);
            LocalDateTime end = begin.plusHours(2);

            List<Route> routeList = routeRepository.findAllAppropriateRoutes(begin, end, thisMoment);
            if (routeList == null) {
                routeList = new ArrayList<>();
            }
            List<RouteDTO> routeDTOList = collectionConverter.toRouteDTOList(routeList);
            return routeDTOList;
        }
    }

    public List<RouteDTO> findAllAppropriateRoutesByOptional(long countryId, long cityId, long airportId, String inputTime) {
        if (airportId != 0) {
            return findAppropriateRouteListByAirportId(airportId, inputTime);
        }
        if (cityId != 0) {
            return findAppropriateRouteListByCityId(cityId, inputTime);
        }
        if (countryId != 0) {
            return findAppropriateRouteListByCountryId(countryId, inputTime);
        }
        return findAllAppropriateRoute(inputTime);
    }
}
