package pl.com.api.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String token;
    private String type;
    private long expiresIn;

    private AuthResponse(String token, String type, long expiresIn) {
        this.token = token;
        this.type = type;
        this.expiresIn = expiresIn;
    }
    public static AuthResponse create(String token, long expiresIn) {
        return new AuthResponse(token, "Bearer", expiresIn);
    }

}