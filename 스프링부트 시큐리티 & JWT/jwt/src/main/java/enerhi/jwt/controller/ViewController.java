package enerhi.jwt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ViewController {

    @GetMapping({"/",""})
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {

        return "api/login";
    }

    @GetMapping("/home")
    public String home() {
        System.out.println("/home 호출 완료 @@@@@");
        return "api/home";
    }
}
