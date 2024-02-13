package com.spring.example.common.filter;

import com.spring.example.common.config.SecurityConfig;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@WebFilter
@Order(2)
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpServletRequest requestToCache = new ContentCachingRequestWrapper(req);
        String uri = requestToCache.getRequestURI();

        try {
            // remove white list
            List<String> whiteList = new ArrayList<>(Arrays.asList(SecurityConfig.getAuthWhiteList()));
            for (String str : whiteList) {
                str = str.replace("/**", "");
                if (uri.indexOf(str) != -1) {
                    log.info("white list url : {}",uri);
                    chain.doFilter(request, response);
                    return;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void destroy() { Filter.super.destroy(); }
}
