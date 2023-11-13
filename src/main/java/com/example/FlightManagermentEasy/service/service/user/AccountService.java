package com.example.FlightManagermentEasy.service.service.user;

import com.example.FlightManagermentEasy.entity.user.Account;
import com.example.FlightManagermentEasy.entity.user.AccountRole;
import com.example.FlightManagermentEasy.entity.user.Role;
import com.example.FlightManagermentEasy.repository.user.user.AccountRepository;
import com.example.FlightManagermentEasy.repository.user.user.AccountRoleRepository;
import com.example.FlightManagermentEasy.repository.user.user.RoleRepository;
import com.example.FlightManagermentEasy.service.MUF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    AccountRoleRepository accountRoleRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    MUF muf;

    public Account signupAndUpdateAccount(String username, String password, String name,
                                          String email, String identity, String phone,
                                          String address, String dob, String gender,
                                          MultipartFile profileImage, Account account) throws IOException, RuntimeException {
        List<Account> accountList = new ArrayList<>();
        if (username != null) {
            if (!username.isEmpty()) {
                accountList = accountRepository.findAll();
            }
        }
        if (accountList.isEmpty()) {
            if (email != null) {
                if (!email.isEmpty()) {
                    accountList = accountRepository.findAll();
                }
            }
        }
        if (accountList.isEmpty()) {
            if (identity != null) {
                if (!identity.isEmpty()) {
                    accountList = accountRepository.findAll();
                }
            }
        }
        if (username != null) {
            if (!username.isEmpty()) {
                String check = account.getAccountName();
                if (check != null) {
                    if (!check.isEmpty()) {
                        if (!check.equals(username)) {
                            List<String> accountNameList = accountList.stream().map(Account::getAccountName).collect(Collectors.toList());
                            if (!accountNameList.contains(username)) {
                                account.setAccountName(username);
                            } else {
                                throw new RuntimeException();
                            }
                        }
                    } else {
                        List<String> accountNameList = accountList.stream().map(Account::getAccountName).collect(Collectors.toList());
                        if (!accountNameList.contains(username)) {
                            account.setAccountName(username);
                        } else {
                            throw new RuntimeException();
                        }
                    }
                } else {
                    List<String> accountNameList = accountList.stream().map(Account::getAccountName).collect(Collectors.toList());
                    if (!accountNameList.contains(username)) {
                        account.setAccountName(username);
                    } else {
                        throw new RuntimeException();
                    }
                }
            }
        }
        if (password != null) {
            if (password.length() >= 3) {
                PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                account.setPassword(passwordEncoder.encode(password));
            }
        }
        if (name != null) {
            System.out.println(name);
            if (!name.isEmpty()) {
                account.setName(name);
            }
        }
        if (email != null) {
            if (!email.isEmpty()) {
                String check = account.getEmail();
                if (check != null) {
                    if (!check.isEmpty()) {
                        if (!check.equals(email)) {
                            List<String> emailList = accountList.stream().map(Account::getEmail).collect(Collectors.toList());
                            if (!emailList.contains(email)) {
                                account.setEmail(email);
                            } else {
                                throw new RuntimeException();
                            }
                        }
                    } else {
                        List<String> emailList = accountList.stream().map(Account::getEmail).collect(Collectors.toList());
                        if (!emailList.contains(email)) {
                            account.setEmail(email);
                        } else {
                            throw new RuntimeException();
                        }
                    }
                } else {
                    List<String> emailList = accountList.stream().map(Account::getEmail).collect(Collectors.toList());
                    if (!emailList.contains(email)) {
                        account.setEmail(email);
                    } else {
                        throw new RuntimeException();
                    }
                }
            }
        }
        if (identity != null) {
            if (!identity.isEmpty()) {
                String check = account.getIdentity();
                if (check != null) {
                    if (!check.isEmpty()) {
                        if (!check.equals(identity)) {
                            List<String> identityList = accountList.stream().map(Account::getIdentity).collect(Collectors.toList());
                            if (!identityList.contains(identity)) {
                                account.setIdentity(identity);
                            } else {
                                throw new RuntimeException();
                            }
                        }
                    } else {
                        List<String> identityList = accountList.stream().map(Account::getIdentity).collect(Collectors.toList());
                        if (!identityList.contains(identity)) {
                            account.setIdentity(identity);
                        } else {
                            throw new RuntimeException();
                        }
                    }
                } else {
                    List<String> identityList = accountList.stream().map(Account::getIdentity).collect(Collectors.toList());
                    if (!identityList.contains(identity)) {
                        account.setIdentity(identity);
                    } else {
                        throw new RuntimeException();
                    }
                }
            }
        }
        if (phone != null) {
            account.setPhone(phone);
        }
        if (address != null) {
            account.setAddress(address);
        }
        if (dob != null) {
            account.setDob(muf.stringToLocalDate(dob));
        }
        if (gender != null) {
            account.setGender(gender);
        }
        if (profileImage != null) {
            if (!profileImage.isEmpty()) {
                if (muf.isImage(profileImage)) {
                    try {
                        byte[] imgData = profileImage.getBytes();
                        Blob image = new SerialBlob(imgData);
                        account.setImage(image);
                    } catch (Exception e) {
                        throw new IOException();
                    }
                } else {
                    throw new IOException();
                }
            }
        }
        accountRepository.save(account);
        if (!isUser(account)) {
            Role user = roleRepository.findRoleByName("user");
            AccountRole accountRole = new AccountRole(account, user);
            accountRoleRepository.save(accountRole);
        }
        return account;
    }

    public Account updateAccountImage(MultipartFile profileImage, Account account) throws IOException {
        if (profileImage != null) {
            if (!profileImage.isEmpty()) {
                try {
                    byte[] imgData = profileImage.getBytes();
                    Blob image = new SerialBlob(imgData);
                    account.setImage(image);
                    accountRepository.save(account);
                } catch (Exception e) {
                    throw new IOException();
                }
            }
        }
        return account;
    }

    public void updateAccountImage(MultipartFile profileImage, long accountId) throws IOException {
        Account account = accountRepository.findById(accountId).orElse(null);
        if (account != null) {
            try {
                updateAccountImage(profileImage, account);
            } catch (IOException e) {
                throw new IOException();
            }
        }
    }

    public boolean isUser(Account account) {
        List<AccountRole> accountRoleList = account.getAccountRoleList();
        if (accountRoleList != null) {
            int check = 0;
            for (int i = 0; i < accountRoleList.size(); i++) {
                if (accountRoleList.get(i).getRole().getName().equals("user")) {
                    check++;
                }
            }
            return check != 0;
        } else {
            return false;
        }
    }

    public boolean isAdmin(Account account) {
        List<AccountRole> accountRoleList = account.getAccountRoleList();
        if (accountRoleList != null) {
            int check = 0;
            for (int i = 0; i < accountRoleList.size(); i++) {
                if (accountRoleList.get(i).getRole().getName().equals("admin")) {
                    check++;
                }
            }
            return check != 0;
        } else {
            return false;
        }
    }

    public boolean isManager(Account account) {
        List<AccountRole> accountRoleList = account.getAccountRoleList();
        if (accountRoleList != null) {
            int check = 0;
            for (int i = 0; i < accountRoleList.size(); i++) {
                if (accountRoleList.get(i).getRole().getName().equals("manager")) {
                    check++;
                }
            }
            return check != 0;
        } else {
            return false;
        }
    }
}
