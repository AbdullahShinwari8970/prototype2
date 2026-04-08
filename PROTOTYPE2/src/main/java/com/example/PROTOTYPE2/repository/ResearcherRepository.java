package com.example.PROTOTYPE2.repository;

import com.example.PROTOTYPE2.entity.Researcher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResearcherRepository extends JpaRepository<Researcher, Integer> {
    Optional<Researcher> findByEmail(String email);
    boolean existsByEmail(String email);
}