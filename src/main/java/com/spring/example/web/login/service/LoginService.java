package com.spring.example.web.login.service;

import com.spring.example.common.security.jwt.JwtTokenProvider;
import com.spring.example.web.login.mapper.LoginMapper;
import com.spring.example.web.login.model.LoginVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Slf4j
@Service
public class LoginService {

    private static final String rolePrefix = "ROLE_";

    private final LoginMapper loginMapper;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final JwtTokenProvider jwtTokenProvider;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public LoginVO findLoginUserInfo(String userId, String userPwd) {
        LoginVO loginVO = loginMapper.loginInfo(userId);

        if (loginVO == null) throw new RuntimeException("fail");

        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userId, userPwd);

            // 검증
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

            // JWT Token create
            String token = jwtTokenProvider.generateToken(authentication);

            String refreshToken = jwtTokenProvider.refreshToken(authentication);

            loginVO.setToken(token);
            loginVO.setRefreshToken(refreshToken);

            return loginVO;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("fail");
        }
    }
}
