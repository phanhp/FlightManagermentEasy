package com.example.FlightManagermentEasy.service;

import com.example.FlightManagermentEasy.dto.flight.FlightDTO;
import com.example.FlightManagermentEasy.dto.flight.location.AirportDTO;
import com.example.FlightManagermentEasy.dto.flight.location.CityDTO;
import com.example.FlightManagermentEasy.dto.flight.location.CountryDTO;
import com.example.FlightManagermentEasy.dto.flight.location.RouteDTO;
import com.example.FlightManagermentEasy.dto.user.AccountDTO;
import com.example.FlightManagermentEasy.entity.Ticket;
import com.example.FlightManagermentEasy.entity.flight.Flight;
import com.example.FlightManagermentEasy.entity.flight.location.Airport;
import com.example.FlightManagermentEasy.entity.flight.location.City;
import com.example.FlightManagermentEasy.entity.flight.location.Country;
import com.example.FlightManagermentEasy.entity.flight.location.Route;
import com.example.FlightManagermentEasy.entity.user.Account;
import com.example.FlightManagermentEasy.repository.TicketRepository;
import com.example.FlightManagermentEasy.repository.flight.location.AirportRepository;
import com.example.FlightManagermentEasy.repository.flight.location.CityRepository;
import com.example.FlightManagermentEasy.repository.flight.location.CountryRepository;
import com.example.FlightManagermentEasy.repository.flight.location.RouteRepository;
import com.example.FlightManagermentEasy.repository.user.bank.BankAccountRepository;
import com.example.FlightManagermentEasy.repository.user.booking.PromotionTicketRepository;
import com.example.FlightManagermentEasy.service.dtoconverter.CollectionConverter;
import com.example.FlightManagermentEasy.service.dtoconverter.ObjectConverter;
import com.example.FlightManagermentEasy.service.service.EmailRequest;
import com.example.FlightManagermentEasy.service.service.EmailService;
import com.example.FlightManagermentEasy.service.service.TicketService;
import com.example.FlightManagermentEasy.service.service.entityStatus.TicketStatus;
import com.example.FlightManagermentEasy.service.service.flight.FlightService;
import com.example.FlightManagermentEasy.service.service.flight.aircraft.AircraftService;
import com.example.FlightManagermentEasy.service.session.BookingSession;
import com.example.FlightManagermentEasy.service.session.LoginSession;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class CommonModel {
    @Autowired
    LoginSession loginSession;
    @Autowired
    EmailService emailService;
    @Autowired
    BookingSession bookingSession;
    @Autowired
    ObjectConverter objectConverter;
    @Autowired
    CollectionConverter collectionConverter;
    @Autowired
    TicketRepository ticketRepository;
    @Autowired
    TicketService ticketService;
    @Autowired
    FlightService flightService;
    @Autowired
    AircraftService aircraftService;
    @Autowired
    PromotionTicketRepository promotionTicketRepository;
    @Autowired
    BankAccountRepository bankAccountRepository;
    @Autowired
    TicketStatus ticketStatus;
    @Autowired
    CountryRepository countryRepository;
    @Autowired
    CityRepository cityRepository;
    @Autowired
    AirportRepository airportRepository;
    @Autowired
    RouteRepository routeRepository;
    @Autowired
    MUF muf;

    //Most Use Model
    public void setPageUrlSession(HttpSession session, String content, String url) {
        String redirectContent = content + "Redirect";
        String redirectUrl = "redirect:" + url;
        session.setAttribute(content, url);
        session.setAttribute(redirectContent, redirectUrl);
    }

    public String getPageUrlSession(HttpSession session, Model model, String content, String alternativeUrl) {
        String url = (String) session.getAttribute(content);
        if (url == null) {
            url = alternativeUrl;
        }
        if (url != null) {
            if (url.isEmpty()) {
                url = alternativeUrl;
            }
        }
        model.addAttribute(content, url);
        return url;
    }

    public String getRedirectPageUrlSession(HttpSession session, Model model, String content, String alternativeUrl) {
        String redirectContent = content + "Redirect";
        String redirectUrl = (String) session.getAttribute(redirectContent);
        if (redirectUrl == null) {
            redirectUrl = "redirect:" + alternativeUrl;
        }
        if (redirectUrl != null) {
            if (redirectUrl.isEmpty()) {
                redirectUrl = "redirect:" + alternativeUrl;
            }
        }
        model.addAttribute(redirectContent, redirectUrl);
        return redirectUrl;
    }

    public void genderListModel(Model model) {
        List<String> genderList = muf.toArrayString("Mystery, Male, Female");
        model.addAttribute("genderList", genderList);
    }

    public void countryModel(Model model) {
        List<Country> countryList = countryRepository.findAll();
        List<CountryDTO> countryDTOList = collectionConverter.toCountryDTOList(countryList);
        model.addAttribute("countryList", countryDTOList);
    }

    public void cityModel(Model model) {
        List<City> cityList = cityRepository.findAll();
        List<CityDTO> cityDTOList = collectionConverter.toCityDTOList(cityList);
        model.addAttribute("cityList", cityDTOList);
    }

    public void airportModel(Model model) {
        List<Airport> airportList = airportRepository.findAll();
        List<AirportDTO> airportDTOList = collectionConverter.toAirportDTOList(airportList);
        model.addAttribute("airportList", airportDTOList);
    }

    public void routeModel(Model model) {
        List<Route> routeList = routeRepository.findAll();
        List<RouteDTO> routeDTOList = collectionConverter.toRouteDTOList(routeList);
        model.addAttribute("routeList", routeDTOList);
    }

    public void locationModel(Model model) {
        countryModel(model);
        cityModel(model);
        airportModel(model);
        routeModel(model);
    }

    public void pageItemModel(Model model, HttpSession session, Optional<Integer> page,
                              String pageItemTitle, Page<?> pageItem,
                              String pageUrlTitle, String basePageUrl,
                              String search) {
        int totalPage = pageItem.getTotalPages();
        int currentPage = page.orElse(0);
        int firstPage = 0;
        int lastPage = totalPage - 1;
        int previousPage = 0;
        int nextPage = lastPage;
        if (currentPage < lastPage) {
            nextPage = currentPage + 1;
        }
        if (currentPage > 0) {
            previousPage = currentPage - 1;
        }

        String pageUrl = basePageUrl;
        if (search.isEmpty()) {
            pageUrl = pageUrl + "?page=";
        } else {
            pageUrl = pageUrl + "/search?" + search + "&page=";
        }

        List<Integer> allPageList = muf.createIntegerListFromNumberToNumber(1, totalPage);

        model.addAttribute("firstPage", firstPage);
        model.addAttribute("previousPage", previousPage);
        model.addAttribute("nextPage", nextPage);
        model.addAttribute("lastPage", lastPage);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute(pageItemTitle, pageItem);
        model.addAttribute(pageUrlTitle, pageUrl);
        model.addAttribute("allPageList", allPageList);

        setPageUrlSession(session, pageUrlTitle, pageUrl + currentPage);
        setPageUrlSession(session, "currentUrl", pageUrl + currentPage);
    }

    public void setImageSession(HttpSession session, String sessionName, String image) {
        if (image != null) {
            if (!image.isEmpty()) {
                String onError = sessionName + "Error";
                try {
                    session.setAttribute(sessionName, image);
                } catch (Exception e) {
                    session.setAttribute(onError, "Image Could Not Load");
                }
            }
        }
    }

    public void getImageSession(Model model, HttpSession session, String sessionName) {
        String imgBase64 = (String) session.getAttribute(sessionName);
        if (imgBase64 != null) {
            if (imgBase64.isEmpty()) {
                imgBase64 = null;
            }
        }
        model.addAttribute(sessionName, imgBase64);

        String onError = sessionName + "Error";
        String loadError = (String) session.getAttribute(onError);
        model.addAttribute(onError, loadError);
        session.setAttribute(onError, "");
    }

    //Header Model
    public void headerModel(Model model) {
        bookingSession.reloadBookingSession();
        model.addAttribute("loginSession", loginSession);
        model.addAttribute("bookingSession", bookingSession);
        try {
            String accountImg = loginSession.getProfileImage();
            model.addAttribute("accountImg", accountImg);
        } catch (Exception e) {
            String accountImgError = "Can Not Load Account Image";
            model.addAttribute("accountImgError", accountImgError);
        }
    }

    //SignUp Model
    public void signupPageModel(HttpSession session,
                                Model model,
                                String signupAction,
                                String title,
                                String signUpPageUrl,
                                String submit) {
        model.addAttribute("signupAction", signupAction);
        model.addAttribute("title", title);
        model.addAttribute("signUpPageUrl", signUpPageUrl);
        model.addAttribute("submit", submit);

        getImageSession(model, session, "profileImage");

        //Get Temporal Save Account and repassword
        AccountDTO temporal = (AccountDTO) session.getAttribute("temporalAccount");
        if (temporal == null) {
            temporal = new AccountDTO();
        }
        model.addAttribute("temporal", temporal);
        session.setAttribute("temporalAccount", null);

        String repassword = (String) session.getAttribute("repassword");
        if (repassword == null) {
            repassword = "";
        }
        model.addAttribute("repassword", repassword);
        session.setAttribute("repassword", "");

        String accountNameError = (String) session.getAttribute("accountNameError");
        if (accountNameError == null) {
            accountNameError = "";
        }
        model.addAttribute("accountNameError", accountNameError);
        session.setAttribute("accountNameError", "");

        String passwordError = (String) session.getAttribute("passwordError");
        if (passwordError == null) {
            passwordError = "";
        }
        model.addAttribute("passwordError", passwordError);
        session.setAttribute("passwordError", "");

        String nameError = (String) session.getAttribute("nameError");
        if (nameError == null) {
            nameError = "";
        }
        model.addAttribute("nameError", nameError);
        session.setAttribute("nameError", "");

        String identityError = (String) session.getAttribute("identityError");
        if (identityError == null) {
            identityError = "";
        }
        model.addAttribute("identityError", identityError);
        session.setAttribute("identityError", "");

        String emailError = (String) session.getAttribute("emailError");
        if (emailError == null) {
            emailError = "";
        }
        model.addAttribute("emailError", emailError);
        session.setAttribute("emailError", "");

        String CUAccountError = (String) session.getAttribute("CUAccountError");
        model.addAttribute("CUAccountError", CUAccountError);
        session.setAttribute("CUAccountError", "");

        String signupConfirmError = (String) session.getAttribute("signupConfirmError");
        model.addAttribute("signupConfirmError", signupConfirmError);
        session.setAttribute("signupConfirmError", "");

        genderListModel(model);
        headerModel(model);
    }

    public boolean isSignupProcessSuccess(HttpSession session,
                                          String username, String password, String repassword,
                                          String name, String email, String identity,
                                          String phone, String address, String dob,
                                          String gender, MultipartFile profileImage) {
        AccountDTO temporal = new AccountDTO();
        int count = 0;
        temporal.setAccountName(username);
        temporal.setPassword(password);
        temporal.setName(name);
        temporal.setEmail(email);
        temporal.setIdentity(identity);
        temporal.setPhone(phone);
        temporal.setAddress(address);
        temporal.setDob(dob);
        temporal.setGender(gender);
        if (muf.isImage(profileImage)) {
            try {
                byte[] imgData = profileImage.getBytes();
                String base64 = Base64.getEncoder().encodeToString(imgData);
                temporal.setImage(base64);
                session.setAttribute("profileImage", base64);
            } catch (Exception e) {
                session.setAttribute("profileImageError", "File Could Not Load");
            }
        }
        session.setAttribute("temporalAccount", temporal);
        session.setAttribute("repassword", repassword);
        if (username == null) {
            System.out.println("Account Name Must Be Filled");
            session.setAttribute("accountNameError", "Account Name Must Be Filled");
            count++;
        }
        if (username.isEmpty()) {
            session.setAttribute("accountNameError", "Account Name Must Be Filled");
            count++;
        }
        if (password.length() < 3) {
            session.setAttribute("passwordError", "Password Must Be Longer Or Equal Than 3 Character");
            count++;
        }
        if (password.compareTo(repassword) != 0) {
            session.setAttribute("passwordError", "Password And Confirm-password Must Be Matched");
            count++;
        }
        if (name == null) {
            session.setAttribute("nameError", "Name Must Be Filled");
            count++;
        }
        if (name.isEmpty()) {
            session.setAttribute("nameError", "Name Must Be Filled");
            count++;
        }
        if (identity == null) {
            session.setAttribute("identityError", "Identity Must Be Filled");
            count++;
        }
        if (identity.isEmpty()) {
            session.setAttribute("identityError", "Identity Must Be Filled");
            count++;
        }
        if (email == null) {
            session.setAttribute("emailError", "Email Must Be Filled");
            count++;
        }
        if (email.isEmpty()) {
            session.setAttribute("emailError", "Email Must Be Filled");
            count++;
        }
        return count == 0;
    }

    //Profile Model
    public void profileManagerModel(Model model,
                                    HttpSession session,
                                    String profilePageUrl,
                                    String successUrl,
                                    String updateSuccess) {
        Account account = loginSession.getAccount();
        AccountDTO accountDTO = objectConverter.toAccountDTO(account);
        model.addAttribute("account", accountDTO);

        model.addAttribute("updateSuccess", updateSuccess);

        model.addAttribute("oldPassword", account.getPassword());
        model.addAttribute("emptyString", "");

        String accountImgError = (String) session.getAttribute("accountImgError");
        model.addAttribute("accountImgError", accountImgError);
        session.setAttribute("accountImgError", "");

        String CUAccountError = (String) session.getAttribute("CUAccountError");
        model.addAttribute("CUAccountError", CUAccountError);
        session.setAttribute("CUAccountError", "");

        String oldPasswordError = (String) session.getAttribute("oldPasswordError");
        model.addAttribute("oldPasswordError", oldPasswordError);
        session.setAttribute("oldPasswordError", "");

        String passwordError = (String) session.getAttribute("passwordError");
        model.addAttribute("passwordError", passwordError);
        session.setAttribute("passwordError", "");

        String updateSuccessMessage = (String) session.getAttribute("updateSuccessMessage");
        model.addAttribute("updateSuccessMessage", updateSuccessMessage);
        session.setAttribute("updateSuccessMessage", "");

        model.addAttribute("profileUrl", profilePageUrl);
        model.addAttribute("successUrl", successUrl);

        genderListModel(model);
        headerModel(model);
    }

    //Cart Model
    public void cartPageModel(Model model, HttpSession session, Optional<Integer> page,
                              String ticketListPageTitle, Page<List<Ticket>> ticketListPage,
                              String cartPageUrlTitle, String cartPageUrl) {
        model.addAttribute("promotionTicketRepository", promotionTicketRepository);
        model.addAttribute("aircraftService", aircraftService);
        model.addAttribute("flightService", flightService);
        model.addAttribute("ticketService", ticketService);
        model.addAttribute("bookingSession", bookingSession);

        String paymentResult = (String) session.getAttribute("paymentResult");
        model.addAttribute("paymentResult", paymentResult);
        session.setAttribute("paymentResult", "");

        pageItemModel(model, session, page, ticketListPageTitle, ticketListPage, cartPageUrlTitle, cartPageUrl, "");
    }

    //ViewFlight Model
    public void viewFlightPageModel(Model model, HttpSession session,
                                    Optional<Long> departureCityIdOptional,
                                    Optional<Long> arrivalCityIdOptional,
                                    Optional<String> departureTimeOptional,
                                    Optional<Integer> page, Pageable pageable,
                                    String pageUrlTitle, String basePageUrl) {
        long departureCityId = departureCityIdOptional.orElse(Long.valueOf(0));
        long arrivalCityId = arrivalCityIdOptional.orElse(Long.valueOf(0));
        String departureTimeString = departureTimeOptional.orElse("");
        model.addAttribute("departureCityId", departureCityId);
        model.addAttribute("arrivalCityId", arrivalCityId);
        model.addAttribute("departureTime", muf.stringToLocalDate(departureTimeString));

        String search = "";
        if (departureCityId != 0 || arrivalCityId != 0 || !departureTimeString.isEmpty()) {
            search = "departureCityId=" + departureCityId + "&arrivalCityId=" + arrivalCityId + "&departureTime=" + departureTimeString;
        }

        List<Flight> flightList = flightService.searchFlight(departureCityId, arrivalCityId, departureTimeString);
        if (departureCityId == 0 && arrivalCityId == 0 && departureTimeString.isEmpty()) {
            flightList = flightService.findAllFLightAfterNow();
        }
        if (flightList == null) {
            flightList = new ArrayList<>();
        }
        List<FlightDTO> flightDTOList = collectionConverter.toFlightDTOList(flightList);
        String pageItemTitle = "flightList";
        Page<FlightDTO> flightDTOPage = flightService.convertFlightDTOListToPage(flightDTOList, pageable);

        String resultSearch = "Found " + flightList.size() + " flights";
        model.addAttribute("resultSearch", resultSearch);

        model.addAttribute("muf", muf);

        pageItemModel(model, session, page, pageItemTitle, flightDTOPage, pageUrlTitle, basePageUrl, search);
        cityModel(model);
        headerModel(model);
    }

    //mail sender
    public boolean sendEmail(EmailRequest emailRequest) {
        try {
            emailService.sendEmail(emailRequest.getTo(), emailRequest.getSubject(), emailRequest.getBody());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
