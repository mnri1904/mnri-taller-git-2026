package py.edu.uc.lp3.rest.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UIController.class)
class UIControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest
    @CsvSource({
        "creeper, 0.25, El Creeper esta en silencio.",
        "zombie, 0.23, Grrr: el zombie gime buscando un objetivo.",
        "cerdo, 0.25, 'Oink, oink: el cerdo gruñe tranquilamente.'"
    })
    void exponeLosCuatroDatosDeCadaDibujable(String tipo, double velocidad, String sonido) throws Exception {
        mockMvc.perform(get("/api/minecraft/ui/" + tipo))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.imagen").value("minecraft:" + tipo))
                .andExpect(jsonPath("$.posicion.x").value(0))
                .andExpect(jsonPath("$.posicion.z").value(0))
                .andExpect(jsonPath("$.sonido").value(sonido))
                .andExpect(jsonPath("$.velocidad").value(velocidad));
    }

    @Test
    void rechazaUnTipoDesconocidoComoJson() throws Exception {
        mockMvc.perform(get("/api/minecraft/ui/desconocido"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Tipo dibujable no soportado: desconocido"));
    }
}
