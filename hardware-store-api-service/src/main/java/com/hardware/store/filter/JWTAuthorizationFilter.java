package com.hardware.store.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.hardware.store.util.JWTUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

import static java.util.Objects.isNull;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@AllArgsConstructor(onConstructor = @__({@Autowired}))
public class JWTAuthorizationFilter extends OncePerRequestFilter {

    private static final String BEARER = "Bearer ";

    private JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(request.getServletPath().equals("/api/v1/authenticate")) {
            filterChain.doFilter(request, response);
        } else {
            String authorizationHeader = request.getHeader(AUTHORIZATION);
            if(!isNull(authorizationHeader) && authorizationHeader.startsWith(BEARER)) {
                try {
                    String accessToken = authorizationHeader.substring(BEARER.length());
                    DecodedJWT decodedJWT = jwtUtil.verifyToken(accessToken);
                    String username = jwtUtil.getSubject(decodedJWT);
                    Collection<SimpleGrantedAuthority> authorities = jwtUtil.getAuthorities(decodedJWT);
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    filterChain.doFilter(request, response);
                } catch (Exception e) {
                    log.error("Error during verifying JWT : ", e);
                    throw e;
                }
            } else {
                filterChain.doFilter(request, response);
            }
        }
    }
}
