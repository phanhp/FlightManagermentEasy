package com.example.FlightManagermentEasy.service.data.person;

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

import java.util.List;

@Service
public class AccountData {
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    AccountRoleRepository accountRoleRepository;
    @Autowired
    MUF muf;

    public void generateAccountData(){
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        List<String> roleList = muf.toArrayString("user, admin, manager");
        List<String> accountList = muf.toArrayString("user, admin, manager");
        for (int i=0; i< roleList.size(); i++){
            Role role = new Role(roleList.get(i));
            Account account = new Account(accountList.get(i),passwordEncoder.encode("123") );
            roleRepository.save(role);
            accountRepository.save(account);
            AccountRole accountRole = new AccountRole(account, role);
            accountRoleRepository.save(accountRole);
        }
    }
}
