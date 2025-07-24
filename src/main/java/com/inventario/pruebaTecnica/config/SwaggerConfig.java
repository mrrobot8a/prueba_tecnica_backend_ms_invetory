package com.inventario.pruebaTecnica.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
    name = "apiKey", // Puede ser cualquier nombre
    type = SecuritySchemeType.APIKEY,
    in = SecuritySchemeIn.HEADER,
    paramName = "X-API-Key", // Exactamente el mismo nombre que usa tu filtro
    description = "API Key requerida para acceder a los endpoints"
)
public class SwaggerConfig {
    // Otras configuraciones de Swagger...
}