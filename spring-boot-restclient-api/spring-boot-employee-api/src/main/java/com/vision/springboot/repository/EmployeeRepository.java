package com.vision.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vision.springboot.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
