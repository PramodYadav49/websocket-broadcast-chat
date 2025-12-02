package com.ci.ClientNotification;

import com.ci.ClientNotification.model.User;
import com.ci.ClientNotification.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Explanation: Runs once on startup to create a default user.
    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername("user").isEmpty()) {
            User user = new User();
            user.setUsername("user");
            // Password is 'password'
            user.setPassword(passwordEncoder.encode("password"));
            user.setRoles("USER");
            userRepository.save(user);
            System.out.println("Default user created: user/password");
        }
    }
}