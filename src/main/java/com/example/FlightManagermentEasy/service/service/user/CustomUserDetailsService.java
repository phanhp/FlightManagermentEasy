package com.example.FlightManagermentEasy.service.service.user;

import com.example.FlightManagermentEasy.entity.user.Account;
import com.example.FlightManagermentEasy.repository.user.user.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findAccountByAccountName(username);
        if (account == null) {
            account = accountRepository.findAccountByEmail(username);
        }
        if (account == null) {
            account = accountRepository.findAccountByIdentity(username);
        }
        if (account == null) {
            throw new UsernameNotFoundException("User " + username + " not found");
        }
        return new CustomUserDetails(account);
    }
}
