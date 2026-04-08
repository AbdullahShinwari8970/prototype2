package com.example.PROTOTYPE2.service;

import com.example.PROTOTYPE2.dto.request.LoginRequest;
import com.example.PROTOTYPE2.dto.request.SignupRequest;
import com.example.PROTOTYPE2.dto.response.AuthResponse;
import com.example.PROTOTYPE2.entity.Researcher;
import com.example.PROTOTYPE2.repository.ResearcherRepository;
import com.example.PROTOTYPE2.security.JwtUtils;
import com.example.PROTOTYPE2.security.ResearcherDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private ResearcherRepository researcherRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtils jwtUtils;


    /**
     * Registers a new researcher account.
     *
     * Accepts a signup request containing the researcher's email, password, and name.
     * Validates that the email is not already associated with an existing account,
     * hashes the provided password using BCrypt, persists the new researcher to the
     * database, and returns a signed JWT token upon successful registration.
     *
     * @param request the signup request containing email, password, and name
     * @return a signed JWT token for the newly registered researcher
     */
    public String signup(SignupRequest request) {
        if (researcherRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("Email already registered.");
        }
        Researcher researcher = Researcher.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .build();
        researcherRepository.save(researcher);
        return "Researcher registered successfully.";
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateToken(authentication);
        ResearcherDetailsImpl principal = (ResearcherDetailsImpl) authentication.getPrincipal();
        return new AuthResponse(jwt, principal.getId(), principal.getUsername(), principal.getName());
    }

}
