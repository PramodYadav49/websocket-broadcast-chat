package com.ci.ClientNotification.controller;

import com.ci.ClientNotification.model.LoginRequest;
import com.ci.ClientNotification.model.RegisterRequest;
import com.ci.ClientNotification.model.User;
import com.ci.ClientNotification.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager,UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userService=userService;
    }
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {

        try {
            // Call the service layer method
            User registeredUser = userService.registerNewUser(
                    registerRequest.getUsername(),
                    registerRequest.getPassword(),
                    "USER" // Default role for new users
            );

            // Return a success response (e.g., HTTP 201 Created)
            return new ResponseEntity<>("User registered successfully! Username: " + registeredUser.getUsername(), HttpStatus.CREATED);

        } catch (RuntimeException e) {
            // Handle the case where the username is already taken
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/login")
    public ResponseEntity<?> authenticationUser(@RequestBody LoginRequest loginRequest){
        Authentication authentication=authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return ResponseEntity.ok("LOGIN SUCCESSFULL>Session established");







    }
}
