package com.andrzej_kosmowski.medical_clinic_proxy.config;

import com.andrzej_kosmowski.medical_clinic_proxy.errorDecoder.MedicalClinicErrorDecoder;
import feign.Client;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import feign.okhttp.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class FeignConfiguration {
    @Bean
    public Client feignClient() {
        return new OkHttpClient();
    }

    @Bean
    public ErrorDecoder errorDecoder(ObjectMapper objectMapper) {
        return new MedicalClinicErrorDecoder(objectMapper);
    }

    @Bean
    public Retryer retryer() {
        return new Retryer.Default();
    }
}
