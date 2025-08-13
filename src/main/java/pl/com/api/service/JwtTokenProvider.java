//package pl.com.api.service;
//
//import io.jsonwebtoken.*;
//import jakarta.servlet.http.HttpServletRequest;
////import jakarta.xml.bind.DatatypeConverter;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
////import org.springframework.security.core.Authentication;
////import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Component;
//
//import javax.crypto.spec.SecretKeySpec;
//import java.security.Key;
//import java.util.Date;
//
//@Slf4j
//@Component
//public class JwtTokenProvider {
//
//    @Value("${jwt.secret}")
//    private String jwtSecret;
//
//    @Value("${jwt.expiration}")
//    private long jwtExpiration;
//
//    private Key getSigningKey() {
//        byte[] secretBytes = DatatypeConverter.parseBase64Binary(jwtSecret);
//        return new SecretKeySpec(secretBytes, SignatureAlgorithm.HS512.getJcaName());
//    }
//
//    public String createToken(Authentication authentication) {
//        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//        return generateToken(userDetails.getUsername());
//    }
//
//    public String generateToken(String username) {
//        Date now = new Date();
//        Date expiryDate = new Date(now.getTime() + jwtExpiration);
//
//        return Jwts.builder()
//                .setSubject(username)
//                .setIssuedAt(now)
//                .setExpiration(expiryDate)
//                .signWith(SignatureAlgorithm.HS512, getSigningKey())
//                .compact();
//    }
//
//    public String getUsernameFromToken(String token) {
//        Claims claims = Jwts.parserBuilder()
//                .setSigningKey(getSigningKey())
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//
//        return claims.getSubject();
//    }
//
//    public boolean validateToken(String token) {
//        try {
//            Jwts.parserBuilder()
//                    .setSigningKey(getSigningKey())
//                    .build()
//                    .parseClaimsJws(token);
//            return true;
//        } catch (SignatureException ex) {
////            log.error("Nieprawidłowyawidłowy podpis JWT");
//        } catch (MalformedJwtException ex) {
////            log.error("Nieprawidłowy token JWT");
//        } catch (ExpiredJwtException ex) {
////            log.error("Token JWT wygasł");
//        } catch (UnsupportedJwtException ex) {
////            log.error("Token JWT nie jest wspierany");
//        } catch (IllegalArgumentException ex) {
////            log.error("JWT claims string jest pusty");
//        }
//        return false;
//    }
//
//    public String resolveToken(HttpServletRequest request) {
//        String bearerToken = request.getHeader("Authorization");
//        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
//            return bearerToken.substring(7);
//        }
//        return null;
//    }
//
//    public long getExpirationTime() {
//        return jwtExpiration;
//    }
//}