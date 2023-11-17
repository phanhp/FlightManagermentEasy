package com.example.FlightManagermentEasy.service.session;

import com.example.FlightManagermentEasy.entity.user.Account;
import com.example.FlightManagermentEasy.entity.user.AccountRole;
import com.example.FlightManagermentEasy.exception.InvalidDataException;
import com.example.FlightManagermentEasy.repository.user.user.AccountRepository;
import com.example.FlightManagermentEasy.repository.user.user.AccountRoleRepository;
import com.example.FlightManagermentEasy.repository.user.user.RoleRepository;
import com.example.FlightManagermentEasy.service.MUF;
import com.example.FlightManagermentEasy.service.service.user.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@SessionScope
public class LoginSession {
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    AccountRoleRepository accountRoleRepository;
    @Autowired
    AccountService accountService;
    @Autowired
    MUF muf;

    private Account account = new Account();

    public Account getAccount() {
        return this.account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public void logout() {
        this.account = new Account();
    }

    public boolean isLoggedIn() {
        if (account.getId() == 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isAdmin() {
        List<AccountRole> accountRoleList = account.getAccountRoleList();
        int check = 0;
        for (int i = 0; i < accountRoleList.size(); i++) {
            if (accountRoleList.get(i).getRole().getName().equals("admin")) {
                check++;
            }
        }
        return check != 0;
    }

    public boolean isManager() {
        List<AccountRole> accountRoleList = account.getAccountRoleList();
        int check = 0;
        for (int i = 0; i < accountRoleList.size(); i++) {
            if (accountRoleList.get(i).getRole().getName().equals("manager")) {
                check++;
            }
        }
        return check != 0;
    }

    public boolean isUser() {
        List<AccountRole> accountRoleList = account.getAccountRoleList();
        int check = 0;
        for (int i = 0; i < accountRoleList.size(); i++) {
            if (accountRoleList.get(i).getRole().getName().equals("user")) {
                check++;
            }
        }
        return check != 0;
    }

    //************************ UPDATE PROFILE IMAGE **********************************8
    public void updateProfileImage(MultipartFile profileImage, Account account) throws IOException, InvalidDataException {
        if (isLoggedIn()) {
            if (!muf.isImage(profileImage)) {
                throw new InvalidDataException("This Is Not Image");
            }
            if (account != null) {
                if (account.getId() != this.account.getId()) {
                    throw new InvalidDataException("Use Can Not Update Image For Another Account");
                }
            }
            try {
                this.account = accountService.updateAccountImage(profileImage, this.account);
            } catch (InvalidDataException e) {
                throw new InvalidDataException("Profile Image Update Fail");
            }
        } else {
            throw new InvalidDataException("User Must Be Login To An Account To Update Profile");
        }
    }

    public String updateProfileImageResult(MultipartFile profileImage, Account account) {
        try {
            updateProfileImage(profileImage, account);
            return "Profile Image Update Successfully";
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        } catch (InvalidDataException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public String updateProfileImageResult(MultipartFile profileImage, long accountId) {
        Account account = accountRepository.findById(accountId).orElse(null);
        return updateProfileImageResult(profileImage, account);
    }

    public String getProfileImage() throws InvalidDataException {
        if (isLoggedIn()) {
            if (this.account.getImage() != null) {
                try {
                    byte[] imgData = this.account.getImage().getBytes(1L, (int) this.account.getImage().length());
                    String base64String = Base64.getEncoder().encodeToString(imgData);
                    return base64String;
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw new InvalidDataException("Load Profile Image Fail");
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    //******************************** UPDATE PROFILE BASIC INFORMATION *********************************
    public void updateProfile(Account account, String name, String email,
                              String identity, String phone, String address,
                              String dob, String gender, MultipartFile profileImage,
                              String oldPassword, String confirmPassword) throws InvalidDataException {
        if (isLoggedIn()) {
            if (account != null) {
                if (account.getId() != this.account.getId()) {
                    throw new InvalidDataException("Use Can Not Update Image For Another Account");
                }
            }
            if (name == null) {
                throw new InvalidDataException("UserName Must Be Filled");
            }
            if (name.isEmpty()) {
                throw new InvalidDataException("UserName Must Be Filled");
            }
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            if (!passwordEncoder.matches(confirmPassword, oldPassword)) {
                throw new InvalidDataException("Wrong Input Confirm Password");
            }
            if (email == null) {
                throw new InvalidDataException("Email Must Be Filled");
            }
            if (email.isEmpty()) {
                throw new InvalidDataException("Email Must Be Filled");
            }
            if (identity == null) {
                throw new InvalidDataException("Identity Must Be Filled");
            }
            if (identity.isEmpty()) {
                throw new InvalidDataException("Identity Must Be Filled");
            }
            if (this.account.getEmail() == null) {
                this.account.setEmail("");
            }
            if (!email.equals(this.account.getEmail())) {
                List<Account> accountList = accountRepository.findAll();
                List<String> emailList = accountList.stream().map(Account::getEmail).collect(Collectors.toList());
                if (emailList.contains(email)) {
                    throw new InvalidDataException("Email Was Used By Another Account, Please Try Another Email");
                }
            }
            if (this.account.getIdentity() == null) {
                this.account.setIdentity("");
            }
            if (!identity.equals(this.account.getIdentity())) {
                List<Account> accountList = accountRepository.findAll();
                List<String> identityList = accountList.stream().map(Account::getIdentity).collect(Collectors.toList());
                if (identityList.contains(identity)) {
                    throw new InvalidDataException("Identity Was Used By Another Account, Please Try Another Identity");
                }
            }
            this.account.setName(name);
            this.account.setEmail(email);
            this.account.setIdentity(identity);
            this.account.setPhone(phone);
            this.account.setAddress(address);
            this.account.setDob(muf.stringToLocalDate(dob));
            this.account.setGender(gender);
            try {
                this.account = accountService.updateAccountImage(profileImage, this.account);
            } catch (InvalidDataException e) {
                throw new InvalidDataException("Profile Image Update Fail");
            }
        } else {
            throw new InvalidDataException("User Must Be Login To An Account To Update Profile");
        }
    }

    public String updateProfileResult(Account account, String name, String email,
                                      String identity, String phone, String address,
                                      String dob, String gender, MultipartFile profileImage,
                                      String oldPassword, String confirmPassword) {
        try {
            updateProfile(account, name, email, identity, phone, address, dob, gender, profileImage, oldPassword, confirmPassword);
            return "Profile Image Update Successfully";
        } catch (InvalidDataException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public String updateProfileResult(long accountId, String name, String email,
                                      String identity, String phone, String address,
                                      String dob, String gender, MultipartFile profileImage,
                                      String oldPassword, String confirmPassword) {
        Account account = accountRepository.findById(accountId).orElse(null);
        return updateProfileResult(account, name, email, identity, phone, address, dob, gender, profileImage, oldPassword, confirmPassword);
    }

    //******************************** UPDATE PROFILE Password *********************************
    public void updateProfilePassword(Account account, String password,
                                      String repassword, String oldPassword, String oldPasswordHide) throws InvalidDataException {
        if (isLoggedIn()) {
            if (account != null) {
                if (account.getId() != this.account.getId()) {
                    throw new InvalidDataException("Use Can Not Update Image For Another Account");
                }
            }
            if (password == null) {
                throw new InvalidDataException("Password Can Not Be Null");
            }
            if (password.length() < 3) {
                throw new InvalidDataException("Password Can Not Be Shorter Than 3");
            }
            System.out.println(password);
            System.out.println(repassword);
            if (!password.equals(repassword)) {
                throw new InvalidDataException("Password And Confirm-password Was Not Matched");
            }
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            if (!passwordEncoder.matches(oldPassword, oldPasswordHide)) {
                throw new InvalidDataException("Wrong Input Old Password");
            }
            this.account.setPassword(passwordEncoder.encode(password));
            accountRepository.save(this.account);
        } else {
            throw new InvalidDataException("User Must Be Login To An Account To Update Profile");
        }
    }

    public String updateProfilePasswordResult(Account account, String password,
                                              String repassword, String oldPassword,
                                              String oldPasswordHide) {
        try {
            updateProfilePassword(account, password, repassword, oldPassword, oldPasswordHide);
            return "Password Update Success";
        } catch (InvalidDataException e) {
            return e.getMessage();
        }
    }

    public String updateProfilePasswordResult(long accountId, String password,
                                              String repassword, String oldPassword,
                                              String oldPasswordHide) {
        Account account = accountRepository.findById(accountId).orElse(null);
        return updateProfilePasswordResult(account, password, repassword, oldPassword, oldPasswordHide);
    }
}
