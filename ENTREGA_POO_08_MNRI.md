# Especificaciones de entrega — POO-08

- Estudiante: Marcelo Nicolas Romero Insfran (`mnri1904`).
- Asignatura: Lenguaje de Programacion 3, CYT646 F.
- Fecha del enunciado: 28 y 29 de octubre de 2026; revision del profesor del 3 de noviembre.
- Dominio: Minecraft.
- Proyecto: `mnri-taller-git-2026`.
- Enlace exacto al commit: **PENDIENTE: completar con la URL del commit publicado**.

## Objetivo aplicado al dominio

Publicar una API REST que guarde entidades de Minecraft en H2, permita crear,
listar, consultar, actualizar y eliminar registros, simule explosiones con dos
reglas propias y use un protocolo comun entre familias distintas. Los errores
se expresan con una jerarquia de excepciones y un handler por tipo concreto.

## Consignas implementadas

1. Spring Web, Spring Data JPA y H2, con Java 21 y Maven Wrapper.
2. Familia persistida `Entidad → Mob → Creeper/Zombie` y
   `Entidad → Animal → Cerdo`. Herencia JPA `SINGLE_TABLE` con discriminador `tipo`.
3. Arquitectura de tres capas: controller HTTP, service transaccional con
   validaciones y repositorio Spring Data de la clase base.
4. Protocolo `Sonoro` implementado por Zombie (Mob) y Cerdo (Animal).
5. Coleccion `insomnia/poo-08-minecraft.json` con entorno, CRUD, error de
   invariante, ID inexistente, lectura mediante la interfaz, simulacion y un
   caso por cada tipo de excepcion: 20 pedidos en total.
6. README con propiedades H2, decisiones, diagrama Mermaid, sobrecarga y
    sobreescritura; `BITACORA.md` con el uso de OpenCode y el modelo exacto.
7. Dos reglas: daño por proximidad y consumo del detonador, ejecutadas por
   metodos del dominio dentro de una transaccion que persiste las tres clases.
8. Excepciones no comprobadas bajo `MinecraftException`; handlers especificos
   que distinguen cada error con los campos JSON `codigo` y `error`.

## URLs y resultados

Base: `http://localhost:8080`.

| Metodo | Ruta | Resultado |
| --- | --- | --- |
| GET | `/` | `200`: confirma que el servicio vive |
| POST | `/api/minecraft/entidades` | `201`, ID generado y `Location` |
| GET | `/api/minecraft/entidades` | `200`, lista persistida |
| GET | `/api/minecraft/entidades/{id}` | `200` o `404` |
| PUT | `/api/minecraft/entidades/{id}` | `200`, actualizacion persistida; `400` o `404` ante error |
| DELETE | `/api/minecraft/entidades/{id}` | `204`, borrado fisico; `404` si no existe |
| GET | `/api/minecraft/entidades/sonidos` | `200`, mensajes mediante `Sonoro` |
| POST | `/api/minecraft/entidades/{id}/explosion` | `200`, simulacion de explosion y daño persistido |

## Invariantes

POST y PUT requieren `tipo`, `vida`, `posicionX` y `posicionZ`. La vida debe
estar entre 1 y 20 para Zombie/Creeper o entre 1 y 10 para Cerdo. El tipo debe
ser conocido y no puede cambiar durante una actualizacion. La identidad y la
vida maxima no son campos editables. Las coordenadas pueden ser negativas.
Los constructores y `actualizarEstado` validan antes de modificar campos
privados; una solicitud invalida no agrega ni modifica un registro.

PUT reemplaza todos los campos editables: omitir alguno da `400`. DELETE
elimina la fila, en lugar de confundirla con la desaparicion del personaje
por daño en el juego.

## Forma de probar (5–10 minutos)

1. Desde la raiz ejecutar `./mvnw test` y `./mvnw spring-boot:run`.
2. En Insomnia usar **Import → File** e importar la coleccion del repositorio.
   En Scratch Pad 13.4.0, si faltan variables, agregarlas con Ctrl+E como Text:
   `base_url`, `entidad_id`, `creeper_id`, `zombie_id`, `id_inexistente`.
   La URL base es `http://localhost:8080` y el ID inexistente es
   `9223372036854775807`; los otros IDs se copian de las respuestas de creacion.
3. Ejecutar GET `/` y POST Zombie; copiar el `id` al entorno `entidad_id`.
4. Listar y consultar el ID; actualizar con PUT y repetir GET: vida 15 y
   posicion (4, -2).
5. Detener y reiniciar el servicio desde la misma carpeta. El GET conserva
   el estado porque H2 usa `jdbc:h2:file:./data/minecraft`.
