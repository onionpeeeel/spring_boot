package com.spring.example.common.security.jwt;

public enum JwtErrorType {

    Invalid("Invalid JWT Token"),

    Malformed("Malformed JWT Token"),

    Expired("Expired JWT Token"),

    IllegalArgument("JWT claims string is empty"),

    UnsupportedJwt("Unsupported JWT Token");

    private String msg;

    JwtErrorType(String msg) { this.msg = msg; }

    public String getMsg() { return msg; }
}
