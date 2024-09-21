package com.ashutosh.blog.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.sql.DataSource;


@Configuration
public class SecurityConfiguration {

    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource) {
        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);

        jdbcUserDetailsManager.setUsersByUsernameQuery(
                "select name,password,enabled from user where name = ?"
        );
        jdbcUserDetailsManager.setAuthoritiesByUsernameQuery(
                "select username, role from role where username=?"
        );
        return jdbcUserDetailsManager;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf->csrf.disable())
        .authorizeHttpRequests(configurer ->
                        configurer
                                .requestMatchers("/register/**").permitAll()
                                .requestMatchers("/homepage").permitAll()
                                .requestMatchers("/posts").permitAll()
                                .requestMatchers("/showblog/**").permitAll()
                                .requestMatchers("/comments/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/posts").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/posts/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/comments").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/comments/**").permitAll()
                                .anyRequest().authenticated()
                )
                .formLogin(form ->
                        form
                                .loginPage("/login")
                                .loginProcessingUrl("/authenticateTheUser")
                                .defaultSuccessUrl("/homepage", true)
                                .permitAll()
                )

                .logout(logout -> logout.permitAll());
        http.httpBasic(Customizer.withDefaults());
        return http.build();
    }

}
