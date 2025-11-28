//package com.ims.inventoryManagementSystem.security;
//
//import io.jsonwebtoken.*;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.stereotype.Component;
//
//import java.security.Key;
//import java.util.Date;
//
//@Component
//public class JwtUtil {
//
//    private static final String SECRET_KEY = "MySuperSecretKeyForJwtGenerationMySuperSecretKey";
//    private static final long EXPIRATION_TIME = 1000 * 60 * 60;
//
//    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
//
//
//
//    public String generateToken(String email) {
//        return Jwts.builder()
//                .setSubject(email)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
//                .signWith(key, SignatureAlgorithm.HS256)
//                .compact();
//    }
//
//    public String extractEmail(String token) {
//        return Jwts.parserBuilder().setSigningKey(key).build()
//                .parseClaimsJws(token).getBody().getSubject();
//    }
//
//    public boolean validateToken(String token) {
//        try {
//            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
//            return true;
//        } catch (JwtException e) {
//            return false;
//        }
//    }
//}

package com.ims.inventoryManagementSystem.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private static final String SECRET_KEY = "MySuperSecretKeyForJwtGenerationMySuperSecretKey";
    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hour

    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());


    // ==========================
    // GENERATE TOKEN
    // ==========================
    public String generateToken(String email, Long userId, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userId);
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    // ==========================
    // COMMON CLAIM EXTRACTION
    // ==========================
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    // ==========================
    // REQUIRED METHODS
    // ==========================

    /** Get email (subject) */
    public String getEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    /** Get user ID */
    public Long getId(String token) {
        Object id = extractAllClaims(token).get("id");
        return id == null ? null : Long.parseLong(id.toString());
    }

    /** Get role */
    public String getRole(String token) {
        Object role = extractAllClaims(token).get("role");
        return role == null ? null : role.toString();
    }


    /** Check if token is expired */
    public boolean isTokenExpired(String token) {
        Date expiry = extractAllClaims(token).getExpiration();
        return expiry.before(new Date());
    }


    /**
     * Your interceptor calls:
     *     if(jwtUtil.validateToken(token).isEmpty() || jwtUtil.validateToken(token) == null)
     *
     * So validateToken MUST return a String.
     */
//    public boolean validateToken(String token) {
//        try {
//            extractAllClaims(token);
////            return "VALID";  // non-empty string
//            return true;
//        } catch (ExpiredJwtException e) {
////            return "EXPIRED";
//            return false;
//        } catch (JwtException e) {
////            return "INVALID";
//            return false;
//        } catch (Exception e) {
////            return "ERROR";
//            return false;
//        }
//    }

    public String validateToken(String token) {
        try {
            extractAllClaims(token);
            return "VALID";  // non-empty string
//            return true;
        } catch (ExpiredJwtException e) {
            return "EXPIRED";
//            return false;
        } catch (JwtException e) {
            return "INVALID";
//            return false;
        } catch (Exception e) {
            return "ERROR";
//            return false;
        }
    }
    public String extractEmail(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (JwtException e) {
            return null;  // invalid token
        }
    }

    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email) // email stored as subject
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


}
