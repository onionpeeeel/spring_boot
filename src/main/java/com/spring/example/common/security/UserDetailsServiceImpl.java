package com.spring.example.common.security;

import com.spring.example.web.login.mapper.LoginMapper;
import com.spring.example.web.login.model.LoginVO;
import com.spring.example.web.login.model.UserVO;
import com.spring.example.web.login.service.LoginService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final String rolePrefix = "ROLE_";

    private final LoginService loginService;

    private final LoginMapper loginMapper;

    private static Map<String, LoginVO> userMap = new HashMap<>();

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        log.info("로그인 시도 ID - loadUserByUsername id : {}", id);
        if (StringUtils.isEmpty(id)) {
            log.error("ID === NULL");
            throw new UsernameNotFoundException("넘어온 ID가 없습니다.");
        }

        // 로그인 사용자 정보 조회
        LoginVO loginInfo = loginMapper.loginInfo(id);

        String roleString = rolePrefix + loginInfo.getRoleId();

        // 사용자 권한 세팅
        Collection<GrantedAuthority> userRole = new ArrayList<>();

        userRole.add(new GrantedAuthority() {
            @Override
            public String getAuthority() { return roleString; }
        });
        loginInfo.setRole(userRole);

        if (loginInfo == null) {
            log.error("조회 결과 아이디 === null");
            throw new UsernameNotFoundException("사용자가 존재하지 않습니다.");
        }

        userMap.put(loginInfo.getUserId(), loginInfo);

        return loginInfo;
    }

    public static LoginVO getLoginInfo(String id) {
        LoginVO loginInfo = userMap.get(id);

        return loginInfo;
    }

    public static LoginVO getUserInfo() {
        // 시큐리티 로그인 사용자 정보 조회
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginVO principal = (LoginVO) authentication.getPrincipal();
        LoginVO loginInfo = userMap.get(principal.getUserId());

        // TODO no data exception ..

        return loginInfo;
    }

    public static LoginVO setUserInfo(UserVO userVO) {
        LoginVO userInfo = new LoginVO();
        userInfo.setUserId(userVO.getUserId());
        userInfo.setUserNm(userVO.getUserNm());

        return userMap.put(userVO.getUserId(), userInfo);
    }

    private static Map<String, LoginVO> getAllLoginInfo() { return userMap; }

}
