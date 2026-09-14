package com.andrzej_kosmowski.medical_clinic_proxy.config;

import feign.Client;
import feign.okhttp.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfiguration {
    @Bean
    public Client feignClient() {
        return new OkHttpClient();
    }
}
