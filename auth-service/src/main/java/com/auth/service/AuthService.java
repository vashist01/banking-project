package com.auth.service;

import com.auth.dto.request.AuthRequest;
import com.auth.dto.request.RegisterRequest;
import com.auth.dto.request.UserContext;
import com.auth.dto.response.AuthResponse;
import com.auth.entity.User;
import com.auth.exception.BusinessException;
import com.auth.exception.UnauthorizedException;
import com.auth.repository.UserRepository;
import com.auth.utils.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final  JwtUtil jwtUtil;
    private final RedisService redisService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse registerUser(@Valid RegisterRequest request) {
        User user = userService.registerUser(request);
        return AuthResponse.builder().email(user.getEmail()).password(user.getPassword()).build();
    }

    public AuthResponse login(@Valid AuthRequest request) {
        try{
            User user = userService.getUserByEmail(request.getEmail());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            userService.updateLoginAttempts(user.getEmail(),true);

            Set<String> roles = new HashSet<>(user.getRoles());
            String token = jwtUtil.generateToken(user.getEmail(),roles,user.getId());
            redisService.storeToken(token,user.getEmail());
            return AuthResponse.builder().accessToken(token).roles(roles).build();

        }catch (Exception exception){
            log.error("Error: Failed to login due to :{}",exception);
            throw new UnauthorizedException("Internal Service is down.");
        }

    }

    public AuthResponse registerAdmin(String username, String password, String email) {
        User user = userService.registerAmin(username,password,email);

        return AuthResponse.builder().email(user.getEmail()).password(password
        ).roles(user.getRoles()).build();
    }

    public boolean validateToken(String token) {

        try{
            if(redisService.isTokenBlackListed(token)){ // attacker will try to use existing token
                log.warn("Token is blacklisted: {}", token);
                return false;
            }

            boolean isValidToken = jwtUtil.isTokenValid(token);
            if(isValidToken){
                //Refresh Token expiration in redis
                String email = jwtUtil.extractUserName(token);
                redisService.storeToken(token,email);
            }
            return isValidToken;
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }

    }

    public UserContext getUserContextFromToken(String token) {
        if(!validateToken(token)){
            throw new UnauthorizedException("Invalid token","1001");
        }
        String email = jwtUtil.extractUserName(token);
        String userId = jwtUtil.extractUserId(token);
        Set<String> roles = jwtUtil.getUserRoles(token);
       User user = userRepository.findById(Long.valueOf(userId)).orElseThrow(() -> new BusinessException("User not found.","4004"));

        return UserContext.builder().userId(user.getId()).username(user.getUsername()).
        email(email).roles(roles).customerId(user.getCustomerId()).build();
    }

    public void changePassword(String token, String oldPassword, String newPassword) {
        String email = jwtUtil.extractUserName(token);
        userService.changePassword(email,oldPassword,newPassword);

    }

    @Transactional
    public void logout(String token) {
        long expiration = redisService.getTokenExpiration(token);
        redisService.blackListToken(token,expiration);
        log.info("User logged out successfully");
    }

    public void assignCustomerId(String email, String customerId) {
        userService.assignCustomerId(email,customerId);
    }
}
