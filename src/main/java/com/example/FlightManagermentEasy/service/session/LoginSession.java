package com.example.FlightManagermentEasy.service.session;

import com.example.FlightManagermentEasy.entity.user.Account;
import com.example.FlightManagermentEasy.entity.user.AccountRole;
import com.example.FlightManagermentEasy.repository.user.user.AccountRepository;
import com.example.FlightManagermentEasy.repository.user.user.AccountRoleRepository;
import com.example.FlightManagermentEasy.repository.user.user.RoleRepository;
import com.example.FlightManagermentEasy.service.service.user.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

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

    public void updateProfileImage(MultipartFile profileImage) throws IOException {
        if (isLoggedIn()) {
            try {
                this.account = accountService.updateAccountImage(profileImage, this.account);
            } catch (IOException e) {
                throw new IOException();
            }
        }
    }

    public void updateProfile(String username, String password, String name, String email, String identity,
                              String phone, String address, String dob, String gender, MultipartFile profileImage) throws IOException, RuntimeException {
        try {
            this.account = accountService.signupAndUpdateAccount(username, password, name, email, identity, phone, address, dob, gender, profileImage, this.account);
        } catch (IOException e) {
            throw new IOException();
        } catch (RuntimeException e) {
            throw new RuntimeException();
        }
    }

    public String getProfileImage() throws IOException {
        if (isLoggedIn()) {
            if (this.account.getImage() != null) {
                try {
                    byte[] imgData = this.account.getImage().getBytes(1L, (int) this.account.getImage().length());
                    String base64String = Base64.getEncoder().encodeToString(imgData);
                    return base64String;
                } catch (Exception e) {
                    throw new IOException();
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
