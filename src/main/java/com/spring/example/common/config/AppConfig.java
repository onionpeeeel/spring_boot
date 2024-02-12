package com.spring.example.common.config;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// AppConfig에 설정을 구성한다.
@Configuration
public class AppConfig {

    private final AutowireCapableBeanFactory beanFactory;

    public AppConfig(AutowireCapableBeanFactory beanFactory) { this.beanFactory = beanFactory; }

    /*
    Spring Security 암호화 Encoder
     */

    // @Bean 메소드는 스프링 컨테이너에 등록됨 ..
    @Bean
    public BCryptPasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
}
