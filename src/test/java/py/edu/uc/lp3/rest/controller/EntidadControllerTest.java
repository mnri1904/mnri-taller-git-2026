package py.edu.uc.lp3.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import py.edu.uc.lp3.repository.EntidadRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class EntidadControllerTest {

    private static final String URL = "/api/minecraft/entidades";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntidadRepository repository;

    @BeforeEach
    void limpiarBase() {
        repository.deleteAll();
    }

    @ParameterizedTest
    @CsvSource({"Zombie,20", "Creeper,20", "Cerdo,10"})
    void completaCrudPersistiendoCadaSubtipo(String tipo, int vidaMaxima) throws Exception {
        String cuerpo = solicitud(tipo, vidaMaxima, 2, -3);
        var resultado = mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo").value(tipo))
                .andExpect(jsonPath("$.vidaMaxima").value(vidaMaxima))
                .andReturn();
        long id = objectMapper.readTree(resultado.getResponse().getContentAsString()).get("id").asLong();
        String ubicacion = URL + "/" + id;
        assertThat(resultado.getResponse().getHeader("Location")).isEqualTo(ubicacion);

        // Se consulta fuera de la transaccion del POST: JPA debe reconstruir el subtipo desde H2.
        assertThat(repository.findById(id).orElseThrow().tipo()).isEqualTo(tipo);
        mockMvc.perform(get(ubicacion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vida").value(vidaMaxima))
                .andExpect(jsonPath("$.posicionZ").value(-3));
        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(id));

        mockMvc.perform(put(ubicacion).contentType(MediaType.APPLICATION_JSON)
                        .content(solicitud(tipo, 5, 10, 11)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.vida").value(5));
        mockMvc.perform(get(ubicacion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vida").value(5))
                .andExpect(jsonPath("$.posicionX").value(10));

        mockMvc.perform(delete(ubicacion)).andExpect(status().isNoContent());
        mockMvc.perform(get(ubicacion)).andExpect(status().isNotFound());
        assertThat(repository.existsById(id)).isFalse();
    }

    @ParameterizedTest
    @CsvSource({"Zombie,0", "Zombie,-1", "Zombie,21", "Creeper,21", "Cerdo,11", "Dragon,10"})
    void rechazaReglasDeNegocioSinPersistir(String tipo, int vida) throws Exception {
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                        .content(solicitud(tipo, vida, 0, 0)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isString());
        assertThat(repository.count()).isZero();
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{\"tipo\":\"Zombie\",\"vida\":10}",
            "{\"tipo\":\" \",\"vida\":10,\"posicionX\":0,\"posicionZ\":0}"})
    void rechazaCamposObligatoriosAusentes(String cuerpo) throws Exception {
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isString());
        assertThat(repository.count()).isZero();
    }

    @Test
    void actualizacionInvalidaConservaEstadoPersistido() throws Exception {
        var resultado = mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                        .content(solicitud("Zombie", 20, 0, 0)))
                .andExpect(status().isCreated()).andReturn();
        String ubicacion = resultado.getResponse().getHeader("Location");

        mockMvc.perform(put(ubicacion).contentType(MediaType.APPLICATION_JSON)
                        .content(solicitud("Cerdo", 5, 8, 9)))
                .andExpect(status().isBadRequest());
        mockMvc.perform(put(ubicacion).contentType(MediaType.APPLICATION_JSON)
                        .content(solicitud("Zombie", 21, 8, 9)))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get(ubicacion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("Zombie"))
                .andExpect(jsonPath("$.vida").value(20))
                .andExpect(jsonPath("$.posicionX").value(0));
    }

    @Test
    void consultaProtocoloSonoroEntreFamiliasPersistidas() throws Exception {
        mockMvc.perform(get(URL + "/sonidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
        for (String tipo : new String[] {"Zombie", "Cerdo", "Creeper"}) {
            mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                            .content(solicitud(tipo, 10, 0, 0)))
                    .andExpect(status().isCreated());
        }
        mockMvc.perform(get(URL + "/sonidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[?(@.tipo == 'Zombie')].mensaje")
                        .value("Grrr: el zombie gime buscando un objetivo."))
                .andExpect(jsonPath("$[?(@.tipo == 'Cerdo')].mensaje")
                        .value("Oink, oink: el cerdo gruñe tranquilamente."));
    }

    @Test
    void devuelve404ParaOperacionesSobreIdInexistente() throws Exception {
        mockMvc.perform(get(URL + "/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("No existe la entidad con id 999999"));
        mockMvc.perform(put(URL + "/999999").contentType(MediaType.APPLICATION_JSON)
                        .content(solicitud("Zombie", 10, 0, 0)))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete(URL + "/999999")).andExpect(status().isNotFound());
    }

    private String solicitud(String tipo, int vida, int x, int z) {
        return """
                {"tipo":"%s","vida":%d,"posicionX":%d,"posicionZ":%d}
                """.formatted(tipo, vida, x, z);
    }
}
