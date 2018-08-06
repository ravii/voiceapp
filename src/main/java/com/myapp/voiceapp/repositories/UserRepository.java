package com.myapp.voiceapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.myapp.voiceapp.models.User;

public interface UserRepository extends JpaRepository<User, Integer> {

    public User findByEmail(String email);
}
