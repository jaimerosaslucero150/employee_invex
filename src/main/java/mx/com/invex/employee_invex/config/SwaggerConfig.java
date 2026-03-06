package mx.com.invex.employee_invex.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
Developer: Enrique Rosas
Date: 05/03/2026

*/

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Employee API")
                        .version("1.0")
                        .description("API for employee management"));
    }
}
