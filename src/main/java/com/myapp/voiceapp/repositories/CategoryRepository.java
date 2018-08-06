package com.myapp.voiceapp.repositories;

import com.myapp.voiceapp.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
