package com.myapp.voiceapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.myapp.voiceapp.models.Query;

public interface QueryRepository extends JpaRepository<Query, Integer> {
}
