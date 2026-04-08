package com.example.PROTOTYPE2;

import com.example.PROTOTYPE2.dto.request.SignupRequest;
import com.example.PROTOTYPE2.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class Prototype2ApplicationTests {

	@Autowired
	private AuthService authService;

	@Test
	void testSignup() {
	}

}
