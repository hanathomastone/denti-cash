package com.example.dentix.domain.user.dao;

import com.example.dentix.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}