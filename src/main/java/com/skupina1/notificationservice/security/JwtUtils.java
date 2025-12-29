package com.skupina1.notificationservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class JwtUtils {

    private static final String SECRET = "53V3NPR41535T0L0RDJUR1CL0NGM4YHER31GNF0RH15RUL3W45PR0M153D";

    public static Claims validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
