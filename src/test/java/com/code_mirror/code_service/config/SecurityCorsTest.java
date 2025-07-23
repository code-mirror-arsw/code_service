package com.code_mirror.code_service.config;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = SecurityCorsTest.TestController.class)
@Import(SecurityCors.class)
class SecurityCorsTest {

    @Autowired
    private MockMvc mockMvc;

    @RestController
    static class TestController {
        @GetMapping("/test")
        public String testEndpoint() {
            return "CORS test OK";
        }
    }

    @Test
    void shouldAllowCorsFromAllowedOrigin() throws Exception {
        mockMvc.perform(options("/test")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"))
                .andExpect(header().string("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS"))
                .andExpect(header().string("Access-Control-Allow-Credentials", "true"));
    }

    @Test
    void shouldRejectCorsFromDisallowedOrigin() throws Exception {
        mockMvc.perform(options("/test")
                        .header("Origin", "http://not-allowed.com")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isForbidden());
    }
}
