package com.example.springbatchdemo.repository;

import com.example.springbatchdemo.entity.Pay;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.Id;

public interface PayRepository extends JpaRepository<Pay, Id> {
}
