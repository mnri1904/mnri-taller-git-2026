# Especificaciones de entrega — POO-08

Este documento describe la entrega original. Las modificaciones posteriores a
la devolución se detallan en [Correcciones de POO-08](README.md#correcciones-de-poo-08).

- Estudiante: Marcelo Nicolás Romero Insfrán (`mnri1904`).
- Asignatura: Lenguaje de Programación 3, CYT646 F.
- Fecha del enunciado: 28 y 29 de octubre de 2026; revisión del profesor del 3 de noviembre.
- Dominio: Minecraft.
- Proyecto: `mnri-taller-git-2026`.
- Commit de la solución publicada (código, colección, documentación y tres capturas):
  https://github.com/mnri1904/mnri-taller-git-2026/commit/2eccb407d30bafbd0b8ba630c6fd398336aaaa06

## Objetivo aplicado al dominio

Publicar una API REST que guarde entidades de Minecraft en H2, permita crear,
listar, consultar, actualizar y eliminar registros, simule explosiones con dos
reglas propias y use un protocolo común entre familias distintas. Los errores
se expresan con una jerarquía de excepciones y un handler por tipo concreto.

## Consignas implementadas

1. Spring Web, Spring Data JPA y H2, con Java 21 y Maven Wrapper.
2. Familia persistida `Entidad → Mob → Creeper/Zombie` y
   `Entidad → Animal → Cerdo`. Herencia JPA `SINGLE_TABLE` con discriminador `tipo`.
3. Arquitectura de tres capas: controller HTTP, service transaccional con
   validaciones y repositorio Spring Data de la clase base.
4. Protocolo `Sonoro` implementado por Zombie (Mob) y Cerdo (Animal).
5. Colección `insomnia/poo-08-minecraft.json` con entorno, CRUD, error de
   invariante, ID inexistente, lectura mediante la interfaz, simulación y un
   caso por cada tipo de excepción: 20 pedidos en total.
6. README con propiedades H2, decisiones, diagrama Mermaid, sobrecarga y
    sobreescritura; `BITACORA.md` con el uso de OpenCode y el modelo exacto.
7. Dos reglas: daño por proximidad y consumo del detonador. En la versión
   evaluada se ejecutaban en el dominio; tras la corrección las aplica
   `EntidadService` dentro de la misma transacción.
8. Excepciones no comprobadas bajo `MinecraftException`; handlers específicos
   que distinguen cada error con los campos JSON `codigo` y `error`.

## URLs y resultados

Base: `http://localhost:8080`.

| Método | Ruta | Resultado |
| --- | --- | --- |
| GET | `/` | `200`: confirma que el servicio vive |
| POST | `/api/minecraft/entidades` | `201`, ID generado y `Location` |
| GET | `/api/minecraft/entidades` | `200`, lista persistida |
| GET | `/api/minecraft/entidades/{id}` | `200` o `404` |
| PUT | `/api/minecraft/entidades/{id}` | `200`, actualización persistida; `400` o `404` ante error |
| DELETE | `/api/minecraft/entidades/{id}` | `204`, borrado físico; `404` si no existe |
| GET | `/api/minecraft/entidades/sonidos` | `200`, mensajes mediante `Sonoro` |
| POST | `/api/minecraft/entidades/{id}/explosion` | `200`, simulación de explosión y daño persistido |

## Invariantes

POST y PUT requieren `tipo`, `vida`, `posicionX` y `posicionZ`. La vida debe
estar entre 1 y 20 para Zombie/Creeper o entre 1 y 10 para Cerdo. El tipo debe
ser conocido y no puede cambiar durante una actualización. La identidad y la
vida máxima no son campos editables. Las coordenadas pueden ser negativas.
En la entrega original, los constructores y `actualizarEstado` validaban el
rango de vida, pero el PUT permitía revivir al Creeper. En la corrección,
el servicio valida tanto el estado persistido como los datos recibidos antes
de modificar la fila.

PUT reemplaza todos los campos editables: omitir alguno da `400`. DELETE
elimina la fila, en lugar de confundirla con la desaparición del personaje
por daño en el juego.

## Forma de probar (5–10 minutos)

1. Desde la raíz ejecutar `./mvnw test` y `./mvnw spring-boot:run`.
2. En Insomnia usar **Import → File** e importar la colección del repositorio.
   En Scratch Pad 13.4.0, si faltan variables, agregarlas con Ctrl+E como Text:
   `base_url`, `entidad_id`, `creeper_id`, `zombie_id`, `id_inexistente`.
   La URL base es `http://localhost:8080` y el ID inexistente es
   `9223372036854775807`; los otros IDs se copian de las respuestas de creación.
3. Ejecutar GET `/` y POST Zombie; copiar el `id` al entorno `entidad_id`.
4. Listar y consultar el ID; actualizar con PUT y repetir GET: vida 15 y
   posición (4, -2).
5. Detener y reiniciar el servicio desde la misma carpeta. El GET conserva
   el estado porque H2 usa `jdbc:h2:file:./data/minecraft`.
6. Ejecutar el POST ilegal (Cerdo con vida 11): `400`. Repetir la lista y
   verificar que no creció con una fila inválida.
7. Crear el Cerdo válido y consultar `/api/minecraft/entidades/sonidos`:
   aparecen mensajes de ambas familias usando la misma interfaz.
8. Consultar el ID inexistente: `404`. Borrar el Zombie: `204` y GET posterior `404`.
9. Ejecutar 12 (crear Creeper, copiar ID a `creeper_id`), 13 (Zombie, copiar ID
   a `zombie_id`) y 14 (Cerdo). Ejecutar 15 para simular y 18 para consultar el
   estado persistido: vidas finales 0, 11 y 4.
10. Ejecutar 17 (Creeper desaparecido), 19 (Zombie no puede explotar) y 20
    (sin tipo); comparar sus códigos JSON con la tabla siguiente.
11. Abrir `/h2-console`, URL JDBC anterior, usuario `sa`, contraseña vacía,
    y ejecutar `SELECT * FROM entidades ORDER BY id;` para ver lo persistido.

## Cierre de entrega

### Dos reglas de negocio: explosión

1. **Daño por proximidad:** el Creeper daña a Zombies y Cerdos vivos dentro de
   tres bloques, reduciendo el daño con la distancia sin dejar vida negativa.
2. **Consumo del detonador:** el Creeper vivo se carga y desaparece al explotar;
   no puede repetir la acción mientras siga desaparecido.

Participan Creeper, Zombie y Cerdo. El Creeper vivo se carga y explota; los
Zombies y Cerdos vivos hasta 3 bloques de distancia euclídea reciben
`12 - 3 * floor(distancia)` puntos de daño. La vida nunca baja de 0. El Creeper
queda desaparecido, no borrado, y no puede repetir la explosión. Un service
transaccional coordina el dominio y persiste los cambios de todas las entidades.

En Insomnia, ejecutar 12 (crear Creeper y copiar ID a `creeper_id`), 13 y 14
(crear objetivos), 15 (detonar), 16–18 (consultar y probar repetición). Las vidas
esperadas son Creeper 0, Zombie 11 y Cerdo 4. El README detalla la regla.

### Excepciones y respuestas HTTP

| Excepción | Pedido | HTTP | Código |
| --- | --- | --- | --- |
| `VidaInvalidaException` | 06 | 400 | `VIDA_INVALIDA` |
| `EntidadNoEncontradaException` | 09 | 404 | `ENTIDAD_NO_ENCONTRADA` |
| `EntidadDesaparecidaException` | 17 | 400 | `ENTIDAD_DESAPARECIDA` |
| `ExplosionInvalidaException` | 19 | 400 | `EXPLOSION_INVALIDA` |
| `ReglaMinecraftException` | 20 | 400 | `REGLA_MINECRAFT_INVALIDA` |

Cada tipo tiene un handler. Las excepciones heredan de `RuntimeException`:
son no comprobadas y provocan rollback por defecto al salir de la transacción
Spring. A diferencia de las comprobadas, no obligan a capturar o declarar
`throws`. El dominio no conoce códigos HTTP; el advice realiza esa traducción.

### Evidencias de la prueba realizada

El recorrido manual se realizó en Insomnia 13.4.0, con capturas compartidas por
el estudiante. El Zombie 7 se creó, actualizó, consultó tras reiniciar y borró.
La explosión del Creeper 9 dejó al Zombie 10 con vida 11 y al Cerdo 11 con
vida 4. GET y la tabla H2 confirmaron esas vidas; el Creeper quedó con vida 0.
Se observaron los cinco códigos de error anteriores.

El detalle se encuentra en [docs/EVIDENCIAS_POO_08.md](docs/EVIDENCIAS_POO_08.md).
Ese registro transcribe resultados de las capturas e incluye cuatro imágenes:
la tabla H2, la explosión, el error al repetirla y la publicación en GitHub,
guardadas en `docs/evidencias/`. La cuarta se incorpora en una actualización
documental posterior al commit de solución enlazado arriba.

### Publicación y evaluación

El estudiante publicó la solución en `mnri-poo-06`. La actualización documental
con la cuarta captura quedó en el commit `9c4c35d`, el mismo que evaluó la
cátedra en Classroom. El resultado de la entrega original fue 17/24.
