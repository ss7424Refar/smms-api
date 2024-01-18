package com.asv.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TokenUtil {

    //    @Value("${jwt.secret}")
    private static final String secret = "storageKey";

    // 1H 过期
    private static final Long expiration = 3600000L;

    private static final List<String> blacklist = new ArrayList<>();

    public static void invalidateToken(String token) {
        blacklist.add(token);
    }

    public static boolean isTokenBlacklisted(String token) {
        return blacklist.contains(token);
    }

    public static String generateToken(String userId, String userName) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(userId)
                .claim("userName", userName)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public static Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    public static boolean isTokenExpired(String token) {
        if (isTokenBlacklisted(token)) {
            return true;
        }
        Claims claims = parseToken(token);

        Date expirationDate = claims.getExpiration();
        return expirationDate.before(new Date());
    }
}
