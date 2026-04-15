package com.example.PROTOTYPE2.auth.service;

import com.example.PROTOTYPE2.auth.dto.request.LoginRequest;
import com.example.PROTOTYPE2.auth.dto.request.SignupRequest;
import com.example.PROTOTYPE2.auth.dto.response.AuthResponse;
import com.example.PROTOTYPE2.auth.entity.Researcher;
import com.example.PROTOTYPE2.auth.repository.ResearcherRepository;
import com.example.PROTOTYPE2.shared.security.JwtUtils;
import com.example.PROTOTYPE2.shared.security.ResearcherDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;

@Service
public class AuthService {

    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private ResearcherRepository researcherRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtils jwtUtils;

    /**
     * Checks if the domain of the given email has MX (mail exchange) records,
     * meaning it is configured to receive emails.
     *
     * @param email the email address to check
     * @return true if the domain has MX records, false otherwise
     */
    private boolean hasMxRecord(String email) {
        try {
            String domain = email.substring(email.indexOf('@') + 1);
            Hashtable<String, String> env = new Hashtable<>();
            env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
            DirContext ctx = new InitialDirContext(env);
            Attributes attrs = ctx.getAttributes(domain, new String[]{"MX"});
            return attrs.get("MX") != null;
        } catch (Exception e) {
            return false;
        }
    }

    public String signup(SignupRequest request) {
        if (!hasMxRecord(request.getEmail())) {
            throw new IllegalStateException("Email domain does not exist.");
        }
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
