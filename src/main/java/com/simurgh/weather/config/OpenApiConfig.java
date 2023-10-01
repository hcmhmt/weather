package com.simurgh.weather.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SimurgH Open Weather Service API")
                        .version("0.1")
                        .description("""
                                This is an API provides weather report 
                                for last 30 minutes based on city names
                                """
                        ));
    }

}
