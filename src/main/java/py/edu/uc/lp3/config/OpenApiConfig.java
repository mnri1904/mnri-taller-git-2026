package py.edu.uc.lp3.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "Minecraft de Marcelo Romero — entidades y explosiones", version = "1.0.0",
        contact = @Contact(name = "Marcelo Romero", url = "https://github.com/mnri1904/mnri-taller-git-2026"),
        description = "Los GET solo consultan. Crear, actualizar, borrar y detonar modifican H2. "
                + "En Swagger UI, Execute envía un pedido real a este servidor."))
public class OpenApiConfig implements WebMvcConfigurer {
    @Bean
    OpenAPI contratoMinecraft() {
        var error = new MediaType().schema(new Schema<>().$ref("#/components/schemas/ErrorJuego"))
                .example(Map.of("codigo", "ENTIDAD_NO_ENCONTRADA", "error", "No existe la entidad con id 99"));
        var noEncontrada = new ApiResponse().description("No hay una fila con ese ID.")
                .content(new Content().addMediaType("application/json", error));
        return new OpenAPI().components(new Components().addResponses("NoEncontrada", noEncontrada));
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/openapi.json", "/v3/api-docs");
    }
}
