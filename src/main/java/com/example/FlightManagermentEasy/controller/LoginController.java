package com.example.FlightManagermentEasy.controller;

import com.example.FlightManagermentEasy.dto.user.AccountDTO;
import com.example.FlightManagermentEasy.entity.MyToken;
import com.example.FlightManagermentEasy.entity.user.Account;
import com.example.FlightManagermentEasy.entity.user.AccountRole;
import com.example.FlightManagermentEasy.entity.user.Role;
import com.example.FlightManagermentEasy.exception.InvalidDataException;
import com.example.FlightManagermentEasy.repository.MyTokenRepository;
import com.example.FlightManagermentEasy.repository.user.user.AccountRepository;
import com.example.FlightManagermentEasy.repository.user.user.AccountRoleRepository;
import com.example.FlightManagermentEasy.repository.user.user.RoleRepository;
import com.example.FlightManagermentEasy.service.CommonModel;
import com.example.FlightManagermentEasy.service.MUF;
import com.example.FlightManagermentEasy.service.service.EmailService;
import com.example.FlightManagermentEasy.service.service.TokenService;
import com.example.FlightManagermentEasy.service.service.user.AccountService;
import com.example.FlightManagermentEasy.service.session.BookingSession;
import com.example.FlightManagermentEasy.service.session.LoginSession;
import com.example.FlightManagermentEasy.service.session.ThisMomentSession;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class LoginController {
    @Autowired
    LoginSession loginSession;
    @Autowired
    BookingSession bookingSession;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    AccountRoleRepository accountRoleRepository;
    @Autowired
    AccountService accountService;
    @Autowired
    CommonModel commonModel;
    @Autowired
    TokenService tokenService;
    @Autowired
    ThisMomentSession thisMomentSession;
    @Autowired
    EmailService emailService;
    @Autowired
    MyTokenRepository myTokenRepository;
    @Autowired
    MUF muf;

    @GetMapping("/login-page")
    public String loginPage(Model model,
                            HttpSession session) {
        String loginAccount = (String) session.getAttribute("loginAccount");
        String loginPassword = (String) session.getAttribute("loginPassword");
        if (loginAccount == null) {
            loginAccount = "";
        }
        if (loginPassword == null) {
            loginPassword = "";
        }
        model.addAttribute("loginAccount", loginAccount);
        model.addAttribute("loginPassword", loginPassword);
        session.setAttribute("loginAccount", "");
        session.setAttribute("loginPassword", "");

        String loginError = (String) session.getAttribute("loginError");
        if (loginError == null) {
            loginError = "";
        }
        model.addAttribute("loginError", loginError);
        session.setAttribute("loginError", "");

        commonModel.headerModel(model);
        return "flight-management/login";
    }

    @GetMapping("/login-fail")
    public String failLogin(HttpSession session) {
        session.setAttribute("loginError", "Wrong User Name Or Password");
        return "redirect:/login-page";
    }

    @GetMapping("/login-success")
    public String login(HttpSession session, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                String username = userDetails.getUsername();
                Account account = accountRepository.findAccountByAccountName(username);
                if (account == null) {
                    account = accountRepository.findAccountByEmail(username);
                }
                if (account == null) {
                    account = accountRepository.findAccountByIdentity(username);
                }
                loginSession.setAccount(account);
            }
        }
        session.setAttribute("account", loginSession.getAccount());
        try {
            String accountImg = loginSession.getProfileImage();
            session.setAttribute("accountImg", accountImg);
        } catch (Exception e) {
            session.setAttribute("accountImgError", "Can Not Load Account Image");
        }
        if (loginSession.isAdmin()) {
            return "redirect:/manager/view-flights-page";
        }
        if (loginSession.isManager()) {
            return "redirect:/manager/view-aircraft-page";
        }
        if (loginSession.isUser()) {
            String redirectUrl = commonModel.getRedirectPageUrlSession(session, model, "currentUrl", "/");
            return redirectUrl;
        }
        return "redirect:/";
    }

    //User SignUP
    @GetMapping("/flight-management/user-signup-page")
    public String signupPage(Model model, HttpSession session) {
        String signUpAction = "/flight-management/user-signup";
        String title = "Create Your Own Account";
        String signUpPageUrl = "redirect:/flight-management/user-signup-page";
        String submit = "Register";

        commonModel.signupPageModel(session, model, signUpAction, title, signUpPageUrl, submit);
        return "flight-management/signup";
    }

    @PostMapping("/flight-management/user-signup")
    public String signupUser(@RequestParam("username") String username,
                             @RequestParam("password") String password,
                             @RequestParam("repassword") String repassword,
                             @RequestParam("name") String name,
                             @RequestParam("email") String email,
                             @RequestParam("identity") String identity,
                             @RequestParam("phone") String phone,
                             @RequestParam("address") String address,
                             @RequestParam("dob") String dob,
                             @RequestParam("gender") String gender,
                             @RequestParam("profileImage") MultipartFile profileImage,
                             @RequestParam("signUpPageUrl") String errorUrl,
                             HttpSession session) {
        if (!commonModel.isSignupProcessSuccess(session, username, password, repassword, name, email, identity, phone, address, dob, gender, profileImage)) {
            return errorUrl;
        }
        List<Account> accountList = accountRepository.findAll();
        List<String> accountNameList = accountList.stream().map(Account::getAccountName).collect(Collectors.toList());
        List<String> identityList = accountList.stream().map(Account::getIdentity).collect(Collectors.toList());
        List<String> emailList = accountList.stream().map(Account::getEmail).collect(Collectors.toList());
        if (accountNameList.contains(username)) {
            session.setAttribute("accountNameError", "Account Name Was Used By Another Account");
            return errorUrl;
        }
        if (identityList.contains(identity)) {
            session.setAttribute("identityError", "Identity Was Used By Another Account");
            return errorUrl;
        }
        if (emailList.contains(email)) {
            session.setAttribute("emailError", "Email Was Used By Another Account");
            return errorUrl;
        }
        Account account = new Account();
        LocalDateTime thisMoment = thisMomentSession.getThisMoment();
        String createTime = muf.localDateTimeToString(thisMoment);
        MyToken token = tokenService.setAccountToken(account.getId(), name, dob, address, identity, gender, username, password, email, phone, profileImage, createTime);
        String encodeKey = token.getEncodeKey();
        String trueSendEmail = new String();
        if (email != null) {
            if (!email.isEmpty()) {
                trueSendEmail = "phoenix.rebirth.2520@gmail.com";
            }
        }
        Map<String, String> userInformationMap = new HashMap<>();
        userInformationMap.put("name", name);
        userInformationMap.put("accountName", username);
        if (gender.equalsIgnoreCase("Male")) {
            userInformationMap.put("gender", "Mr");
        }
        if (gender.equalsIgnoreCase("Female")) {
            userInformationMap.put("gender", "Ms");
        } else {
            userInformationMap.put("gender", "Mr/Ms");
        }
        userInformationMap.put("encodeKey", encodeKey);

        String subject = "Account SignUp Confirmation From Flight-Management-Easy";
        Context context = new Context();
        for (Map.Entry<String, String> userInforEntry : userInformationMap.entrySet()) {
            context.setVariable(userInforEntry.getKey(), userInforEntry.getValue());
        }
        try {
            emailService.sendEmailWithHtmlTemplate(trueSendEmail, subject, "emailAccountSignup", context);
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("mailSendError", "Can Not Send Email, Please Try It Again");
            System.out.println("Email Cant Not Send");
            return errorUrl;
        }

        session.setAttribute("loginAccount", username);
        session.setAttribute("loginPassword", password);
        return "redirect:/login-page";
    }


    @PostMapping("/flight-management/user-confirm-signup/{encodeKey}")
    public String userConfirmSignup(@PathVariable("encodeKey") String encodeKey, HttpSession session) {
        MyToken token = myTokenRepository.findMyTokenByEncodeKey(encodeKey);
        if (token != null) {
            Map<String, String> accountInformationMap = tokenService.getAccountInformationMapInToken(token);
            LocalDateTime thisMoment = thisMomentSession.getThisMoment();
            LocalDateTime createTime = muf.stringToLocalDateTime(accountInformationMap.get("createTime"));
            try {
                if (createTime != null) {
                    if (thisMoment.isBefore(createTime.plusMinutes(10))) {
                        long accountId = Long.valueOf(accountInformationMap.get("id"));
                        Account account = accountRepository.findById(accountId).orElse(null);
                        if (account == null) {
                            account = new Account();
                        }
                        String name = accountInformationMap.get("name");
                        String dob = accountInformationMap.get("dob");
                        String address = accountInformationMap.get("address");
                        String identity = accountInformationMap.get("identity");
                        String gender = accountInformationMap.get("gender");
                        String accountName = accountInformationMap.get("accountName");
                        String password = accountInformationMap.get("password");
                        String email = accountInformationMap.get("email");
                        String phone = accountInformationMap.get("phone");
                        String image = accountInformationMap.get("image");
                        try {
                            accountService.signupAndUpdateAccount(accountName, password, name, email, identity, phone, address, dob, gender, image, account);
                            myTokenRepository.delete(token);
                        } catch (IOException e) {
                            System.out.println("Image load fail");
                        } catch (InvalidDataException e) {
                            System.out.println("Create Account Fail");
                            session.setAttribute("signupConfirmError", e.getMessage());
                            myTokenRepository.delete(token);
                            return "redirect:/flight-management/user-signup-page";
                        }
                        return "redirect:/login-page";
                    } else {
                        myTokenRepository.delete(token);
                        session.setAttribute("signupConfirmError", "Time Expired, Please Try Again");
                        return "redirect:/flight-management/user-signup-page";
                    }
                } else {
                    myTokenRepository.delete(token);
                    session.setAttribute("signupConfirmError", "Can Not Find Information, Create Time Is Null");
                    return "redirect:/flight-management/user-signup-page";
                }
            } catch (DateTimeParseException e) {
                myTokenRepository.delete(token);
                session.setAttribute("signupConfirmError", "Can Not Find Information, Create Time Encode Fail");
                return "redirect:/flight-management/user-signup-page";
            }
        } else {
            session.setAttribute("signupConfirmError", "Can Not Find Information");
            return "redirect:/flight-management/user-signup-page";
        }
    }

    @GetMapping("/flight-management/forget-password-page")
    public String forgetPasswordPage(Model model, HttpSession session) {
        String forgetPasswordEmailError = (String) session.getAttribute("forgetPasswordEmailError");
        String forgetEmail = (String) session.getAttribute("forgetEmail");
        session.setAttribute("forgetPasswordEmailError", "");
        session.setAttribute("forgetEmail", "");
        model.addAttribute("forgetEmail", forgetEmail);
        model.addAttribute("signupAction", "/flight-management/information-send");
        model.addAttribute("forgetPasswordEmailError", forgetPasswordEmailError);
        model.addAttribute("submit", "Confirm");
        model.addAttribute("pageUrl", "redirect:/flight-management/forget-password-page");
        commonModel.headerModel(model);
        return "forgetAccount";
    }

    @PostMapping("/flight-management/information-send")
    public String informationSend(Model model, HttpSession session,
                                  @RequestParam("email") String email,
                                  @RequestParam("pageUrl") String pageUrl){
        try {
            String[] s = accountService.getInformationFromEmail(email);
            String accountName =  s[0];
            String name = s[1];
            String newPassword = s[2];
            String trueSendEmail = new String();
            if (email != null) {
                if (!email.isEmpty()) {
                    trueSendEmail = "phoenix.rebirth.2520@gmail.com";
                }
            }
            String subject = "Account And Password Recover";
            Context context = new Context();
            context.setVariable("accountName", accountName);
            context.setVariable("name", name);
            context.setVariable("password", newPassword);
            try {
                emailService.sendEmailWithHtmlTemplate(trueSendEmail, subject, "emailPasswordAndAccountRecover", context);
            } catch (Exception e) {
                e.printStackTrace();
                session.setAttribute("mailSendError", "Can Not Send Email, Please Try It Again");
                System.out.println("Email Cant Not Send");
            }
            return "redirect:/login-page";
        }catch (InvalidDataException e){
            e.printStackTrace();
            String reportError = e.getMessage();
            session.setAttribute("forgetEmail", email);
            session.setAttribute("forgetPasswordEmailError", reportError);
            return pageUrl;
        }
    }

    //  Admin SignUp
    @GetMapping("/admin/admin-signup-page")
    public String adminSignUpPage(Model model, HttpSession session) {
        String signupAction = "/admin/admin-signup";
        String title = "Welcome New Admin";
        String signupPageUrl = "redirect:/admin/admin-signup-page";
        String submit = "Signup New Admin";

        commonModel.signupPageModel(session, model, signupAction, title, signupPageUrl, submit);
        return "flight-management/signup";
    }

    @PostMapping("/admin/admin-signup")
    public String signupAdmin(@RequestParam("username") String username,
                              @RequestParam("password") String password,
                              @RequestParam("repassword") String repassword,
                              @RequestParam("name") String name,
                              @RequestParam("email") String email,
                              @RequestParam("identity") String identity,
                              @RequestParam("phone") String phone,
                              @RequestParam("address") String address,
                              @RequestParam("dob") String dob,
                              @RequestParam("gender") String gender,
                              @RequestParam("profileImage") MultipartFile profileImage,
                              @RequestParam("signUpPageUrl") String errorUrl,
                              HttpSession session) {
        if (commonModel.isSignupProcessSuccess(session, username, password, repassword, name, email, identity, phone, address, dob, gender, profileImage) == false) {
            return errorUrl;
        }

        Account account = accountRepository.findAccountByAccountName(username);
        if (account == null) {
            account = accountRepository.findAccountByEmail(email);
        }
        if (account == null) {
            account = accountRepository.findAccountByIdentity(identity);
        }
        if (account == null) {
            account = new Account();
        }
        if (accountService.isAdmin(account)) {
            session.setAttribute("accountNameError", "This Is Another Admin Account, You Can Not Re-Create It");
            return errorUrl;
        }

        try {
            accountService.signupAndUpdateAccount(username, password, name, email, identity, phone, address, dob, gender, profileImage, account);
        } catch (IOException e) {
            session.setAttribute("profileImage", null);
            session.setAttribute("imageError", "File Could Not Load");
        } catch (InvalidDataException e) {
            session.setAttribute("CUAccountError", e.getMessage());
            return errorUrl;
        }

        Role admin = roleRepository.findRoleByName("admin");
        AccountRole accountRole = new AccountRole(account, admin);
        accountRoleRepository.save(accountRole);

        session.setAttribute("loginAccount", username);
        session.setAttribute("loginPassword", password);
        return "redirect:/login-page";
    }

    //Manager SignUp
    @GetMapping("/admin/manager-signup-page")
    public String managerSignUpPage(Model model,
                                    HttpSession session) {
        String signupAction = "/admin/manager-signup";
        String title = "Welcome New Manager";
        String signupPageUrl = "redirect:/admin/manager-signup-page";
        String submit = "Signup New Manager";

        commonModel.signupPageModel(session, model, signupAction, title, signupPageUrl, submit);
        return "flight-management/signup";
    }

    @PostMapping("/admin/manager-signup")
    public String signupManager(@RequestParam("username") String username,
                                @RequestParam("password") String password,
                                @RequestParam("repassword") String repassword,
                                @RequestParam("name") String name,
                                @RequestParam("email") String email,
                                @RequestParam("identity") String identity,
                                @RequestParam("phone") String phone,
                                @RequestParam("address") String address,
                                @RequestParam("dob") String dob,
                                @RequestParam("gender") String gender,
                                @RequestParam("profileImage") MultipartFile profileImage,
                                @RequestParam("signUpPageUrl") String errorUrl,
                                HttpSession session) {
        if (!commonModel.isSignupProcessSuccess(session, username, password, repassword, name, email, identity, phone, address, dob, gender, profileImage)) {
            return errorUrl;
        }

        Account account = accountRepository.findAccountByAccountName(username);
        if (account == null) {
            account = accountRepository.findAccountByEmail(email);
        }
        if (account == null) {
            account = accountRepository.findAccountByIdentity(identity);
        }
        if (account == null) {
            account = new Account();
        }

        if (accountService.isManager(account)) {
            session.setAttribute("accountNameError", "This Is Another Manager Account, You Can Not Re-Create It");
            return errorUrl;
        }

        try {
            accountService.signupAndUpdateAccount(username, password, name, email, identity, phone, address, dob, gender, profileImage, account);
        } catch (IOException e) {
            session.setAttribute("profileImage", null);
            session.setAttribute("imageError", "File Could Not Load");
        } catch (InvalidDataException e) {
            session.setAttribute("CUAccountError", e.getMessage());
            return errorUrl;
        }

        Role manager = roleRepository.findRoleByName("manager");
        AccountRole accountRole = new AccountRole(account, manager);
        accountRoleRepository.save(accountRole);

        session.setAttribute("loginAccount", username);
        session.setAttribute("loginPassword", password);
        return "redirect:/login-page";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        loginSession.logout();
        bookingSession.reloadBookingSession();
        session.setAttribute("account", null);
        session.setAttribute("accountImg", null);
        return "redirect:/";
    }

    @GetMapping("/admin/view-manager-page")
    public String viewManagerPage(Model model, HttpSession session) {
        return "viewManagerPage";
    }

    @PostMapping("/admin/remove-manager")
    public String removeManager(HttpSession session) {
        return "redirect:/admin/view-manager-page";
    }
}
