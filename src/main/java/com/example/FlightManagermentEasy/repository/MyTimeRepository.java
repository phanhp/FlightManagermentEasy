package com.example.FlightManagermentEasy.repository;

import com.example.FlightManagermentEasy.entity.MyTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MyTimeRepository extends JpaRepository<MyTime, Long> {

}
