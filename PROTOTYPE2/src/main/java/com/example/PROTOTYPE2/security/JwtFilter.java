package com.example.PROTOTYPE2.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * HTTP request filter responsible for JWT authentication on every incoming request.
 *
 * This filter intercepts each HTTP request before it reaches any controller,
 * extracts the JWT token from the Authorization header, and delegates token
 * validation to JwtUtil. If the token is valid, the authenticated researcher
 * is set in Spring's SecurityContext, allowing the request to proceed.
 * If the token is missing, malformed, or invalid, the request is rejected
 * with a 401 Unauthorized response.
 *
 * This class has no knowledge of how tokens are built or signed: it only
 * concerns itself with reading the token from the request and acting on
 * the result of validation.
 *
 * Registered into the Spring Security filter chain via SecurityConfig,
 * running once per request before the UsernamePasswordAuthenticationFilter.
 *
 * Request flow:
 *   Incoming request
 *         ↓
 *   JwtFilter — reads Authorization header, extracts token
 *         ↓
 *   JwtUtil — validates token, extracts researcher_id
 *         ↓
 *   JwtFilter — sets authenticated researcher in SecurityContext
 *         ↓
 *   Request proceeds to Controller
 */
@Component
public class JwtFilter extends OncePerRequestFilter {
//OncePerRequestFilter is a Spring class that guarantees your filter runs once
// And only once per request.

    @Autowired
    private JwtUtils jwtUtils; // instance, lowercase

    @Autowired
    private ResearcherDetailsServiceImpl userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    //request is all the information from the client coming from the frontend (HTTP REQ)
        //response is the HTTP Response that will be sent back to the client,
            //filterchain is the queue of remaining filters and eventually the controller.

        try {
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtils.validateToken(jwt)) {
                String email = jwtUtils.getEmailFromToken(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e.getMessage());
        }

        // at the end, always call this to pass the request to the next filter
        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}

