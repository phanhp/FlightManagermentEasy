package com.example.FlightManagermentEasy.controller;

import com.example.FlightManagermentEasy.dto.user.AccountDTO;
import com.example.FlightManagermentEasy.entity.user.Account;
import com.example.FlightManagermentEasy.exception.InvalidDataException;
import com.example.FlightManagermentEasy.service.CommonModel;
import com.example.FlightManagermentEasy.service.MUF;
import com.example.FlightManagermentEasy.service.dtoconverter.ObjectConverter;
import com.example.FlightManagermentEasy.service.service.user.AccountService;
import com.example.FlightManagermentEasy.service.session.LoginSession;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
public class UserAccountController {
    @Autowired
    CommonModel commonModel;
    @Autowired
    LoginSession loginSession;
    @Autowired
    ObjectConverter objectConverter;
    @Autowired
    AccountService accountService;
    @Autowired
    MUF muf;

    @GetMapping("/user/view-profile-page")
    public String userViewProfilePage(Model model,
                                      HttpSession session) {
        String successUrl = "redirect:/user/view-profile-page";
        String profileUrl = "redirect:/user/view-profile-page";
        String updateSuccess = "Image Update Successfully";

        commonModel.updateProfile(session, model, profileUrl, successUrl, updateSuccess);
        return "userViewProfile";
    }


    @GetMapping("/user/edit-information-page")
    public String userEditProfilePage(Model model,
                                      HttpSession session) {
        String successUrl = "redirect:/user/view-profile-page";
        String profileUrl = "redirect:/user/edit-information-page";
        String updateSuccess = "Information Update Successfully";
        commonModel.updateProfile(session, model, profileUrl, successUrl, updateSuccess);
        return "userEditProfileInformation";
    }

    @GetMapping("/user/edit-password-page")
    public String userEditPasswordPage(Model model,
                                       HttpSession session) {
        String successUrl = "redirect:/user/view-profile-page";
        String profileUrl = "redirect:/user/edit-password-page";
        String updateSuccess = "Password Update Successfully";
        commonModel.updateProfile(session, model, profileUrl, successUrl, updateSuccess);
        return "userEditProfilePassword";
    }

    @PostMapping("/user/update-profile")
    public String updateProfile(@RequestParam(value = "username", required = false) Optional<String> usernameOptional,
                                @RequestParam(value = "password", required = false) Optional<String> passwordOptional,
                                @RequestParam(value = "repassword", required = false) Optional<String> repasswordOptional,
                                @RequestParam(value = "oldPassword", required = false) Optional<String> oldPasswordOptional,
                                @RequestParam(value = "oldPasswordHide", required = false) Optional<String> oldPasswordHideOptional,
                                @RequestParam(value = "name", required = false) Optional<String> nameOptional,
                                @RequestParam(value = "email", required = false) Optional<String> emailOptional,
                                @RequestParam(value = "identity", required = false) Optional<String> identityOptional,
                                @RequestParam(value = "phone", required = false) Optional<String> phoneOptional,
                                @RequestParam(value = "address", required = false) Optional<String> addressOptional,
                                @RequestParam(value = "dob", required = false) Optional<String> dobOptional,
                                @RequestParam(value = "gender", required = false) Optional<String> genderOptional,
                                @RequestParam(value = "profileImage", required = false) Optional<MultipartFile> profileImageOptional,
                                @RequestParam("profileUrl") String profileUrl,
                                @RequestParam("successUrl") String successUrl,
                                @RequestParam("updateSuccess") String updateSuccess,
                                HttpSession session) {
        String userName = usernameOptional.orElse(null);
        String password = passwordOptional.orElse(null);
        String repassword = repasswordOptional.orElse(null);
        String oldPassword = oldPasswordOptional.orElse(null);
        String oldPasswordHide = oldPasswordHideOptional.orElse(null);
        String name = nameOptional.orElse(null);
        String email = emailOptional.orElse(null);
        String identity = identityOptional.orElse(null);
        String phone = phoneOptional.orElse(null);
        String address = addressOptional.orElse(null);
        String dob = dobOptional.orElse(null);
        String gender = genderOptional.orElse(null);
        MultipartFile profileImage = profileImageOptional.orElse(null);
        if (oldPassword != null && oldPasswordHide != null) {
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            if (passwordEncoder.matches(oldPassword, oldPasswordHide)) {
                if (password != null && repassword != null) {
                    if (password.equals(repassword)) {
                        if (password.length() >= 3) {
                            try {
                                loginSession.updatePassword(password);
                            } catch (InvalidDataException e) {
                                session.setAttribute("passwordError", e.getMessage());
                                return profileUrl;
                            }
                        } else {
                            session.setAttribute("passwordError", "Password Must Be Longer Or Equal Than 3 Character");
                            return profileUrl;
                        }
                    } else {
                        session.setAttribute("passwordError", "Password And Confirm-password Must Be Matched");
                        return profileUrl;
                    }
                } else {
                    try {
                        loginSession.updateProfile(userName, password, name, email, identity, phone, address, dob, gender, profileImage);
                    } catch (IOException e) {
                        session.setAttribute("accountImgError", "Can not load file");
                        return profileUrl;
                    } catch (InvalidDataException e) {
                        session.setAttribute("CUAccountError", "Account Name, Email Or Identity Were Used By Other Accounts");
                        return profileUrl;
                    }
                }
            } else {
                session.setAttribute("oldPasswordError", "Wrong Old Password Confirm");
                return profileUrl;
            }
        } else {
            try {
                loginSession.updateProfile(userName, password, name, email, identity, phone, address, dob, gender, profileImage);
            } catch (IOException e) {
                session.setAttribute("accountImgError", "Can not load file");
                return profileUrl;
            } catch (InvalidDataException e) {
                session.setAttribute("CUAccountError", "Account name, Email or Identity was used by other accounts");
                return profileUrl;
            }
        }
        session.setAttribute("updateSuccessMessage", updateSuccess);
        return successUrl;
    }


}
