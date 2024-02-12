package com.spring.example.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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

}
