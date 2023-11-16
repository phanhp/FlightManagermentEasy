package com.example.FlightManagermentEasy.service.service.user;

import com.example.FlightManagermentEasy.entity.user.Account;
import com.example.FlightManagermentEasy.entity.user.AccountRole;
import com.example.FlightManagermentEasy.entity.user.Role;
import com.example.FlightManagermentEasy.exception.InvalidDataException;
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
import java.util.HashSet;
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
                                          String profileImage, Account account) throws IOException, InvalidDataException {
        System.out.println("c1");
        if (username == null) {
            throw new InvalidDataException("UserName Must Be Filled");
        }
        System.out.println("c2");
        if (username.isEmpty()) {
            throw new InvalidDataException("UserName Must Be Filled");
        }
        System.out.println("c3");
        if (password == null) {
            throw new InvalidDataException("PassWord Must Be Filled");
        }
        System.out.println("c4");
        if (password.length() < 3) {
            throw new InvalidDataException("PassWord Must Be Longer Than 3");
        }
        System.out.println("c5");
        if (email == null) {
            throw new InvalidDataException("Email Must Be Filled");
        }
        System.out.println("c6");
        if (email.isEmpty()) {
            throw new InvalidDataException("Email Must Be Filled");
        }
        System.out.println("c7");
        if (identity == null) {
            throw new InvalidDataException("Identity Must Be Filled");
        }
        System.out.println("c8");
        if (identity.isEmpty()) {
            throw new InvalidDataException("Identity Must Be Filled");
        }
        if (account == null) {
            account = new Account();
        }
        List<Account> accountList = new ArrayList<>();
        accountList = accountRepository.findAll();

        System.out.println("1");
        String checkAccountName = account.getAccountName();
        List<String> accountNameList = accountList.stream().map(Account::getAccountName).collect(Collectors.toList());
        if (checkAccountName != null) {
            if (!checkAccountName.equals(username)) {
                if (!accountNameList.contains(username)) {
                    account.setAccountName(username);
                } else {
                    throw new InvalidDataException("Username Was Used By Another Account");
                }
            }
        } else {
            if (!accountNameList.contains(username)) {
                account.setAccountName(username);
            } else {
                throw new InvalidDataException("Username Was Used By Another Account");
            }
        }

        System.out.println("2");
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        account.setPassword(passwordEncoder.encode(password));

        account.setName(name);

        System.out.println("3");
        String checkEmail = account.getEmail();
        List<String> emailList = accountList.stream().map(Account::getEmail).collect(Collectors.toList());
        if (checkEmail != null) {
            if (!checkEmail.equals(email)) {
                if (!emailList.contains(email)) {
                    account.setEmail(email);
                } else {
                    throw new InvalidDataException("Email Was Used By Another Account");
                }
            }
        } else {
            if (!emailList.contains(email)) {
                account.setEmail(email);
            } else {
                throw new InvalidDataException("Email Was Used By Another Account");
            }
        }

        System.out.println("4");
        String checkIdentity = account.getIdentity();
        List<String> identityList = accountList.stream().map(Account::getIdentity).collect(Collectors.toList());
        if (checkIdentity != null) {
            if (!checkIdentity.equals(identity)) {
                if (!identityList.contains(identity)) {
                    account.setIdentity(identity);
                } else {
                    throw new InvalidDataException("Identity Was Used By Another Account");
                }
            }
        } else {
            if (!identityList.contains(identity)) {
                account.setIdentity(identity);
            } else {
                throw new InvalidDataException("Identity Was Used By Another Account");
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
                try {
                    byte[] imgData = Base64.getDecoder().decode(profileImage);
                    Blob image = new SerialBlob(imgData);
                    account.setImage(image);
                } catch (Exception e) {
                    throw new IOException();
                }
            }
        }
        System.out.println("save");
        accountRepository.save(account);
        if (!isUser(account)) {
            Role user = roleRepository.findRoleByName("user");
            AccountRole accountRole = new AccountRole(account, user);
            accountRoleRepository.save(accountRole);
        }
        return account;
    }

    public Account signupAndUpdateAccount(String username, String password, String name,
                                          String email, String identity, String phone,
                                          String address, String dob, String gender,
                                          MultipartFile profileImage, Account account) throws IOException, InvalidDataException {
        System.out.println("c1");
        if (username == null) {
            throw new InvalidDataException("UserName Must Be Filled");
        }
        System.out.println("c2");
        if (username.isEmpty()) {
            throw new InvalidDataException("UserName Must Be Filled");
        }
        System.out.println("c3");
        if (password == null) {
            throw new InvalidDataException("PassWord Must Be Filled");
        }
        System.out.println("c4");
        if (password.length() < 3) {
            throw new InvalidDataException("PassWord Must Be Longer Than 3");
        }
        System.out.println("c5");
        if (email == null) {
            throw new InvalidDataException("Email Must Be Filled");
        }
        System.out.println("c6");
        if (email.isEmpty()) {
            throw new InvalidDataException("Email Must Be Filled");
        }
        System.out.println("c7");
        if (identity == null) {
            throw new InvalidDataException("Identity Must Be Filled");
        }
        System.out.println("c8");
        if (identity.isEmpty()) {
            throw new InvalidDataException("Identity Must Be Filled");
        }
        if (account == null) {
            account = new Account();
        }
        List<Account> accountList = new ArrayList<>();
        accountList = accountRepository.findAll();

        System.out.println("c9");
        String checkAccountName = account.getAccountName();
        List<String> accountNameList = accountList.stream().map(Account::getAccountName).collect(Collectors.toList());
        if (checkAccountName != null) {
            if (!checkAccountName.equals(username)) {
                if (!accountNameList.contains(username)) {
                    account.setAccountName(username);
                } else {
                    throw new InvalidDataException("Username Was Used By Another Account");
                }
            }
        } else {
            if (!accountNameList.contains(username)) {
                account.setAccountName(username);
            } else {
                throw new InvalidDataException("Username Was Used By Another Account");
            }
        }

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        account.setPassword(passwordEncoder.encode(password));

        account.setName(name);

        System.out.println("c10");
        String checkEmail = account.getEmail();
        List<String> emailList = accountList.stream().map(Account::getEmail).collect(Collectors.toList());
        if (checkEmail != null) {
            if (!checkEmail.equals(email)) {
                if (!emailList.contains(email)) {
                    account.setEmail(email);
                } else {
                    throw new InvalidDataException("Email Was Used By Another Account");
                }
            }
        } else {
            if (!emailList.contains(email)) {
                account.setEmail(email);
            } else {
                throw new InvalidDataException("Email Was Used By Another Account");
            }
        }

        System.out.println("c11");
        String checkIdentity = account.getIdentity();
        List<String> identityList = accountList.stream().map(Account::getIdentity).collect(Collectors.toList());
        if (checkIdentity != null) {
            if (!checkIdentity.equals(identity)) {
                if (!identityList.contains(identity)) {
                    account.setIdentity(identity);
                } else {
                    throw new InvalidDataException("Identity Was Used By Another Account");
                }
            }
        } else {
            if (!identityList.contains(identity)) {
                account.setIdentity(identity);
            } else {
                throw new InvalidDataException("Identity Was Used By Another Account");
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

    public String[] getInformationFromEmail(String email) throws InvalidDataException {
        if (email == null) {
            throw new InvalidDataException("Email Can Not Be Empty");
        }
        if (email.isEmpty()) {
            throw new InvalidDataException("Email Can Not Be Empty");
        }
        Account account = accountRepository.findAccountByEmail(email);
        if (account == null) {
            throw new InvalidDataException("Can Not Find Email From This Account, Input Another Email");
        }
        String newPassword = muf.generateRandomKeyInSet(new HashSet<>(), 10);
        String accountName = account.getAccountName();
        String name = account.getName();
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
        String[] inforArray = new String[3];
        inforArray[0] = accountName;
        inforArray[1] = name;
        inforArray[2] = newPassword;
        return inforArray;
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

    //******************* REMOVE AUTHORITY **********************************
    public void removeManager(Account admin, Account manager) throws InvalidDataException {
        if (admin == null) {
            throw new InvalidDataException("Only Admin Can Use This Function");
        }
        if (!isAdmin(admin)) {
            throw new InvalidDataException("Only Admin Can Use This Function");
        }
        if (manager == null) {
            throw new InvalidDataException("Manager Account Can Not Be Found");
        }
        if (!isManager(manager)) {
            throw new InvalidDataException("This Funcation Is Use For Remove Authority Of Manager, Can Not Be Used For Other Account");
        }
        List<AccountRole> accountRoleList = manager.getAccountRoleList();
        for (int i = 0; i < accountRoleList.size(); i++) {
            if (accountRoleList.get(i).getRole().getName().equals("manager")) {
                accountRoleRepository.delete(accountRoleList.get(i));
            }
        }
    }

    public String removeManagerResult(Account admin, Account manager) {
        try {
            removeManager(admin, manager);
            return "Manager Remove Successfully";
        } catch (InvalidDataException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public String removeManagerResult(long adminId, long managerId) {
        Account admin = accountRepository.findById(adminId).orElse(null);
        Account manager = accountRepository.findById(managerId).orElse(null);
        return removeManagerResult(admin, manager);
    }

}
