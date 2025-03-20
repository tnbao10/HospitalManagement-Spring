package com.example.demo.configurations;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.example.demo.services.AccountService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;



@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    private AccountService accountService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .cors(cors -> cors.disable())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/dist/**", "/account/login", "/account/register").permitAll()
                        .requestMatchers("/admin/**").hasAnyRole("ADMIN")
                        .requestMatchers("/doctor/**").hasAnyRole("DOCTOR")
                        .requestMatchers("/patient/**").hasAnyRole("PATIENT")
                )
                .formLogin(f -> {
                    f.loginPage("/account/login")
                     .loginProcessingUrl("/account/process-login")
                     .usernameParameter("email")
                     .passwordParameter("password")
                     .successHandler(new AuthenticationSuccessHandler() {
                         @Override
                         public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                                             Authentication authentication) throws IOException, ServletException {
                             Map<String, String> redirectUrls = new HashMap<>();
                             redirectUrls.put("ROLE_ADMIN", "/admin/doctor-management");
                             redirectUrls.put("ROLE_DOCTOR", "/doctor/profile");
                             redirectUrls.put("ROLE_PATIENT", "/patient/faculty-list");
                             String url = "";
                             for (GrantedAuthority authority : authentication.getAuthorities()) {
                                 if (redirectUrls.containsKey(authority.getAuthority())) {
                                     url = redirectUrls.get(authority.getAuthority());
                                     break;
                                 }
                             }
                             response.sendRedirect(url);
                         }
                     })
                     .failureForwardUrl("/account/login?error");
                })
                .logout(f -> {
                    f.logoutUrl("/account/logout")
                     .logoutSuccessUrl("/account/login");
                })
                .exceptionHandling(ex -> {
                    ex.accessDeniedPage("/account/access-denied");
                })
                .build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    public void configGlobal(AuthenticationManagerBuilder builder) throws Exception {
        builder.userDetailsService(accountService);
    }

}

/*
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    private AccountService accountService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .cors(cors -> cors.disable())  // Vô hiệu hóa CORS
                .csrf(csrf -> csrf.disable())  // Vô hiệu hóa CSRF
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/**")  // Cho phép tất cả các yêu cầu
                        .permitAll()  // Tất cả các trang đều có quyền truy cập
                )
                .formLogin(f -> {
                    f.loginPage("/account/login")
                            .loginProcessingUrl("/account/process-login")
                            .usernameParameter("email")
                            .passwordParameter("password")
                            .successHandler(new AuthenticationSuccessHandler() {
                                @Override
                                public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                                                     Authentication authentication) throws IOException, ServletException {
                                    Map<String, String> redirectUrls = new HashMap<>();
                                    redirectUrls.put("ROLE_ADMIN", "/admin/doctor-management");
                                    redirectUrls.put("ROLE_DOCTOR", "/doctor/profile");
                                    redirectUrls.put("ROLE_PATIENT", "/patient/faculty-list");
                                    String url = "";
                                    for (GrantedAuthority authority : authentication.getAuthorities()) {
                                        if (redirectUrls.keySet().contains(authority.getAuthority())) {
                                            url = redirectUrls.get(authority.getAuthority());
                                            break;
                                        }
                                    }
                                    response.sendRedirect(url);
                                }
                            })
                            .failureForwardUrl("/account/login?error");

                })
                .logout(f -> {
                    f.logoutUrl("/account/logout")
                            .logoutSuccessUrl("/account/login");
                })
                .exceptionHandling(ex -> {
                    ex.accessDeniedPage("/account/access-denied");
                })
                .build(); // Đóng chuỗi cấu hình và build SecurityFilterChain
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    public void configGlobal(AuthenticationManagerBuilder builder) throws Exception {
        builder.userDetailsService(accountService);
    }
}
*/
