package com.hardware.store.filter;

import com.hardware.store.util.JWTUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CredentialsAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final String AUTHENTICATE_URI = "/api/v1/authenticate";

    private JWTUtil jwtUtil;

    public CredentialsAuthenticationFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
        super(authenticationManager);
        this.setFilterProcessesUrl(AUTHENTICATE_URI);
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        User user = (User) authentication.getPrincipal();
        String accessToken = jwtUtil.generateAccessToken(user);
        response.setHeader("access-token", accessToken);
    }

}
