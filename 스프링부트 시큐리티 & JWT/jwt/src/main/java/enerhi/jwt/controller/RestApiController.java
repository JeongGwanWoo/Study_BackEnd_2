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
import enerhi.jwt.model.UserProfile;
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
        System.out.println("로그인 시도중@@@@@@@@@@@@@@@@");

        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userLoginRequest.getUsername(), userLoginRequest.getPassword());

            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            System.out.println("로그인 완료됨: " + principalDetails.getUser().getUsername()); // 로그인 정상적으로 되었다는 뜻.

            String jwtToken = createJwtToken(authentication);
            LoginResponse loginResponse = new LoginResponse(true, jwtToken);
            System.out.println("jwtToken = Bearer " + jwtToken);
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

    @GetMapping("/api/v1/user/info")
    public ResponseEntity<?> userProfile(Authentication authentication) {
        System.out.println("사용자 정보 호출 완료");
        // 로그인한 사용자 정보 가져오기 (PrincipalDetails 같은 인증 객체에서 가져올 수 있음)
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

        // 사용자 정보 생성
        UserProfile profile = new UserProfile();

        profile.setUsername(principalDetails.getUsername());

        return ResponseEntity.ok(profile);
    }

    @GetMapping("api/v1/user")
    public String user() {
        return "<h1>user</h1>";
    }

    // manager, admin 권한만 접근 가능
    @GetMapping("api/v1/manager")
    public String manager() {
        return "<h1>manager</h1>";
    }

    // admin 권한만 접근 가능
    @GetMapping("api/v1/admin")
    public String admin() {
        return "<h1>admin</h1>";
    }
}
