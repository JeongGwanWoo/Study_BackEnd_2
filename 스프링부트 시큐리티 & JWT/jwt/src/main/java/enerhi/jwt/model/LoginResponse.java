package enerhi.jwt.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoginResponse {
    private boolean success;
    private String token;

    public LoginResponse(boolean success, String token) {
        this.success = success;
        this.token = token;
    }
}
