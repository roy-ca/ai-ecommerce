package com.roy.ecommerce.service;

import com.roy.ecommerce.dto.LoginRequest;
import com.roy.ecommerce.exception.UserAlreadyExistsException;
import com.roy.ecommerce.model.User;
import com.roy.ecommerce.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(User user) {
        if(userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw  new UserAlreadyExistsException("Email already registered");
        }

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        return userRepository.save(user);
    }

    public String login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isPasswordValid = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());

        if(!isPasswordValid) {
            throw new RuntimeException("Invalid Password");
        }

        return "Login Successful";
    }
}
