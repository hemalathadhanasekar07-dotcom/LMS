package com.project.lms.config;

import com.project.lms.security.JwtAuthenticationEntryPoint;
import com.project.lms.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final JwtAuthenticationEntryPoint entryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sess ->
                        sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(eh -> eh
                        .authenticationEntryPoint(entryPoint)
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"message\":\"Forbidden\"}");
                        })
                )
                .authorizeHttpRequests(auth -> auth

                        // Public
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/organizations").authenticated()
                        // Admin only
                        .requestMatchers(HttpMethod.POST, "/api/users").authenticated()
                        .requestMatchers("/api/users/*/approve").authenticated()
                        .requestMatchers("/api/users/*/reject").authenticated()


                        .requestMatchers(HttpMethod.GET, "/api/users")
                        .authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/users/export")
                        .authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/users/import")
                        .authenticated()
                        .requestMatchers("/api/users/*/reject")
                        .authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/users/*")
                        .authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/courses")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/courses/**")
                        .authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/courses/*/modules")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/courses/*/modules")
                        .authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/courses/**")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/courses/**")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/courses/export")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/courses/**")
                        .authenticated()
                        .anyRequest().authenticated()

                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(form -> form.disable());

        return http.build();
    }


    @Bean
    public org.springframework.security.core.userdetails.UserDetailsService userDetailsService() {
        return username -> {
            throw new org.springframework.security.core.userdetails.UsernameNotFoundException("No default users");
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }
}