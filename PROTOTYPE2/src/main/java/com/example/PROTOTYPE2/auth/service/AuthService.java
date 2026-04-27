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
            String domain = email.substring(email.indexOf('@') + 1); //extracts the domain from the email.
            Hashtable<String, String> env = new Hashtable<>();
            env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
            DirContext ctx = new InitialDirContext(env);
            Attributes attrs = ctx.getAttributes(domain, new String[]{"MX"}); //DNS client using java built in JNDI Libary
            return attrs.get("MX") != null;
        } catch (Exception e) {
            return false;
        }
    }

    public String signup(SignupRequest request) {
        if (!hasMxRecord(request.getEmail())) { //1. Does email domain exist?
            throw new IllegalStateException("Email domain does not exist.");
        }
        if (researcherRepository.existsByEmail(request.getEmail())) { //2. Does email already registered?
            throw new IllegalStateException("Email already registered.");
        }
        Researcher researcher = Researcher.builder() //3. Build the entity
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword())) //4. hashing the password.
                .name(request.getName())
                .build();
        researcherRepository.save(researcher); //5. insert into database.
        return "Researcher registered successfully.";
    }

    public AuthResponse login(LoginRequest request) {
        // 1. Delegate authentication to Spring Security.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        // 2. Store the authenticated user in the security context for this request.
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Generate a signed JWT token from the authenticated user.
        //    The token encodes the researcher's email and expiry, signed with the secret key.
        String jwt = jwtUtils.generateToken(authentication);

        // 4. Cast the principal to ResearcherDetailsImpl to access researcher-specific
        //    fields (id, name) not available on the base UserDetails interface.
        ResearcherDetailsImpl principal = (ResearcherDetailsImpl) authentication.getPrincipal();

        // 5. Return the JWT + researcher details to the frontend.
        return new AuthResponse(jwt, principal.getId(), principal.getUsername(), principal.getName());
    }
}
