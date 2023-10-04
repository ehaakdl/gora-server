package org.gora.server.common.token;

import java.security.Key;
import java.util.Date;
import java.util.Map;

import org.gora.server.model.TokenInfoDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TokenUtils {
    // 32글자 필요
    @Value("${app.secret-key}")
    private String SECRET_KEY;

    private Key getSecretKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    private String createToken(Map<String, Object> claimsMap, String subject, Date expireAt) {

        return Jwts.builder()
                .setSubject(subject)
                .addClaims(claimsMap)
                .setIssuedAt(new Date())
                .setExpiration(expireAt)
                .signWith(getSecretKey())
                .compact();
    }

    public TokenInfoDto createToken(Map<String, Object> claimsMap, eTokenType type) {
        Date nowAt = new Date();
        Date expiredAt = new Date(nowAt.getTime() + type.getExpirePeriod());
        String token = createToken(claimsMap, type.getSubject(), expiredAt);
        return new TokenInfoDto("Bearer " + token, expiredAt);
    }

    public String extractToken(String token) {
        return token.substring("Bearer ".length());
    }

    public boolean validToken(String token) {
        token = extractToken(token);
        
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
}
