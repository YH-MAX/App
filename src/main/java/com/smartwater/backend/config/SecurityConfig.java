package com.smartwater.backend.config;

import com.smartwater.backend.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// ✅ CORS imports (important)
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                //  Enable CORS at Spring Security layer (CRITICAL for Flutter Web)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Mobile/REST API: usually disable CSRF
                .csrf(csrf -> csrf.disable())

                // Stateless JWT auth
                .sessionManagement(sess ->
                        sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth
                        // Allow ALL preflight requests (CRITICAL)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Public endpoints
                        .requestMatchers(
                                "/api/users/register",
                                "/api/users/login",
                                "/api/users/verify-email",
                                "/api/users/reset-password",
                                "/error"
                        ).permitAll()

                        // Actuator endpoints (DEV only)
                        .requestMatchers("/actuator/**").permitAll()

                        // Gateway endpoints (TEMP allow for integration testing)
                        .requestMatchers("/api/water/**").permitAll()

                        // Public device sensor data for Dashboard live updates
                        .requestMatchers("/api/sensor/device/**").permitAll()

                        // ✅ Community: Allow GET (view posts) without auth, require auth for POST/PUT/DELETE
                        .requestMatchers(HttpMethod.GET, "/api/community/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/community/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/community/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/community/**").authenticated()

                        // ✅ Reports: Allow GET (view reports) without auth, require auth for POST/PUT/DELETE
                        .requestMatchers(HttpMethod.GET, "/api/reports/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/reports/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/reports/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/reports/**").authenticated()

                        // Protected modules (user-specific sensor data still requires auth)
                        .requestMatchers("/api/sensor/me/**").authenticated()
                        .requestMatchers("/api/alerts/**").authenticated()
                        .requestMatchers("/api/users/*/follow").authenticated()
                        .requestMatchers("/api/users/*/followers").authenticated()
                        .requestMatchers("/api/users/*/following").authenticated()
                        .requestMatchers("/api/users/*/profile").authenticated()
                        .requestMatchers("/api/users/me/**").authenticated()

                        // Everything else requires auth
                        .anyRequest().authenticated()
                )

                // JWT filter before username/password filter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS configuration used by Spring Security.
     * For development/demo: allow all origins (safe for local testing).
     * In production: restrict allowed origins to your trusted domains.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Dev mode: allow any localhost port + any origin
        // If your Spring Boot version complains, keep allowedOriginPatterns.
        config.setAllowedOriginPatterns(List.of("*"));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));

        // If you don't use cookies/session from browser, keep false
        config.setAllowCredentials(false);

        // Cache preflight result (seconds)
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
