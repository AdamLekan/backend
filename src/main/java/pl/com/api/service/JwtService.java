package pl.com.api.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {
    // Sekret do podpisywania JWT (32 znaki = 256 bitów dla algorytmu HS256)
    private static final String SECRET_KEY = "mysupersecretkeymysupersecretkey";
    // Czas ważności access tokena (np. 15 minut)
    private static final long ACCESS_TOKEN_EXPIRATION_MS = 15 * 60 * 1000;
    // Czas ważności refresh tokena (np. 7 dni)
    private static final long REFRESH_TOKEN_EXPIRATION_MS = 7 * 24 * 60 * 60 * 1000;

    private Key getSigningKey() {
        // Tworzy klucz HMAC z sekretu
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    /** Generuje nowy access token dla podanej nazwy użytkownika */
    public String generateAccessToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_MS))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /** Generuje nowy refresh token dla podanej nazwy użytkownika */
    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_MS))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /** Odczytuje nazwę użytkownika (subject) z tokena JWT */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /** Odczytuje datę wygaśnięcia tokena JWT */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /** Pobiera dowolny claim z tokena, używając przekazanej funkcji */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }

    /** Sprawdza, czy token JWT już wygasł */
    public boolean isTokenExpired(String token) {
        Date expiration = extractExpiration(token);
        return expiration.before(new Date());
    }

    /** Waliduje token JWT: sprawdza podpis, datę ważności i zgodność nazwy użytkownika */
    public boolean validateToken(String token, org.springframework.security.core.userdetails.UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
