package com.spring.example.common.security.jwt;

import io.jsonwebtoken.JwtException;
import lombok.Getter;

@Getter
public class CustomJwtException extends JwtException {

    private final String msg = "";

    private final JwtErrorType type;

    public CustomJwtException(JwtErrorType type) {
        super(type.getMsg());

        this.type = type;
    }
}
