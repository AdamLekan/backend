//package pl.com.api.service;
//
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Service;
//import pl.com.api.model.AuthResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//
//@Service
//public class AuthService {
//    private final AuthenticationManager authenticationManager;
//    private final JwtTokenProvider tokenProvider;
//    private final CustomUserDetailsService userDetailsService;
//
//    @Autowired
//    public AuthService(
//            AuthenticationManager authenticationManager,
//            JwtTokenProvider tokenProvider,
//            CustomUserDetailsService userDetailsService) {
//        this.authenticationManager = authenticationManager;
//        this.tokenProvider = tokenProvider;
//        this.userDetailsService = userDetailsService;
//    }
//
//    public AuthResponse login(String username, String password) {
//        Authentication authentication = authenticationManager.authenticate(
//            new UsernamePasswordAuthenticationToken(username, password)
//        );
//
//        String token = tokenProvider.createToken(authentication);
//
//        return AuthResponse.create(token, tokenProvider.getExpirationTime());
//    }
//
//    public AuthResponse refreshToken(String token) {
//        if (!tokenProvider.validateToken(token)) {
//            throw new RuntimeException("Token jest nieprawidłowy lub wygasł");
//        }
//
//        String username = tokenProvider.getUsernameFromToken(token);
//        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//        String newToken = tokenProvider.generateToken(userDetails.getUsername());
//
//        return AuthResponse.create(newToken, tokenProvider.getExpirationTime());
//    }
//}