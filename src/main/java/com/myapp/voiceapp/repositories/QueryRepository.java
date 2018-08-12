package com.myapp.voiceapp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.myapp.voiceapp.models.Query;
 
 
public interface QueryRepository extends JpaRepository<Query, Integer> {
	
	@org.springframework.data.jpa.repository.Query("select q from Query q join q.categories c where c.uid IN :categoryIds")
	List<Query> findByCategories(@Param("categoryIds") List<Integer> categoryIds);
	
}
