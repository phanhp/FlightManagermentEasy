package com.example.FlightManagermentEasy.repository;

import com.example.FlightManagermentEasy.entity.MyToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MyTokenRepository extends JpaRepository<MyToken, Long> {
    MyToken findMyTokenByEncodeKey(String encodeKey);
}
