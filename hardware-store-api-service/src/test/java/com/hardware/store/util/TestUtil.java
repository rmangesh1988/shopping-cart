package com.hardware.store.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class TestUtil {

    private static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static final String USERNAME_ADMIN = "adminUsername@example.io";
    public static final String USERNAME_CUSTOMER = "customerUsername@example.io";

    static {
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
    }

    public static String asJsonString(Object object) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(object);
    }
}
