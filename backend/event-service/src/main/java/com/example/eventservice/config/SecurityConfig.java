package com.example.eventservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtFilter jwtFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .cors()
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/events/**").permitAll()
                .antMatchers("/uploads/**").permitAll()
                .antMatchers(HttpMethod.POST, "/events/**").hasRole("ORGANIZER")
                .antMatchers(HttpMethod.PUT, "/events/**").hasRole("ORGANIZER")
                .antMatchers(HttpMethod.DELETE, "/events/**").hasRole("ORGANIZER")
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
