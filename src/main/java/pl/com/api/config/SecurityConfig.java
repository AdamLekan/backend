package pl.com.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthFilter jwtAuthFilter,
                                                   PasswordEncoder passwordEncoder,
                                                   UserDetailsService userDetailsService) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(c -> c.configurationSource(corsConfigurationSource()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 1. Przekieruj nieautoryzowane żądania do /login
                .exceptionHandling(e -> e.authenticationEntryPoint((request, response, authException) -> {
                    redirectToLogin(request, response);
                }))
                .authorizeHttpRequests(auth -> auth
                        // 2. Zezwól na ścieżki frontu (index.html, login, zasoby statyczne)
                        .requestMatchers("/", "/index.html", "/login", "/favicon.ico").permitAll()
                        .requestMatchers("/*.js", "/*.css", "/static/**", "/assets/**").permitAll()
                        // 3. Zezwól na endpointy uwierzytelniające
                        .requestMatchers("/auth/**", "/refresh", "/logout").permitAll()
                        // 4. Reszta wymaga autoryzacji
                        .anyRequest().authenticated()
                )
                // 5. Dodaj filtr JWT przed UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                // 6. Konfiguruj dostawcę uwierzytelnienia
                .authenticationProvider(authenticationProvider(userDetailsService, passwordEncoder));

        return http.build();
    }

    /** Metoda przekierowuje nieautoryzowane żądania do /login. */
    private void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Dla żądań JSON API możesz zamiast przekierowania zwrócić 401:
        // if (request.getHeader("Accept") != null && request.getHeader("Accept").contains("application/json")) {
        //    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        //    return;
        // }
        response.sendRedirect("/login");
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
                                                            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(List.of("http://localhost:4200"));
        cfg.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(List.of("Authorization","Content-Type"));
        cfg.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}
