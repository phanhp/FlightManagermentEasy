package com.example.FlightManagermentEasy.repository.user.bank;

import com.example.FlightManagermentEasy.entity.user.bank.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    BankAccount findBankAccountByName(String name);

}
