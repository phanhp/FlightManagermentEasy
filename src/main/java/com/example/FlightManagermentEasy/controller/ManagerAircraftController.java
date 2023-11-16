package com.example.FlightManagermentEasy.controller;

import com.example.FlightManagermentEasy.dto.flight.aircraft.AircraftDTO;
import com.example.FlightManagermentEasy.repository.flight.aircraft.AircraftRepository;
import com.example.FlightManagermentEasy.repository.flight.aircraft.CabinRepository;
import com.example.FlightManagermentEasy.repository.flight.aircraft.SeatRepository;
import com.example.FlightManagermentEasy.repository.flight.aircraft.SeatRowRepository;
import com.example.FlightManagermentEasy.service.CommonModel;
import com.example.FlightManagermentEasy.service.dtoconverter.CollectionConverter;
import com.example.FlightManagermentEasy.service.dtoconverter.ObjectConverter;
import com.example.FlightManagermentEasy.service.service.flight.aircraft.AircraftService;
import com.example.FlightManagermentEasy.service.session.LoginSession;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class ManagerAircraftController {
    @Autowired
    ObjectConverter objectConverter;
    @Autowired
    CollectionConverter collectionConverter;
    @Autowired
    AircraftRepository aircraftRepository;
    @Autowired
    AircraftService aircraftService;
    @Autowired
    CabinRepository cabinRepository;
    @Autowired
    SeatRowRepository seatRowRepository;
    @Autowired
    SeatRepository seatRepository;
    @Autowired
    LoginSession loginSession;
    @Autowired
    CommonModel commonModel;

    @GetMapping("/manager/view-aircraft-page")
    public String adminViewAircraftPage(Model model,
                                        @RequestParam("page") Optional<Integer> page,
                                        HttpSession session) {
        Pageable pageable = PageRequest.of(page.orElse(0), 8);
        Page<AircraftDTO> aircraftDTOPage = aircraftService.viewAllAircraftPage(pageable);
        model.addAttribute("aircraftList", aircraftDTOPage);

        int currentPage = page.orElse(0);
        int totalPage = aircraftDTOPage.getTotalPages();
        int nextPage;
        int previousPage;
        if (currentPage == totalPage - 1) {
            nextPage = totalPage - 1;
        } else {
            nextPage = currentPage + 1;
        }
        if (currentPage == 0) {
            previousPage = 0;
        } else {
            previousPage = currentPage - 1;
        }

        String deleteAircraftMessage = (String) session.getAttribute("deleteAircraftMessage");
        model.addAttribute("deleteAircraftMessage", deleteAircraftMessage);

        model.addAttribute("totalPage", totalPage);
        model.addAttribute("nextPage", nextPage);
        model.addAttribute("previousPage", previousPage);

        commonModel.headerModel(model);
        return "managerViewAircraft";
    }

    @GetMapping("/manager/create-aircraft-page")
    public String createAircraftPage(Model model, HttpSession session){

        return "managerCreateAircraft";
    }

    @GetMapping("/manager/delete-aircraft/{aircraftId}")
    public String adminDeleteAircraft(@PathVariable long aircraftId,
                                      HttpSession session) {
        String result = aircraftService.deleteAircraftResult(aircraftId);
        session.setAttribute("deleteAircraftMessage", result);
        return "redirect:/manager/view-aircraft-page";
    }


}
