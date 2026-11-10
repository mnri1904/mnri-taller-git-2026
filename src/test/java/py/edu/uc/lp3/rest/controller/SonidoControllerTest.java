package py.edu.uc.lp3.rest.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SonidoController.class)
class SonidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void devuelveElSonidoDelCerdoComoJson() throws Exception {
        mockMvc.perform(get("/api/minecraft/sonidos/cerdo"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.tipo").value("cerdo"))
                .andExpect(jsonPath("$.mensaje").value("Oink, oink: el cerdo gruñe tranquilamente."));
    }

    @Test
    void devuelveElSonidoDelZombieComoJson() throws Exception {
        mockMvc.perform(get("/api/minecraft/sonidos/zombie"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.tipo").value("zombie"))
                .andExpect(jsonPath("$.mensaje").value("Grrr: el zombie gime buscando un objetivo."));
    }

    @Test
    void rechazaUnTipoNoSoportadoConErrorJson() throws Exception {
        mockMvc.perform(get("/api/minecraft/sonidos/creeper"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Tipo sonoro no soportado: creeper"));
    }
}
