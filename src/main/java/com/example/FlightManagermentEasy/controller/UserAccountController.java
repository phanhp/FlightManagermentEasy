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
import org.springframework.security.core.parameters.P;
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

    @PostMapping("/user/update-profile-image")
    public String updateProfileImage(@RequestParam("profileImage") MultipartFile multipartFile,
                                     @RequestParam("accountId") long accountId,
                                     @RequestParam("successUrl") String successUrl,
                                     HttpSession session){
        String updateProfileResult = loginSession.updateProfileImageResult(multipartFile, accountId);
        session.setAttribute("updateProfileResult", updateProfileResult);
        return successUrl;
    }

    @PostMapping("/user/update-profile-information")
    public String updateProfileInformation(@RequestParam("accountId") long accountId,
                                           @RequestParam("name") String name,
                                           @RequestParam("email") String email,
                                           @RequestParam("identity") String identity,
                                           @RequestParam("phone") String phone,
                                           @RequestParam("address") String address,
                                           @RequestParam("dob") String dob,
                                           @RequestParam("gender") String gender,
                                           @RequestParam("profileImage") MultipartFile profileImage,
                                           @RequestParam("oldPassword") String oldPassword,
                                           @RequestParam("oldPasswordHide") String oldPasswordHide,
                                           @RequestParam("successUrl") String successUrl,
                                           @RequestParam("profileUrl") String profileUrl,
                                           HttpSession session){
        String updateProfileResult = loginSession.updateProfileResult(accountId, name, email, identity, phone, address, dob, gender, profileImage, oldPasswordHide, oldPassword);
        session.setAttribute("updateProfileResult", updateProfileResult);
        if(updateProfileResult.contains("Success")){
            return successUrl;
        }
        return profileUrl;
    }

    @PostMapping("/user/update-profile-password")
    public String updateProfilePassword(@RequestParam("accountId") long accountId,
                                        @RequestParam("password") String password,
                                        @RequestParam("repassword") String repassword,
                                        @RequestParam("oldPassword") String oldPassword,
                                        @RequestParam("oldPasswordHide") String oldPasswordHide,
                                        @RequestParam("successUrl") String successUrl,
                                        @RequestParam("profileUrl") String profileUrl,
                                        HttpSession session){
        String updateProfileResult = loginSession.updateProfilePasswordResult(accountId, password, repassword, oldPassword, oldPasswordHide);
        session.setAttribute("updateProfileResult", updateProfileResult);
        if(updateProfileResult.contains("Success")){
            return successUrl;
        }
        return profileUrl;
    }


}
