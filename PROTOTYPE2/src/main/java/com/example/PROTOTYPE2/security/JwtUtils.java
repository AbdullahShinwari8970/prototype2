package com.example.PROTOTYPE2.security;


import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Utility class responsible for JWT token operations.
 *
 * This class is solely concerned with the creation, validation, and parsing
 * of JSON Web Tokens (JWT). It has no knowledge of HTTP requests or Spring
 * Security context — it purely handles token logic.
 *
 * Used by JwtFilter to validate incoming tokens on each request.
 */
@Component
public class JwtUtils {


    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    //The Line above creates the logger, I passed JwtUtils.class
    // so that when messages print to the console they are labelled with the class they came from,
    // making it easy to trace where an error originated.

    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.expiration}")
    private int jwtExpirationMs;


    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }



    /**
     * Generates a signed JWT token for an authenticated researcher.
     * <p>
     * Extracts the authenticated researcher from the Spring Security Authentication
     * object, then builds a JWT token containing the researcher's username (email)
     * as the subject claim. The token is signed using HMAC-SHA256 with the
     * application's secret key and is valid for the duration defined by
     * jwtExpirationMs. This token is returned to the frontend on successful
     * login and must be included in the Authorization header of all subsequent
     * protected requests.
     * <p>
     * Token structure:
     *   - Subject:    researcher's email (extracted from ResearcherDetailsImpl)
     *   - Issued at:  current timestamp
     *   - Expiry:     current timestamp + jwtExpirationMs
     *   - Signature:  HMAC-SHA256 signed with the application secret key
     *
     * @param authentication the Spring Security Authentication object containing
     *                       the authenticated researcher's principal
     * @return a compact, signed JWT token string in the format header.payload.signature
     */
    public String generateToken(Authentication authentication) {
        ResearcherDetailsImpl principal = (ResearcherDetailsImpl) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(principal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validates an incoming JWT token.
     *
     * Verifies that the token's signature matches the application's secret key
     * and that the token has not expired. Returns true if the token is valid,
     * false otherwise.
     *
     * @param token the JWT token string to validate
     * @return true if the token is valid and not expired, false otherwise
     */
    public boolean validateToken(String token){
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Extracts the researcher email from a validated JWT token.
     *
     * Parses the token payload and retrieves the researcher_id claim,
     * which is used to identify the currently authenticated researcher
     * on each protected request.
     *
     * @param token the JWT token string to parse
     * @return the researcher ID contained within the token payload
     */
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }
}
