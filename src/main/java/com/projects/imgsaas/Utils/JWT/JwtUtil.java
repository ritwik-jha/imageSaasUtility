package com.projects.imgsaas.Utils.JWT;

// import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.SignatureAlgorithm;
// import io.jsonwebtoken.Claims;

import org.springframework.stereotype.Component;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@Component
public class JwtUtil {

    private final String secretKey = "mysecretkey";
    private final long tokenValidityInMilliseconds = 60 * 60 * 1000;

    public String generateJwtToken(String email) throws Exception {
        long currTime = System.currentTimeMillis();
        long expiryTime = System.currentTimeMillis() + tokenValidityInMilliseconds;

        String header = Base64.getEncoder().encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes());
        String payloadInString = "{\"sub\":\"" + email + "\",\"iat\":" + currTime / 1000 + ",\"exp\":" + expiryTime / 1000 + "}";
        String payload = Base64.getEncoder().encodeToString(payloadInString.getBytes());

        String signaure = generateHMAC(header + "." + payload, secretKey);

        return header + "." + payload + "." + signaure;
    }

    public boolean validateToken(String token, String email) throws Exception {
        String[] tokenArray = token.split("\\.");

        if(tokenArray.length != 3){
            return false;
        }

        String header = tokenArray[0];
        String payload = tokenArray[1];
        String signature = tokenArray[2];

        String expectedSignature = generateHMAC(header + "." + payload, secretKey);

        if(!signature.equals(expectedSignature)){
            return false;
        }

        String payloadJson = new String(Base64.getUrlDecoder().decode(payload));
        String emailFromPayload = payloadJson.replaceAll(".*\"sub\":\"(.*?)\".*", "$1");
        long exp = Long.parseLong(payloadJson.replaceAll(".*\"exp\":(\\d+).*", "$1"));

        if(!email.equals(emailFromPayload) || exp < System.currentTimeMillis()){
            return false;
        }

        return true;

    }

    private static String generateHMAC(String data, String key) throws Exception {
        Mac hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "HmacSHA256");
        hmac.init(secretKeySpec);
        byte[] hash = hmac.doFinal(data.getBytes());
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
    }


}
