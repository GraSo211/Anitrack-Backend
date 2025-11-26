package com.graso.anitrack.security.service;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private static final String SECRET_KEY = "rH59Qtv2mA6yX3cC8pLewS7fK2eJ9uT4dR1cM8vG5zP2qN7kH3yF6bT0jW4aE9xS";
    /* private static final long TOKEN_EXPIRATION = 1000 * 60 * 60 * 24; */
    private static final long TOKEN_EXPIRATION = 1000;
    private static final long REFRESH_WINDOW = 1000 * 60 * 60 * 24 * 7;

    public SecretKey key() {
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(SECRET_KEY));
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = Map.of("authorities", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList());
        return generateToken(claims, userDetails.getUsername());
    }

    public String generateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION))
                .signWith(
                        key())

                .compact();
    }

    private Claims getAllClaims(String token) {

        return Jwts
                .parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }

    private <T> T getClaim(String token, Function<Claims,T> claimsMapper){
        Claims claims = getAllClaims(token);
        return claimsMapper.apply(claims);
    }

    public String getUsername(String token){
        return getClaim(token, Claims::getSubject);
    }


    public Date getExpiration(String token){
        return getClaim(token, Claims::getExpiration);
    }

    public boolean isTokenExpired(String token){
        return getExpiration(token).before(new Date());
    }

    public boolean canTokenBeRefreshed(String token){
        return getExpiration(token).before(new Date(System.currentTimeMillis()+ REFRESH_WINDOW));
    }

    public String renewToken(String token, UserDetails userDetails){
        if(!canTokenBeRefreshed(token)){
            throw new RuntimeException("Token cannot be refreshed");
        }
        
        return generateToken(userDetails);
    }

  /*   public String getAuthorities(String token){
        return getClaim(token, claims -> claims.get("authorities", String.class));
    } */
}
