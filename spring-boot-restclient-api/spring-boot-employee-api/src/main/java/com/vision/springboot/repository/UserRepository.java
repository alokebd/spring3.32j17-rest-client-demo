package com.vision.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vision.springboot.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
    