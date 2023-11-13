package com.example.FlightManagermentEasy.repository.user.user;

import com.example.FlightManagermentEasy.entity.user.AccountRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRoleRepository extends JpaRepository<AccountRole, Long> {
}
