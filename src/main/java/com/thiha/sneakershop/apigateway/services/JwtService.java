package com.thiha.sneakershop.apigateway.services;

import java.util.Date;
import java.lang.IllegalArgumentException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.function.Function;
import com.thiha.sneakershop.apigateway.utils.DateTimeHelper;
import java.security.Key;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    @Value("${jwtSettings.signingKey}")
    private String secretKey;

    private Key getSigningKey(){
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    private Claims extractsAllCalims(String token){
       try{
        return Jwts.parserBuilder()
                        .setSigningKey(getSigningKey())
                        .build()
                        .parseClaimsJws(token)
                        .getBody();
       }catch(Exception e){
            e.printStackTrace();
            throw new IllegalArgumentException("Invalid Jwt token", e);
       }
    }

    public <T> T extractsClaim(String token, Function<Claims, T> claimsResolver){
        Claims claims = extractsAllCalims(token);
        return claimsResolver.apply(claims);
    }

    private Date getExpiration(String token){
        return extractsClaim(token, Claims::getExpiration);
    }

    public boolean isTokenExpired(String token){
        Date now = DateTimeHelper.getCurrentUTC();
        Date expiration = getExpiration(token);
        return now.after(expiration);
    }

}
