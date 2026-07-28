package com.auth.service;

import com.auth.dto.request.RegisterRequest;
import com.auth.entity.User;
import com.auth.exception.BusinessException;
import com.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Transactional
    public void unlockAccount(String email) {
        User user = getUserByEmail(email);
        user.setAccountNonLocked(true);
        user.setFailedAttempts(0);
        user.setLockTime(null);
        userRepository.save(user);

        log.info("User account unlocked: {}", email);
    }
    @Transactional
    public User registerUser( RegisterRequest request) {

        if(userRepository.existsByEmail(request.getEmail())){
            throw new BusinessException("Email already registered", "EMAIL_EXISTS");
        }
        User user = User.builder().email(request.getEmail()).firstName(request.getFirstName())
                .lastName(request.getLastName()).password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber()).username(request.getUsername()).enabled(true)
                .roles(Set.of("USER")).build();
        userRepository.save(user);
        return user;
    }


    @Transactional
    @CachePut(value = "user",key = "#email")
    public void updateLoginAttempts( String email, boolean success) {
        User user = getUserByEmail(email);
        LocalDateTime localDateTime =LocalDateTime.now(ZoneId.systemDefault());
        if(success){
            user.setFailedAttempts(0);
            user.setLockTime(null);
            user.setLastLogin(localDateTime);
        }else {
            user.setFailedAttempts(user.getFailedAttempts()+1);
            if(user.getFailedAttempts() >=5){
                user.setLockTime(localDateTime);
                user.setAccountNonLocked(false);
            }
            userRepository.save(user);
        }
    }

    @Cacheable(value = "users",key = "#email")
    public User getUserByEmail(String email) {
        System.out.println("######## METHOD EXECUTED ########");
        return  userRepository.findByEmail(email).
                orElseThrow(() -> new BusinessException("User not found","4001"));
    }

    @Transactional
    public User registerAmin(String username, String password, String email) {
        User user = User.builder().email(email).
              password(passwordEncoder.encode(password))
                 .enabled(true).username(username)
                .roles(Set.of("USER","ADMIN")).build();
        userRepository.save(user);
        return user;
    }
    @Transactional
    @CacheEvict(value = "user",key = "#user.email")
    public void changePassword(String email, String oldPassword, String newPassword) {
        User user = getUserByEmail(email);
        if(!passwordEncoder.matches(oldPassword,user.getPassword())){
            throw new BusinessException("Invalid old password", "INVALID_PASSWORD");
        }
        user.setPassword(newPassword);
        userRepository.save(user);
    }
    @Transactional
    public void assignCustomerId(String email, String customerId) {
        User user = getUserByEmail(email);
        user.setCustomerId(customerId);
        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return getUserByEmail(email);
    }
}
