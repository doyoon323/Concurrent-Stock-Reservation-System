package com.example.reservation_system.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.reservation_system.dto.request.UserSignUpRequest;
import com.example.reservation_system.dto.response.UserResponse;
import com.example.reservation_system.service.UserService;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import java.security.Key;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor //생성자를 알아서 만들어줌 . this.__ = 이런거 
public class UserController {
    
    private final UserService userService;

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    @PostMapping("/api/signup")
    public ResponseEntity<?> signUp(@Validated @RequestBody UserSignUpRequest signUpDto) {// @RequestBody: 프론트의 json데이터를 dto로 변환 
        UserResponse response = userService.createUser(signUpDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/api/login")
    // 반환 타입을 String에서 ResponseEntity<?>로 변경 (JSON 응답을 위해)
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        Long userId = userService.login(loginData.get("loginId"), loginData.get("password"));

        // 2. jwtProvider.createToken(userId) 대신 여기서 직접 생성
        String token = io.jsonwebtoken.Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(new java.util.Date())
                .setExpiration(new java.util.Date(System.currentTimeMillis() + 86400000)) // 24시간
                .signWith(key)
                .compact();

        // 3. 토큰과 유저 ID를 JSON으로 반환
        return ResponseEntity.ok(Map.of(
            "accessToken", token, 
            "userId", userId
        ));
    }

    @GetMapping("/api/me")
    public String getMe(HttpSession session) {
        Long userId = (Long) session.getAttribute("USER_ID");
        
        if (userId == null) {
            return "로그인이 필요합니다!";
        }
        return "현재 로그인한 유저 ID는: " + userId;
    }

    
}


