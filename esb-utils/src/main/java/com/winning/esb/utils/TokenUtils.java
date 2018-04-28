package com.winning.esb.utils;

/**
 * Created by xuehao on 2017/9/11.
 */

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.*;


public class TokenUtils {

    public static final String CLAIM_KEY_USERNAME = "sub";
    public static final String CLAIM_KEY_CREATED = "crt";
    public static final String CLAIM_KEY_ROLE = "role";
    public static final String CLAIM_KEY_AUTH = "auth";

    private static String secret = "grace";

    private static Long expiration = 3153600000L;

    /**
     * 从 Token 中获取 username
     */
    public static String getUsernameFromToken(String token) {
        String username;
        try {
            final Claims claims = getClaimsFromToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            username = e.toString();
        }
        return username;
    }


    /**
     * 从 Token 中获取创建时间
     */
    public static Date getCreatedDateFromToken(String token) {
        Date created;
        try {
            final Claims claims = getClaimsFromToken(token);
            created = new Date((Long) claims.get(CLAIM_KEY_CREATED));
        } catch (Exception e) {
            created = null;
        }
        return created;
    }

    /**
     * 从 Token 中获取过时时间
     */
    public static Date getExpirationDateFromToken(String token) {
        Date expiration;
        try {
            final Claims claims = getClaimsFromToken(token);
            expiration = claims.getExpiration();
        } catch (Exception e) {
            expiration = null;
        }
        return expiration;
    }

    private static Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    /**
     * 生成过期时间
     */
    private static Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + expiration * 1000);
    }

    /**
     * 验证 Token 是否过期
     */
    private static Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    private static Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
        return (lastPasswordReset != null && created.before(lastPasswordReset));
    }

    /**
     * 生成 Token
     */
    public static String generateToken(String username,  String auth) {
        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put(CLAIM_KEY_USERNAME, username);
        claims.put(CLAIM_KEY_CREATED, new Date());
        List<String> role = new ArrayList<String>();
        role.add("admin");
        role.add("user");
        claims.put(CLAIM_KEY_ROLE, role);
        claims.put(CLAIM_KEY_AUTH, auth);
        return generateToken(claims);
    }

    /**
     * 生成 Token
     */
    public static String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(generateExpirationDate())
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public static Boolean canTokenBeRefreshed(String token, Date lastPasswordReset) {
        final Date created = getCreatedDateFromToken(token);
        return !isCreatedBeforeLastPasswordReset(created, lastPasswordReset)
                && !isTokenExpired(token);
    }

    /**
     * 刷新 Token
     */
    public static String refreshToken(String token) {
        String refreshedToken;
        try {
            final Claims claims = getClaimsFromToken(token);
            claims.put(CLAIM_KEY_CREATED, new Date());
            refreshedToken = generateToken(claims);
        } catch (Exception e) {
            refreshedToken = null;
        }
        return refreshedToken;
    }

    /**
     * 验证 Token
     */
    public static Boolean validateToken(String token, String username1) {
        final String username = getUsernameFromToken(token);
        final Date created = getCreatedDateFromToken(token);
        //final Date expiration = getExpirationDateFromToken(token);
        return (username.equals(username1) && !isTokenExpired(token));
    }
}

