package pl.com.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import pl.com.api.service.JwtService;
import pl.com.api.service.UserService;


import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userDetailsService;

    /**
     * Endpoint logowania - weryfikuje dane użytkownika i zwraca parę tokenów.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginReq) {
        String username = loginReq.get("username");
        String password = loginReq.get("password");
        try {
            // Uwierzytelnienie za pomocą domyślnego mechanizmu Spring Security
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (Exception ex) {
            // Błędne dane logowania
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // Uwierzytelnienie przebiegło pomyślnie - wygeneruj tokeny
        String accessToken = jwtService.generateAccessToken(username);
        String refreshToken = jwtService.generateRefreshToken(username);
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        return ResponseEntity.ok(tokens);
    }

    /**
     * Endpoint odświeżania tokenu - przyjmuje refresh token i zwraca nowy access token (oraz nowy refresh token).
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (refreshToken == null) {
            return ResponseEntity.badRequest().body("Refresh token is required");
        }
        String username;
        try {
            username = jwtService.extractUsername(refreshToken);
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            // Refresh token wygasł
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token expired");
        } catch (Exception e) {
            // Token niepoprawny
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }
        // (Opcjonalnie) można sprawdzić czy użytkownik istnieje w systemie:
        userDetailsService.loadUserByUsername(username);  // upewnienie się, że użytkownik jest prawidłowy

        // Wygeneruj nowy zestaw tokenów (można podjąć decyzję czy odświeżać refresh token czy zachować stary)
        String newAccessToken = jwtService.generateAccessToken(username);
        String newRefreshToken = jwtService.generateRefreshToken(username);
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", newAccessToken);
        tokens.put("refreshToken", newRefreshToken);
        return ResponseEntity.ok(tokens);
    }

    /**
     * Endpoint wylogowania - w razie potrzeby unieważnia tokeny (w tej wersji demo bez persystencji tokenów).
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // W prawdziwej aplikacji tutaj nastąpiłoby np. unieważnienie refresh tokena w bazie (tzw. blacklisting).
        // Ponieważ nie trzymamy stanu tokenów po stronie serwera, po prostu zwracamy 200 OK.
        return ResponseEntity.ok().build();
    }
}
