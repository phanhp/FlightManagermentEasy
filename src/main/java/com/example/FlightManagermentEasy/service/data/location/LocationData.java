package com.example.FlightManagermentEasy.service.data.location;

import com.example.FlightManagermentEasy.entity.flight.location.Airport;
import com.example.FlightManagermentEasy.entity.flight.location.City;
import com.example.FlightManagermentEasy.entity.flight.location.Route;
import com.example.FlightManagermentEasy.repository.flight.location.AirportRepository;
import com.example.FlightManagermentEasy.repository.flight.location.CityRepository;
import com.example.FlightManagermentEasy.repository.flight.location.CountryRepository;
import com.example.FlightManagermentEasy.repository.flight.location.RouteRepository;
import com.example.FlightManagermentEasy.service.MUF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
public class LocationData {
    @Autowired
    LocationDataFunction locationDataFunction;
    @Autowired
    CountryRepository countryRepository;
    @Autowired
    CityRepository cityRepository;
    @Autowired
    AirportRepository airportRepository;
    @Autowired
    RouteRepository routeRepository;

    public void createCountryData(){
        locationDataFunction.createCountry("My, VietNam");
    }

    public void createCityData(){
        locationDataFunction.createCity("Ohio, NewYork", "My");
        locationDataFunction.createCity("Hanoi, Hue, Danang, ThaiNguyen, HaiPhong, ThanhHoa, BinhDuong", "VietNam");
    }

    public void creatAirportData(){
        LocationData locationData = new LocationData();
        MUF muf = new MUF();
        Random random = new Random();
        List<String> randomList = muf.toArrayString("01, 02, 03");
        List<City> cityList = cityRepository.findAll();
        for (int i = 0; i < cityList.size(); i++) {
            int randomSize = random.nextInt(randomList.size())+1;
            for(int j=0; j<randomSize;j++){
                String airportName = cityList.get(i).getName() + "." + randomList.get(j);
                locationDataFunction.createAirport(airportName, cityList.get(i));
            }
        }
    }

    public void createRouteData(){
        List<Airport> airportList = airportRepository.findAll();
        Integer[] num = {12,15,18,21,24};
        List<Integer> numberList = Arrays.asList(num);
        for(int i=0; i<airportList.size();i++){
            Collections.shuffle(numberList);
            int numberOfRoute = numberList.get(0);
            for (int j=1; j<=numberOfRoute;j++){
                String routeName = airportList.get(i).getName()+"_Route_"+j;
                Route route = new Route(routeName, airportList.get(i));
                routeRepository.save(route);
            }
        }
    }
}
