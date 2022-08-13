package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.CustomerInfo;

public interface CustomerRepository extends JpaRepository<CustomerInfo, Integer> {

}
