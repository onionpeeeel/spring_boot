package com.spring.example.common.security.jwt;

import com.spring.example.web.login.model.LoginVO;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

// Access Token 생성 해줌 ..
@Slf4j
@Component
public class JwtTokenProvider {

    private final Key key;

    @Value("${jwt.validtime}")
    int validTime;

    @Value("${jwt.refreshvalidtime}")
    int refreshValidTime;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] secretByteKey = DatatypeConverter.parseBase64Binary(secretKey);
        this.key = Keys.hmacShaKeyFor(secretByteKey);
    }

    public String generateToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // 사용자 정보 - 필요한 정보가 있으면 claim에 추가해 토큰 생성
        LoginVO principal = (LoginVO) authentication.getPrincipal();

        // Access Token Create - 사용자명, 권한 세팅
        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", authorities)
                .setExpiration(new Date(System.currentTimeMillis() + validTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String refreshToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // Refresh Token 생성
        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", authorities)
                .setExpiration(new Date(System.currentTimeMillis() + refreshValidTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Authentication getAuthentication(String accessToken) {
        // 토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get("auth") == null) {
            throw new RuntimeException("access not found");
        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        LoginVO principal = new LoginVO();

        principal.setUserId(claims.getSubject());
        principal.setRoleId(claims.get("auth").toString());

        return new UsernamePasswordAuthenticationToken(principal, "",  authorities);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            throw new CustomJwtException(JwtErrorType.Invalid);
        } catch (ExpiredJwtException e) {
            throw new CustomJwtException(JwtErrorType.Expired);
        } catch (UnsupportedJwtException e) {
            throw new CustomJwtException(JwtErrorType.UnsupportedJwt);
        } catch (IllegalArgumentException e) {
            throw new CustomJwtException(JwtErrorType.IllegalArgument);
        }
    }

    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch(ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public String createToken(String userId, String roles) {
        Claims claims = Jwts.claims().setSubject(userId);
        claims.put("roles", roles);

        return Jwts.builder()
                .setSubject(userId)
                .claim("auth", roles)
                .setExpiration(new Date(System.currentTimeMillis() + validTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(String userId, String roles) {
        Claims claims = Jwts.claims().setSubject(userId);
        claims.put("roles", roles);

        return Jwts.builder()
                .setSubject(userId)
                .claim("auth", roles)
                .setExpiration(new Date(System.currentTimeMillis() + refreshValidTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
