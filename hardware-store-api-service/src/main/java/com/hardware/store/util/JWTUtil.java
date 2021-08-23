package com.hardware.store.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JWTUtil {

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.secret}")
    private String secret;

    public String generateAccessToken(User user) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(expiryDuration())
                .withIssuer(issuer)
                .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);
    }

    public DecodedJWT verifyToken(String accessToken) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        return jwtVerifier.verify(accessToken);
    }

    public String getSubject(DecodedJWT decodedJWT) {
        return decodedJWT.getSubject();
    }

    protected Date expiryDuration() {
        return new Date(System.currentTimeMillis() + 60 * 60 * 1000);
    }

    public Collection<SimpleGrantedAuthority> getAuthorities(DecodedJWT decodedJWT) {
        return Arrays
                .stream(decodedJWT.getClaim("roles").asArray(String.class))
                .map(role -> new SimpleGrantedAuthority(role))
                .collect(Collectors.toSet());
    }
}
