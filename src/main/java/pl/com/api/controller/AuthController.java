//package pl.com.api.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
////import org.springframework.security.authentication.AuthenticationManager;
//import lombok.RequiredArgsConstructor;
//import pl.com.api.model.AuthResponse;
//import pl.com.api.service.AuthService;
//import pl.com.api.service.JwtTokenProvider;
//
//@RestController
//@RequestMapping("/api/auth")
//public class AuthController {
//    private final AuthService authService;
//    private final JwtTokenProvider jwtTokenProvider;
//    @Autowired
//    public AuthController(AuthService authService, JwtTokenProvider jwtTokenProvider) {
//        this.authService = authService;
//        this.jwtTokenProvider = jwtTokenProvider;
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<AuthResponse> login(
//            @RequestParam String username,
//            @RequestParam String password) {
//        return ResponseEntity.ok(authService.login(username, password));
//    }
//
//    @PostMapping("/refresh")
//    public ResponseEntity<AuthResponse> refresh(
//            @RequestHeader("Authorization") String token) {
//        return ResponseEntity.ok(authService.refreshToken(token.substring(7)));
//    }
//}