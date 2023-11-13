package com.example.FlightManagermentEasy.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    UserDetailsService userDetailsService;

    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(new BCryptPasswordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf((csrf)->csrf
                        .disable())
                .authorizeHttpRequests((request) -> request
                        .requestMatchers("/","/css/**","/js/**","/images/**","/files/**","/flight-management/**","/test","/ajax/**","/**").permitAll()
                        .requestMatchers("/admin/**").hasAuthority("admin")
                        .requestMatchers("/manager/**").hasAnyAuthority("admin", "manager")
                        .requestMatchers("/user/**").hasAnyAuthority("user", "admin", "manager")
                        .anyRequest()
                        .authenticated())
                .formLogin((login) -> login
                        .loginPage("/login-page")
                        .loginProcessingUrl("/j_spring_security_check")
                        .defaultSuccessUrl("/login-success", true)
                        .failureUrl("/login-fail")
                        .permitAll())
                .exceptionHandling((exception)-> exception
                        .accessDeniedPage("/flight-management/my403-page"))
                .logout((logout) -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
