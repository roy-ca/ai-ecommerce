package com.roy.ecommerce.service;

import com.roy.ecommerce.dto.LoginRequest;
import com.roy.ecommerce.exception.UserAlreadyExistsException;
import com.roy.ecommerce.model.Role;
import com.roy.ecommerce.model.User;
import com.roy.ecommerce.repository.UserRepository;
import com.roy.ecommerce.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public User register(User user) {
        if(userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw  new UserAlreadyExistsException("Email already registered");
        }

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setRole(Role.USER);
        return userRepository.save(user);
    }

    public String login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isPasswordValid = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());

        if(!isPasswordValid) {
            throw new RuntimeException("Invalid Password");
        }

        return jwtUtil.generateToken(user.getEmail());
    }
}
