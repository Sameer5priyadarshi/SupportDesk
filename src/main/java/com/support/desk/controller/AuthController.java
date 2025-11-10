package com.support.desk.controller;

import com.support.desk.dto.JwtRequest;
import com.support.desk.dto.JwtResponse;
import com.support.desk.dto.UserRegistrationDTO;
import com.support.desk.service.AuthService;
import com.support.desk.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest jwtRequest) {
        return new ResponseEntity<>(authService.login(jwtRequest), HttpStatus.OK);
    }

    @PostMapping("/register/customer")
    public ResponseEntity<?> registerCustomer(@Valid @RequestBody UserRegistrationDTO registrationDto) {
        userService.registerUser(registrationDto, "ROLE_CUSTOMER");
        return ResponseEntity.ok().body("User registered successfully!");
    }

    @PostMapping("/register/employee")
    public ResponseEntity<?> registerEmployee(@Valid @RequestBody UserRegistrationDTO registrationDto) {
        userService.registerUser(registrationDto, "ROLE_EMPLOYEE");
        return ResponseEntity.ok().body("Employee registered successfully!");
    }

    @PostMapping("/register/admin")
    public ResponseEntity<?> registerAdmin(@Valid @RequestBody UserRegistrationDTO registrationDto) {
        userService.registerUser(registrationDto, "ROLE_ADMIN");
        return ResponseEntity.ok().body("Admin registered successfully!");
    }
}
