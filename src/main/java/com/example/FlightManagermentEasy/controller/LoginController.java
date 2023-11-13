package com.example.FlightManagermentEasy.controller;

import com.example.FlightManagermentEasy.dto.user.AccountDTO;
import com.example.FlightManagermentEasy.entity.MyToken;
import com.example.FlightManagermentEasy.entity.user.Account;
import com.example.FlightManagermentEasy.entity.user.AccountRole;
import com.example.FlightManagermentEasy.entity.user.Role;
import com.example.FlightManagermentEasy.repository.user.user.AccountRepository;
import com.example.FlightManagermentEasy.repository.user.user.AccountRoleRepository;
import com.example.FlightManagermentEasy.repository.user.user.RoleRepository;
import com.example.FlightManagermentEasy.service.CommonModel;
import com.example.FlightManagermentEasy.service.MUF;
import com.example.FlightManagermentEasy.service.service.EmailRequest;
import com.example.FlightManagermentEasy.service.service.EmailService;
import com.example.FlightManagermentEasy.service.service.TokenService;
import com.example.FlightManagermentEasy.service.service.user.AccountService;
import com.example.FlightManagermentEasy.service.session.BookingSession;
import com.example.FlightManagermentEasy.service.session.LoginSession;
import com.example.FlightManagermentEasy.service.session.ThisMomentSession;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.Blob;
import java.time.LocalDateTime;
import java.util.Base64;
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
    EmailService  emailService;
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
        return "flight-management/loginForm";
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
            return "redirect:/admin/view-flights-page";
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
    public String signupPage(Model model,
                             HttpSession session) {
        String signUpAction = "/flight-management/user-signup";
        String title = "Create Your Own Account";
        String signUpPageUrl = "redirect:/flight-management/user-signup-page";
        String submit = "Register";

        commonModel.signupPageModel(session, model, signUpAction, title, signUpPageUrl, submit);
        return "flight-management/signupForm";
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
        //test send email bằng cách gửi qua 1 tài khoản khác thay vì gửi bằng tài khoản email test khi điền vào
        String trueSendEmail = new String();
        if(email != null){
            if(!email.isEmpty()){
                trueSendEmail = "phoenix.rebirth.2520@gmail.com";
            }
        }
        Map<String, String> userInformationMap = new HashMap<>();
        userInformationMap.put("name", name);
        userInformationMap.put("accountName", username);
        if(gender.equalsIgnoreCase("Male")){
            userInformationMap.put("gender", "Mr");
        }
        if(gender.equalsIgnoreCase("Female")){
            userInformationMap.put("gender", "Ms");
        }
        if(gender.equalsIgnoreCase("Mystery")){
            userInformationMap.put("gender", "Mr/Ms");
        }
        userInformationMap.put("encodeKey", encodeKey);
        String subject = "Account SignUp Confirmation From Flight-Management-Easy";
        Context context = new Context();
        for (Map.Entry<String, String> userInforEntry: userInformationMap.entrySet()){
            context.setVariable(userInforEntry.getKey(), userInforEntry.getValue());
        }
        try{
            System.out.println("Step 01");
            emailService.sendEmailWithHtmlTemplate(trueSendEmail, subject, "emailConfirmTemplate", context);
            System.out.println("Step 02");
        }catch (Exception e){
            e.printStackTrace();
            session.setAttribute("mailSendError", "Can Not Send Email, Please Try It Again");
            System.out.println("Email Cant Not Send");
            return errorUrl;
        }

        session.setAttribute("loginAccount", username);
        session.setAttribute("loginPassword", password);
        return "redirect:/login-page";
    }

    //  Admin SignUp
    @GetMapping("/admin/admin-signup-page")
    public String adminSignUpPage(Model model,
                                  HttpSession session) {
        String signupAction = "/admin/admin-signup";
        String title = "Welcome New Admin";
        String signupPageUrl = "redirect:/admin/admin-signup-page";
        String submit = "Signup New Admin";

        commonModel.signupPageModel(session, model, signupAction, title, signupPageUrl, submit);
        return "flight-management/signupForm";
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
        } catch (RuntimeException e) {
            session.setAttribute("CUAccountError", "Account Name, Email Or Identity Were Used By Other Accounts");
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
        return "flight-management/signupForm";
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
        } catch (RuntimeException e) {
            session.setAttribute("CUAccountError", "Account Name, Email Or Identity Were Used By Other Accounts");
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
