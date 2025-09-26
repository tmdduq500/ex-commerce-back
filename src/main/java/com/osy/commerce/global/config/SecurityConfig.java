package com.osy.commerce.global.config;

import com.osy.commerce.global.security.RestAccessDeniedHandler;
import com.osy.commerce.global.security.RestAuthenticationEntryPoint;
import com.osy.commerce.global.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RestAuthenticationEntryPoint restEntryPoint;
    private final RestAccessDeniedHandler restDeniedHandler;
    @Value("${swagger.enabled:false}")
    private boolean swaggerEnabled;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                            if (swaggerEnabled) {
                                auth.requestMatchers(
                                        "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**"
                                ).permitAll();
                            } else {
                                auth.requestMatchers(
                                        "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**"
                                ).denyAll();
                            }
                            auth
                                    .requestMatchers("/api/auth/**").permitAll()
                                    .requestMatchers(HttpMethod.GET, "/api/v1/products/**", "/api/v1/categories/**").permitAll()
                                    .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                                    .requestMatchers("/actuator/**").hasRole("ADMIN")
                                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").denyAll()
                                    .anyRequest().authenticated();
                        }

                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(restEntryPoint)
                        .accessDeniedHandler(restDeniedHandler)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
