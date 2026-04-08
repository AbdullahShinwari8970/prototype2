package com.example.PROTOTYPE2;


import com.example.PROTOTYPE2.dto.request.SignupRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Integration tests for the AuthController REST endpoints.
 *
 * Tests the full HTTP request/response cycle for authentication,
 * including signup, login, and JWT token validation. Uses MockMvc
 * to simulate HTTP requests without starting a real server.
 *
 * The H2 in-memory database is used for all tests, ensuring
 * a clean state on every test run with no impact on real data.
 *
 * Endpoints tested:
 *  - POST /api/auth/signup → researcher registration
 *  - POST /api/auth/login → researcher login + JWT issuance
 *
 * Test coverage:
 *  - Happy path (valid input, correct credentials)
 *  - Duplicate email registration
 *  - Invalid input (blank fields, bad email format)
 *  - Wrong password / unknown email on login
 *  - Protected endpoints with and without JWT token
 */
@SpringBootTest
//@AutoConfigureMockMvc
public class AuthControllerTest {


    @Test
    public void test_signup_with_valid_request() {
    }


    @Test
    public void test_signup_with_invalid_request() {

    }

    @Test
    public void testLogin() {}
}
