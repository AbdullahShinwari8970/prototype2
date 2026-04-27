package com.example.PROTOTYPE2.auth.repository;

import com.example.PROTOTYPE2.auth.entity.Researcher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
//Spring scans for @Repositary at start up and registers it in the application context,-
// -So it can be injected with @Autowired or constructor infection anywhere.
@Repository
public interface ResearcherRepository extends JpaRepository<Researcher, Integer> {
    Optional<Researcher> findByEmail(String email); //By the Method name, spring just reads the method and generates SQL automatically.
    boolean existsByEmail(String email);
}


//JpaRepository<Researcher, Integer> gives you all standard database operations for free without writing any SQL:
    //save() —> insert or update
    //findById() —> get by primary key
    //findAll() —> get everything
    //deleteById() —> delete by primary key
    //existsById() —> check if exists


//The Repository is just the database access layer
    //Services import it and calls its methods.