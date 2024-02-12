package com.spring.example.web.login.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
@Setter
public class LoginVO implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String userId;
    private String userNm;
    private String userPwd;
    private String useYn;
    private String roleId;
    private String token;
    private String refreshToken;
    private Collection<GrantedAuthority> role;

    @Override
    public String getPassword() { return this.userPwd; }

    @Override
    public String getUsername() { return this.userId; }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return StringUtils.equals(this.useYn, "Y"); }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return StringUtils.equals(this.useYn, "Y"); }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return role; }
}
