package enerhi.jwt.controller;

import enerhi.jwt.config.repository.UserRepository;
import enerhi.jwt.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class RestApiController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("home")
    public String home() {
        return "<h1>home</h1>";
    }

    @PostMapping("token")
    public String token() {
        return "<h1>token</h1>";
    }

    @PostMapping("join")
    public String join(@RequestBody User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword())); // 1234ppp -> ABC33333
        user.setRoles("USER");
        userRepository.save(user);
        return "회원가입완료";
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

    // admin 권하만 접근 가능
    @GetMapping("api/v1/admin")
    public String admin() {
        return "<h1>admin</h1>";
    }
}
