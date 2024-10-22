package enerhi.jwt.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import enerhi.jwt.config.auth.PrincipalDetails;
import enerhi.jwt.config.auth.jwt.JwtAuthenticationFilter;
import enerhi.jwt.config.auth.jwt.JwtProperties;
import enerhi.jwt.config.repository.UserRepository;
import enerhi.jwt.model.LoginResponse;
import enerhi.jwt.model.User;
import enerhi.jwt.model.UserLoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;

@RestController
@RequiredArgsConstructor
public class RestApiController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManager authenticationManager;

    @PostMapping("token")
    public String token() {
        return "<h1>token</h1>";
    }

    @PostMapping("login")
    public ResponseEntity<LoginResponse> login(@RequestBody UserLoginRequest userLoginRequest) {
        System.out.println("JwtAuthenticationFilter: 로그인 시도중@@@@@@@@@@@@@@@@");

        // 1. username, password 받아서
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userLoginRequest.getUsername(), userLoginRequest.getPassword());

            // PrincipalDetailsService의 loadUserByUsername() 함수가 실행된 후 정상이면 authentication이 리턴됨.
            // DB에 있는 username과 password가 일치한다.
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            System.out.println("로그인 완료됨: " + principalDetails.getUser().getUsername()); // 로그인 정상적으로 되었다는 뜻.

            LoginResponse loginResponse = new LoginResponse(true, createJwtToken(authentication));
            return ResponseEntity.ok(loginResponse);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse(false, "Invalid credentials"));
        }
    }

    private String createJwtToken(Authentication authentication) {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        return JWT.create()
                .withSubject(principalDetails.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + (JwtProperties.EXPIRATION_TIME)))
                .withClaim("id", principalDetails.getUser().getId())
                .withClaim("username", principalDetails.getUser().getUsername())
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));
    }

    @PostMapping("join")
    public String join(@RequestBody User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword())); // 1234ppp -> ABC33333
        user.setRoles("USER");
        userRepository.save(user);
        return "회원가입완료";
    }

    @GetMapping("api/v1/user")
    public ResponseEntity<String> user() {
        System.out.println("user 접근 진행중!!!!!!!!!!!!!!!!!!");
        return ResponseEntity.ok("<h1>user</h1>");
    }

    // manager, admin 권한만 접근 가능
    @GetMapping("api/v1/manager")
    public String manager() {
        return "<h1>manager</h1>";
    }

    // admin 권하만 접근 가능
    @GetMapping("api/v1/admin")
    public String admin() {
        return "<h1>admin</h1>";
    }
}