6. Ejecutar el POST ilegal (Cerdo con vida 11): `400`. Repetir la lista y
   verificar que no crecio con una fila invalida.
7. Crear el Cerdo valido y consultar `/api/minecraft/entidades/sonidos`:
   aparecen mensajes de ambas familias usando la misma interfaz.
8. Consultar el ID inexistente: `404`. Borrar el Zombie: `204` y GET posterior `404`.
9. Ejecutar 12 (crear Creeper, copiar ID a `creeper_id`), 13 (Zombie, copiar ID
   a `zombie_id`) y 14 (Cerdo). Ejecutar 15 para simular y 18 para consultar el
   estado persistido: vidas finales 0, 11 y 4.
10. Ejecutar 17 (Creeper desaparecido), 19 (Zombie no puede explotar) y 20
    (sin tipo); comparar sus codigos JSON con la tabla siguiente.
11. Abrir `/h2-console`, URL JDBC anterior, usuario `sa`, contraseña vacia,
    y ejecutar `SELECT * FROM entidades ORDER BY id;` para ver lo persistido.

## Cierre de entrega

### Dos reglas de negocio: explosion

1. **Daño por proximidad:** el Creeper daña a Zombies y Cerdos vivos dentro de
   tres bloques, reduciendo el daño con la distancia sin dejar vida negativa.
2. **Consumo del detonador:** el Creeper vivo se carga y desaparece al explotar;
   no puede repetir la accion mientras siga desaparecido.

Participan Creeper, Zombie y Cerdo. El Creeper vivo se carga y explota; los
Zombies y Cerdos vivos hasta 3 bloques de distancia euclidea reciben
`12 - 3 * floor(distancia)` puntos de daño. La vida nunca baja de 0. El Creeper
queda desaparecido, no borrado, y no puede repetir la explosion. Un service
transaccional coordina el dominio y persiste los cambios de todas las entidades.

En Insomnia, ejecutar 12 (crear Creeper y copiar ID a `creeper_id`), 13 y 14
(crear objetivos), 15 (detonar), 16–18 (consultar y probar repeticion). Las vidas
esperadas son Creeper 0, Zombie 11 y Cerdo 4. El README detalla la regla.

### Excepciones y respuestas HTTP

| Excepcion | Pedido | HTTP | Codigo |
| --- | --- | --- | --- |
| `VidaInvalidaException` | 06 | 400 | `VIDA_INVALIDA` |
| `EntidadNoEncontradaException` | 09 | 404 | `ENTIDAD_NO_ENCONTRADA` |
| `EntidadDesaparecidaException` | 17 | 400 | `ENTIDAD_DESAPARECIDA` |
| `ExplosionInvalidaException` | 19 | 400 | `EXPLOSION_INVALIDA` |
| `ReglaMinecraftException` | 20 | 400 | `REGLA_MINECRAFT_INVALIDA` |

Cada tipo tiene un handler. Las excepciones heredan de `RuntimeException`:
son no comprobadas y provocan rollback por defecto al salir de la transaccion
Spring. A diferencia de las comprobadas, no obligan a capturar o declarar
`throws`. El dominio no conoce codigos HTTP; el advice realiza esa traduccion.

### Evidencias de la prueba realizada

El recorrido manual se realizo en Insomnia 13.4.0, con capturas compartidas por
el estudiante. El Zombie 7 se creo, actualizo, consulto tras reiniciar y borro.
La explosion del Creeper 9 dejo al Zombie 10 con vida 11 y al Cerdo 11 con
vida 4. GET y la tabla H2 confirmaron esas vidas; el Creeper quedo con vida 0.
Se observaron los cinco codigos de error anteriores.

El detalle se encuentra en [docs/EVIDENCIAS_POO_08.md](docs/EVIDENCIAS_POO_08.md).
Ese registro transcribe resultados de las capturas e incluye tres imagenes:
la tabla H2, la explosion y el error al repetirla, guardadas en `docs/evidencias/`.

### Pasos de publicacion

- Los cambios se registran en commits separados segun la consigna, con mensajes
   que expliquen dependencias/propiedades, entidades, repositorio, controller,
   reglas y simulacion, excepciones y handlers, coleccion y documentacion.
- Publicar los commits y reemplazar el pendiente de este documento por la URL
  exacta: `https://github.com/<usuario>/<repositorio>/commit/<hash-completo>`.
- Subir estas especificaciones a Classroom con el enlace al commit final.
- La importacion y el recorrido manual en Insomnia ya se realizaron; conservar
  las capturas como evidencia adicional si se adjuntan a Classroom.
