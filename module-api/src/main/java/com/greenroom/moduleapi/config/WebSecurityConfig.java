package com.greenroom.moduleapi.config;

import com.greenroom.moduleapi.security.jwt.JwtAuthenticationProvider;
import com.greenroom.modulecommon.config.JwtConfig;
import com.greenroom.modulecommon.entity.user.Role;
import com.greenroom.modulecommon.jwt.JwtAuthenticationTokenFilter;
import com.greenroom.modulecommon.jwt.JwtProvider;
import com.greenroom.modulecommon.security.CommonAccessDeniedHandler;
import com.greenroom.modulecommon.security.CommonUnauthorizedHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtConfig jwtConfig;
    private final JwtProvider jwtProvider;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final CommonUnauthorizedHandler unauthorizedHandler;
    private final CommonAccessDeniedHandler accessDeniedHandler;

    @Bean
    public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter() {
        return new JwtAuthenticationTokenFilter(jwtProvider, jwtConfig.getHeader());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // AuthenticationManager 는 AuthenticationProvider 목록을 지니고 있다.
        // 이 목록에 JwtAuthenticationProvider 를 추가한다.
        auth.authenticationProvider(jwtAuthenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf()
                .disable()
            .headers()
                .disable()
            .cors()
                .configurationSource(configurationSource())
                .and()
            .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler)
                .authenticationEntryPoint(unauthorizedHandler)
            .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
                .authorizeRequests()
                    .regexMatchers("^/actuator.*").permitAll()
                    .antMatchers("/h2-console/**").permitAll()
                    .antMatchers("/api/health").permitAll()
                    .antMatchers("/api/auth/login").permitAll()
                    .antMatchers("/api/auth/reissue").permitAll()
                    .antMatchers("/api/users/join").permitAll()
                    .antMatchers("/api/users/name").permitAll()
                    .antMatchers("/api/**").hasRole(Role.USER.name())
                .anyRequest().authenticated()
            .and()
                .formLogin()
                .disable();
        http
            .addFilterBefore(jwtAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public CorsConfigurationSource configurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
