package com.hardware.store.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hardware.store.domain.ApiException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@AllArgsConstructor(onConstructor = @__({@Autowired}))
public class ApiAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(ApiException.builder()
                .httpStatus(HttpStatus.UNAUTHORIZED)
                .message(ex.getMessage())
                .build()));
    }
}
