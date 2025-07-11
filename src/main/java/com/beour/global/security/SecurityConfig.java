package com.beour.global.security;

import com.beour.global.jwt.CustomLogoutFilter;
import com.beour.global.jwt.JWTFilter;
import com.beour.global.jwt.JWTUtil;
import com.beour.global.jwt.LoginFilter;
import com.beour.token.repository.RefreshTokenRepository;
import com.beour.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        AuthenticationManager authenticationManager = authenticationManager();

        LoginFilter loginFilter = new LoginFilter(authenticationManager, userRepository, jwtUtil,
            refreshTokenRepository);
        loginFilter.setFilterProcessesUrl("/api/users/login");

        http.cors((cors) -> cors
            .configurationSource(new CorsConfigurationSource() {
                @Override
                public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                    CorsConfiguration configuration = new CorsConfiguration();

                    configuration.setAllowedOrigins(
                        Collections.singletonList("http://localhost:3000")
                    );
                    configuration.setAllowedMethods(Collections.singletonList("*"));
                    configuration.setAllowCredentials(true);
                    configuration.setAllowedHeaders(Collections.singletonList("*"));
                    configuration.setMaxAge(3600L);

                    configuration.setExposedHeaders(Collections.singletonList("Authorization"));

                    return configuration;
                }
            })
        );

        http
            .authorizeHttpRequests((auth) -> auth
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                    .requestMatchers("/api/users/**").permitAll()
                    .requestMatchers("/api/spaces/reserve/available-times", "/api/spaces/search/**",
                        "/api/spaces/new", "/api/reviews/new", "/api/banners").permitAll()
                    .requestMatchers("/admin").hasRole("ADMIN")
                    .requestMatchers("/api/spaces/reserve", "/api/reservation/**", "/api/guest/**")
                    .hasRole("GUEST")
                    .requestMatchers("/api/spaces", "/api/spaces/my-spaces", "/api/spaces/*", "/api/spaces/*/*",
                            "/api/host/available-times/spaces",  "/api/host/available-times/space/*").hasRole("HOST")

                    .requestMatchers("/api/mypage/**").hasAnyRole("HOST", "GUEST")
                    .requestMatchers("/logout").hasAnyRole("HOST", "GUEST", "ADMIN")
                    .anyRequest().authenticated()
//                .anyRequest().permitAll();
            );

        http
            .addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class)
            .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshTokenRepository),
                LogoutFilter.class);

        http
            .formLogin((auth) -> auth.disable())
            .csrf((auth) -> auth.disable())
            .httpBasic((auth) -> auth.disable())
            .sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}