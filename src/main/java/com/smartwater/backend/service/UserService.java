package com.smartwater.backend.service;

import com.smartwater.backend.model.User;
import com.smartwater.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;


    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User registerUser(User user){

        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if(existingUser.isPresent()){
            throw new RuntimeException("Email already registered");

        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User loginUser(String email, String passward){
        Optional<User> userOptional = userRepository.findByEmail(email);

        if(userOptional.isPresent()){
            User user = userOptional.get();

            if(passwordEncoder.matches(passward, user.getPassword())){
                return user;
            }else{
                throw new RuntimeException("Invalid password");
            }
        }else{
            throw new RuntimeException("User not found!");
        }

    }

    public User updateUser(User user){
        return userRepository.save(user);
    }
}
