package com.spring.example.common.config;

import com.spring.example.common.security.UserDetailsServiceImpl;
import com.spring.example.common.security.jwt.JwtSecurityConfig;
import com.spring.example.common.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

// DI 방법 중 생성자 주입을 임의의 코드 없이 자동으로 설정
// 초기화 되지 않은 final 필드나,  @NonNull 이 붙은 필드에 대해 생성자 생성
@RequiredArgsConstructor
// 스프링 설정을 선언하는 어노테이션 ..
@Configuration
// Spring security를 활성화 하고 웹 보안 설정을 구성하는데 사용
// 자동으로 Spring security filter chain 생성하고 웹 보안을 활성화 ..
@EnableWebSecurity
public class SecurityConfig {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final UserDetailsServiceImpl userDetailsServiceImple;

    private final JwtTokenProvider jwtTokenProvider;

    // 인증 없이 접속 가능한 URL
    private static final String[] AUTH_WHITE_LIST = {
            "/api/**",
    };

    // 어드민 접근 URL
    private static final String[] AUTH_ADMIN_LIST = {

    };

    // 토큰 필터에서 제거
    @Bean
    public WebSecurityCustomizer configure() { return (web) -> web.ignoring().requestMatchers(""); }

    /**
     *  Spring Security 설정
     */
    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(login -> login.disable())
                .httpBasic(basic -> basic.disable())

                .authorizeHttpRequests()
                .requestMatchers(HttpMethod.OPTIONS, "/**/*").permitAll()
                .requestMatchers(AUTH_WHITE_LIST).permitAll()
                .requestMatchers(AUTH_ADMIN_LIST)
                .hasAnyRole("S", "A")

                .anyRequest()
                .authenticated()
                .and()
                .apply(new JwtSecurityConfig(jwtTokenProvider));

        return http.build();
    };

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder.authenticationProvider(authenticationProvider())
                .userDetailsService(userDetailsServiceImple)
                .passwordEncoder(bCryptPasswordEncoder);

        return authenticationManagerBuilder.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsServiceImple);
        authenticationProvider.setPasswordEncoder(bCryptPasswordEncoder);
        authenticationProvider.setHideUserNotFoundExceptions(false);

        return authenticationProvider;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);                                // json -> js 처리
        configuration.setAllowedOriginPatterns(List.of("*"));               // all ip response
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")); // 모든 메서드 허용
        configuration.addAllowedHeader("*");                                    // 모든 header에 응답
        configuration.addExposedHeader("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    public static String[] getAuthWhiteList() {
        return AUTH_WHITE_LIST;
    }

}
