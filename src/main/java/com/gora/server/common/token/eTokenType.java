package com.gora.server.common.token;

import java.security.Key;
import java.util.Date;
import java.util.Map;

import com.gora.server.model.TokenInfoDTO;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum eTokenType {
    ACCESS(
            1000L * 60 * 60, "access"),
    REFRESH(1000L * 60 * 60, "refresh"),
    EMAIL_VERIFY(
            1000L * 60 * 3, "email_verify");

    private final long expirePeriod;
    private final String subject;
    private static String SECRET_KEY = System.getenv("JWT_SECRET_KEY");

    private static String createToken(Map<String, Object> claimsMap, String subject, Date expireAt) {

        return Jwts.builder()
                .setSubject(subject)
                .addClaims(claimsMap)
                .setIssuedAt(new Date())
                .setExpiration(expireAt)
                .signWith(getSecretKey())
                .compact();
    }

    private static Key getSecretKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public static boolean validToken(String token) {
        token = token.replace("Bearer ", "");

        try {
            Jwts.parser()
                    .setSigningKey(getSecretKey())
                    .parseClaimsJws(token)
                    .getBody();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static TokenInfoDTO createToken(Map<String, Object> claimsMap, eTokenType type) {
        Date nowAt = new Date();
        Date expiredAt = new Date(nowAt.getTime() + type.getExpirePeriod());
        String token = createToken(claimsMap, type.getSubject(), expiredAt);
        return new TokenInfoDTO("Bearer " + token, expiredAt);
    }
}
