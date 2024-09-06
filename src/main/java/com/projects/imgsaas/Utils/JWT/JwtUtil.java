package com.projects.imgsaas.Utils.JWT;

// import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.SignatureAlgorithm;
// import io.jsonwebtoken.Claims;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Base64;

@Component
public class JwtUtil {

    private final String secretKey = "mysecretkey";

    public String generateJwtToken(String email){
        LocalDateTime localDateTime = LocalDateTime.now();

        String decodedToken = email + "?" + localDateTime.toString() + "?" + secretKey;
        String encodedString = Base64.getEncoder().encodeToString(decodedToken.getBytes());

        return encodedString;
    }
    public String decodeToken(String token){
        return new String(Base64.getDecoder().decode(token));
    }

    public boolean validateToken(String token, String username) {
        String[] tokenArray = decodeToken(token).split("\\?");
        String tokenUsername = tokenArray[0];
        return (username.equals(tokenUsername) && !isTokenExpired(token));
    }

    public boolean isTokenExpired(String token) {
        String[] tokenArray = decodeToken(token).split("\\?");
        String tokenDateTimeString = tokenArray[1];
        LocalDateTime tokenDateTime = LocalDateTime.parse(tokenDateTimeString);
        LocalDateTime nowTime = LocalDateTime.now();

        return tokenDateTime.isBefore(nowTime.minusHours(1));
    }

}
