package com.example.PROTOTYPE2.auth.repository;

import com.example.PROTOTYPE2.auth.entity.Researcher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResearcherRepository extends JpaRepository<Researcher, Integer> {
    Optional<Researcher> findByEmail(String email);
    boolean existsByEmail(String email);
}
