package org.gora.server.common.token;

import java.security.Key;
import java.util.Date;
import java.util.Map;

import org.gora.server.model.TokenInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TokenUtils {
    //    32글자 필요
    @Value("${app.secret-key:a89e2da3-704d-4ff0-a803-c8d8dc57cbf1}")
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

    public TokenInfo createToken(Map<String, Object> claimsMap, eTokenType type){
        Date nowAt = new Date();
        Date expiredAt = new Date(nowAt.getTime() + type.getExpirePeriod());
        String token = createToken(claimsMap, type.getSubject(), expiredAt);
        return new TokenInfo("Bearer "+ token, expiredAt);
    }

    public boolean validToken(String token){
        
    }
}
