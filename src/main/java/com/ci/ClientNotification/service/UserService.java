package com.ci.ClientNotification.service;

import com.ci.ClientNotification.model.User;
import com.ci.ClientNotification.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder=passwordEncoder;
    }
    public User registerNewUser(String username, String rawPassword, String roles) {

        // 1. Check if user already exists
        if (userRepository.findByUsername(username).isPresent()) {
            // In a real application, throw a custom exception here (e.g., UsernameAlreadyExistsException)
            throw new RuntimeException("Username '" + username + "' is already taken.");
        }

        // 2. Create the User entity
        User newUser = new User();
        newUser.setUsername(username);

        // 3. HASH THE PASSWORD before saving!
        newUser.setPassword(passwordEncoder.encode(rawPassword));

        // 4. Set roles (e.g., "USER" or "USER,ADMIN")
        newUser.setRoles(roles);

        // 5. Save the user to the database
        return userRepository.save(newUser);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
     User user=userRepository.findByUsername(username).get();
       return org.springframework.security.core.userdetails.User.builder()
               .username(user.getUsername())
               .password(user.getPassword())
               .roles(user.getRoles().split(","))
               .build();



    }
}
