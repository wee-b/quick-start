package com.quickstart.base.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    /**
     * 手动注册 ObjectMapper Bean，解决注入失败问题
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}