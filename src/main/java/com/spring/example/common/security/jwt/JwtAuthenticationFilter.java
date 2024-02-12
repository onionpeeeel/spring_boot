package com.spring.example.common.security.jwt;

import io.jsonwebtoken.Claims;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// OncePerRequestFilter 로 한 요청에 대해 한번만 수행함 ..
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) { this.jwtTokenProvider = jwtTokenProvider; }

    //
    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException, CustomJwtException {
        String token = resolveToken(request);
        String refreshToken = resolveRefreshToken(request);

        try {
            logger.info(StringUtils.isEmpty(token));
            if (!StringUtils.isEmpty(token) && jwtTokenProvider.validateToken(token)) {
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (CustomJwtException e) {
            if (e.getType() == JwtErrorType.Expired) {
                // refresh token 초기화
                if (jwtTokenProvider.validateToken(refreshToken) && !StringUtils.isEmpty(refreshToken)) {
                    Claims claims = jwtTokenProvider.parseClaims(refreshToken);

                    // access token 재발급
                    String newAccessToken = jwtTokenProvider.createToken(claims.get("sub").toString(), claims.get("auth").toString());

                    // refresh token 재발급
                    String newRefreshToken = jwtTokenProvider.createRefreshToken(claims.get("sub").toString(), claims.get("auth").toString());

                    // Header 에 access Token 추가
                    response.setHeader("Authorization", newAccessToken);
                    response.setHeader("refreshToken", newRefreshToken);
                    response.setHeader("allTokenExpire", "N");
                    request.setAttribute("Authorization", newAccessToken);
                    request.setAttribute("refreshToken", newRefreshToken);

                    // 컨텍스트에 넣기 ..
                    this.setAuthentication(newAccessToken);
                } else throw new CustomJwtException(JwtErrorType.Invalid);
            } else throw e;
        }

        chain.doFilter(request, response);
    }

    // 헤더에서 토큰 추출 ..
    private String resolveToken(HttpServletRequest request) {
        logger.info("request bearer Token : " + request.getHeader("Authorization"));
        String bearerToken = request.getHeader("Authorization");
        return bearerToken;
    }

    // 헤더에서 리프레시 토큰 추출 ..
    private String resolveRefreshToken(HttpServletRequest request) {
        logger.info("request refresh Token : " + request.getHeader("refreshToken"));
        request.getHeaderNames().asIterator().forEachRemaining(
                headerName -> logger.debug(headerName + " : " + request.getHeader(headerName))
        );

        String refreshToken = request.getHeader("refreshToken");

        return refreshToken;
    }

    // Token 으로 유저 정보 ..
    public void setAuthentication(String token) {
//        Authentication authentication = jwtTokenProvider;
    }

}
