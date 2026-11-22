# Bitácora de asistencia de IA — POO-06, POO-07 y POO-08

## Identificación

- Estudiante: Marcelo Nicolás Romero Insfrán (`mnri1904`).
- Dominio: Minecraft.
- Marca del agente: **OpenCode**.
- Modelo de las etapas 1–19:
  **`openai/gpt-6.1-sol`** (nombre: `gpt-6.1-sol`).
- Modalidad: asistencia para interpretar el enunciado, implementar cambios,
  explicar conceptos y verificar el proyecto. Se utilizó IA para generar y
  modificar código, pruebas y documentación.

## Resumen de prompts y trabajo realizado

### 1. Interpretación de la continuación

Se solicitó avanzar paso a paso sobre el repositorio del taller. Se compartieron
el enunciado POO-06, la rúbrica y el template de paquetes. El agente comparó
estos requisitos con el proyecto existente e identificó los cambios pendientes:
paquetes, constructores sobrecargados, sobrecarga de un mensaje y documentación.

### 2. Organización de paquetes

Se compartió la salida de la creación de la rama `mnri-poo-06`. El agente movió
las clases a `domain` y `rest.controller`, ubicó Application en el paquete raíz
y actualizó imports y pruebas. La compilación limpia pasó 11 pruebas.
El estudiante registró el cambio con el commit `b05ce08`.

### 3. Constructores sobrecargados

Se compartió el resultado del commit de paquetes para continuar al siguiente
paso. El agente agregó constructores simples y con vida y posición inicial,
con delegación mediante `this` y `super`, y modificó el controller para alimentar
el constructor con parámetros URL. Se verificaron valores válidos, límites de
vida y respuestas HTTP 400: 21 pruebas aprobadas.
El estudiante registró el cambio con el commit `ca02c8c`.

### 4. Sobrecarga de un mensaje del dominio

Se compartió el commit de constructores para continuar. El agente implementó
`recibirDanio(int cantidad, int proteccion)` junto con la firma existente de
un argumento. La protección descuenta puntos del golpe y la validación queda
en el dominio. Se expuso como parámetro opcional del endpoint de daño.
Las pruebas verificaron absorción parcial y total, argumentos inválidos,
desaparición y ambas firmas por HTTP: 29 pruebas aprobadas.
El estudiante registró el cambio con el commit `7fe99db`.

### 5. Documentación

Se compartió el commit de sobrecarga para continuar con el README y la bitácora.
El agente actualizó el README con licencia Apache 2.0, organización de paquetes,
constructores, diferencia entre sobrecarga y sobreescritura, ejemplos HTTP y
diagrama Mermaid. También redactó esta bitácora como resumen de la asistencia,
sin copiar un transcript de chat.

### 6. Interfaz compartida entre familias

Se solicitó declarar en `domain` una capacidad compartida por dos clases de
familias distintas y devolver su mensaje mediante REST en JSON. Tras una
implementación inicial en `mromero-tplp3-2026/Minecraft`, el estudiante compartió
una captura del proyecto `mnri-taller-git-2026` para ubicar el trabajo aquí.
El agente agregó `Sonoro`, la familia `Animal` y `Cerdo`, e implementó la misma
interfaz en el `Zombie` existente de la familia `Mob`. `SonidoController`
invoca `emitirSonido()` mediante una referencia `Sonoro`. Se agregaron pruebas
HTTP para ambos mensajes y para el tipo no soportado, junto con la documentación.

### 7. Contrato visual para entidades dibujables

Se solicitó agregar `UICapable` a todos los elementos dibujables del dominio.
El agente declaró los mensajes `imagen`, `posicion`, `sonido` y `velocidad`,
implementó el contrato en `Entidad` y completó el comportamiento en Creeper,
Zombie y Cerdo. La posición se expone mediante el record inmutable `PosicionUI`.
Se documentó el contrato y se agregaron pruebas de movimiento, estado de carga
y desaparición a través de la interfaz.

### 8. Consulta del contrato visual desde el navegador

Se preguntó cómo ver `UICapable` en el navegador. El agente agregó
`UIController`, que consulta imagen, posición, sonido y velocidad mediante
la interfaz y los expone en `/api/minecraft/ui/{tipo}` como JSON. Se agregaron
pruebas HTTP para las tres entidades y el error por tipo desconocido.

### 9. CRUD REST en tres capas y H2 — 2026-10-28

El estudiante solicitó un CRUD REST de `Entidad`, con arquitectura de tres
capas, validación en un service y persistencia H2 mediante Spring Data. Como
había varias copias del dominio, el agente preguntó en qué proyecto trabajar
y el estudiante eligió `mnri-taller-git-2026`.

Con **OpenCode**, modelo **`openai/gpt-6.1-sol`**, se agregaron las dependencias
JPA/H2, el mapeo `SINGLE_TABLE` de la jerarquía existente, un repositorio para
`Entidad`, un service transaccional y un controller con DTOs y códigos HTTP.
La base de desarrollo es de archivo y las pruebas usan H2 en memoria. Se
documentó el uso en `RUN.md` y se ejecutó `./mvnw test`: 54 pruebas aprobadas,
incluyendo 14 del CRUD. Los cambios previos del estudiante se conservaron.

### 10. Adecuación al enunciado POO-08 — 2026-10-28

El estudiante preguntó por Insomnia y pidió usar esta sesión para realizar
`docs/ejercicio-poo-08-crud-h2-insomnia-2026-10-28.md` del clon local
`afq-taller-git-2024`. El agente leyó el enunciado y la rúbrica, comparó sus
requisitos con el proyecto y completó la colección importable de Insomnia,
las respuestas de diseño del README, el diagrama de capas y las especificaciones
para Classroom.

Se agregó `findByVidaGreaterThan` como consulta derivada de Spring Data y un
endpoint que invoca `Sonoro.emitirSonido()` sobre Zombies y Cerdos persistidos,
de familias distintas. Se mantuvieron los endpoints anteriores de demostración.
Se agregó una prueba del protocolo sobre datos persistidos. La colección fue
generada por el agente en formato de exportación Insomnia v4; su importación
y recorrido visual quedan como comprobación manual del estudiante.

Verificación final de esta etapa: `./mvnw test` aprobó 55 pruebas, sin fallos
ni errores. Además se ejecutó un recorrido HTTP automatizado leyendo los 12
pedidos del archivo de colección contra Spring Boot real, con H2 en un archivo
temporal: se comprobaron los códigos esperados, la ausencia de filas tras el
POST inválido, ambos mensajes de `Sonoro`, la consola H2 y la conservación de
vida y posición después de detener y volver a iniciar el proceso Java.
Esto verifica los pedidos y la persistencia; no registra una importación visual
en la aplicación Insomnia. Los commits y su publicación siguen pendientes.

### 11. Regla de negocio de explosiones — 2026-10-29

El estudiante pidió agregar una regla de negocio conectada a REST, con tres
clases del modelo participando en una simulación de explosiones de Minecraft.
El agente propuso un Creeper persistido que daña a Zombies y Cerdos en un
radio de 3 bloques; el estudiante eligió daño decreciente con la distancia.

Con OpenCode y el modelo `openai/gpt-6.1-sol` se agregó `Creeper.detonarSobre`,
el resultado inmutable `ResultadoExplosion` y el endpoint POST
`/api/minecraft/entidades/{id}/explosion`. El service ejecuta todo en una
transacción JPA. Se validan tipo y estado del detonador, radio y vida mínima;
las muertes conservan la fila con vida cero. Se agregaron pruebas de dominio
y REST, y se extendió la colección con los pedidos 13–18 y el entorno `creeper_id`.
`./mvnw test` aprobó 60 pruebas sin fallos ni errores.
Se verificaron por HTTP los 18 pedidos de la colección contra un servidor real
con una base temporal H2 en archivo. Después de reiniciar el proceso, se
confirmaron las vidas finales Creeper 0, Zombie 11 y Cerdo 4.

### 12. Jerarquía coherente de bloques — 2026-10-29

El estudiante solicitó agregar una jerarquía coherente con Minecraft. Con
OpenCode y `openai/gpt-6.1-sol`, el agente agregó `Bloque` como base abstracta
y las hijas `Piedra` y `Tierra`, con resistencia privada, minado validado,
constructores simples y sobrecargados, y recursos distintos al romperse.
Ambas cumplen `UICapable` con posición fija y velocidad cero; se expone su
lectura visual mediante el `UIController` existente. Se actualizó el README
con la diferencia entre la familia de bloques y la de entidades, ejemplos y
el diagrama Mermaid. Se agregaron pruebas de minado, bordes y respuestas HTTP.
Verificación: `./mvnw test` aprobó 68 pruebas, sin fallos ni errores.

### 13. Corrección: la jerarquía solicitada era de excepciones

El estudiante aclaró que el prompt anterior estaba incompleto: necesitaba una
jerarquía de excepciones coherente con Minecraft, no una familia de bloques.
El agente retiró exclusivamente la familia Bloque/Piedra/Tierra y sus cambios
de demostración agregados en el paso anterior. Ese registro se conserva arriba
como historial de la interpretación corregida.

Con OpenCode y `openai/gpt-6.1-sol` se agregó `MinecraftException` como raíz,
`ReglaMinecraftException` y las especializaciones `VidaInvalidaException`,
`EntidadDesaparecidaException` y `ExplosionInvalidaException`. La excepción
`EntidadNoEncontradaException` se movió de service a domain.exception y pasó a
ser otra rama de la raíz. Se conectaron a los constructores, acciones de dominio
y service, y se centralizó la traducción REST a 400/404 con un advice.
Se ajustaron pruebas para verificar los tipos específicos y se actualizó el
diagrama. La familia persistida y las reglas de explosión no se eliminaron.
Verificación de la corrección: `./mvnw clean test` aprobó 62 pruebas sin
fallos ni errores, incluyendo el CRUD, los errores HTTP y la simulación.

### 14. Handlers específicos y errores distinguibles

El estudiante pidió avanzar paso a paso y autorizó primero mejorar los errores
REST. Con OpenCode y `openai/gpt-6.1-sol` se agregó un handler por excepción
concreta y un campo `codigo` distinto, conservando el mensaje `error` y los
estados HTTP previos. Se ajustaron las pruebas REST para comprobar cada código
y la documentación de las respuestas. Esta etapa no cambia las reglas del juego
ni agrega pedidos a la colección de Insomnia.
Verificación: `./mvnw test` aprobó las 62 pruebas, incluyendo las comprobaciones
de los cinco códigos de error.

### 15. Colección Insomnia con un caso por excepción

El estudiante ejecutó las pruebas curl y compartió respuestas con los cinco
códigos esperados. Luego autorizó completar la colección. Con OpenCode y
`openai/gpt-6.1-sol` se agregaron los pedidos 19 (intentar detonar un Zombie)
y 20 (POST sin tipo), la variable zombie_id y descripciones de los cinco casos
de error. El ID inexistente se guarda como texto para evitar redondeo en clientes
JavaScript. Se actualizó la guía de importación y ejecución del README.
La importación visual en Insomnia queda pendiente de realizar con el estudiante.

### 16. Acompañamiento en Insomnia y documentación de evidencias

El estudiante solicitó instrucciones paso a paso y compartió capturas de la
instalación de Insomnia Desktop 13.4.0, la importación, los pedidos y H2. Se
aclaró la diferencia entre Inso CLI y la aplicación gráfica. En Scratch Pad los
pedidos se importaron, pero las variables del entorno quedaron vacías; se
configuraron con Ctrl+E, filas Text y los IDs reales devueltos por el servicio.

Se observaron el CRUD del Zombie 7, la consulta posterior al reinicio con vida
15 y posición (4,-2), el borrado y su 404. El protocolo Sonoro devolvió mensajes
de ambas familias. La explosión del Creeper 9 afectó al Zombie 10 (vida final
11) y al Cerdo 11 (vida final 4); el GET posterior y SELECT en H2 confirmaron
los resultados. Las capturas mostraron los cinco códigos de error esperados.

El estudiante autorizó actualizar la documentación y usar las capturas si
era necesario. Con OpenCode y `openai/gpt-6.1-sol` se redactó el registro
`docs/EVIDENCIAS_POO_08.md` a partir de lo visible, sin inventar archivos de
imagen ni enlaces. Se completaron en README y especificaciones las dos reglas,
la justificación de excepciones no comprobadas, los handlers y las instrucciones
reproducibles de Scratch Pad. No se modificó código ni se hicieron commits en
esta etapa. Las notas históricas de pasos previos que decían pendiente la
prueba visual quedan superadas por esta verificación manual.

### 17. Incorporación de capturas como archivos de evidencia

El estudiante quiso incluir las imágenes, no solo sus resultados transcritos.
Se explicó que las capturas pegadas en el chat no estaban automáticamente
guardadas en el repositorio. El estudiante guardó tres PNG en `docs/evidencias/`:
`01-tabla-h2.png`, `02-explosion.png` y `03-error-creeper-desaparecido.png`.
El agente verificó visualmente los tres archivos e insertó sus enlaces de
imagen relativos en `docs/EVIDENCIAS_POO_08.md`, actualizando las especificaciones
de entrega. Son capturas reales aportadas por el estudiante, no generadas por IA.

### 18. Registro en commits separados para POO-08

Con autorización del estudiante, el agente ejecutó los comandos Git locales,
sin publicar cambios remotos. Se separaron las unidades pedidas por la consigna:

| Commit | Aporte |
| --- | --- |
| `83bf83c` | Dependencias JPA/H2 y propiedades de desarrollo. |
| `17181bd` | Familia persistible y contratos del dominio. |
| `3598e31` | Repositorio Spring Data y consulta de entidades vivas. |
| `c33e75b` | CRUD transaccional, lecturas de protocolos y pruebas. |
| `c6e646d` | Reglas y simulación de explosiones persistida. |
| `9cce55e` | Excepciones, handlers específicos y códigos de error. |
| `7d2fa2f` | Colección Insomnia con 20 pedidos. |

Para separar cambios de archivos compartidos se prepararon versiones parciales
en el índice Git, sin sobrescribir los archivos de trabajo. Las versiones del
modelo, CRUD y simulación se probaron en copias temporales del árbol preparado:
29, 55 y 60 pruebas aprobadas respectivamente. La versión completa con
excepciones aprobó 62 pruebas. Se validó la estructura JSON de la colección.

El último commit de esta secuencia incorpora README, RUN, esta bitácora,
especificaciones de Classroom y las tres capturas enlazadas. Su hash se obtiene
del historial, no se inventa dentro del mismo commit. La publicación y el enlace
final de Classroom se completan después de confirmar el envío a GitHub.

### 19. Publicación personal del estudiante y cuarta captura

El estudiante prefirió ejecutar el push personalmente y dejar evidencia. Ejecutó
`git push origin mnri-poo-06` y compartió la salida exitosa
`5c46c46..2eccb40`. Luego guardó `docs/evidencias/04-publicacion-github.png`.
El agente leyó la captura, confirmó visualmente su contenido y agregó su enlace
al registro de evidencias. Se completó en las especificaciones la URL exacta
del commit publicado y se actualizó el estado del README.

La cuarta captura quedó en `9c4c35d`. El profesor evaluó ese commit.

### 20. Correcciones de POO-08 — 22 de noviembre de 2026

El estudiante pidió revisar la devolución de Classroom, evaluar el estado del
repositorio y corregir POO-08 antes de comenzar POO-09. Herramienta: OpenCode;
modelo: `openai/gpt-6.2-astra`. El agente reprodujo el PUT que revivía al
Creeper: la primera ejecución de la prueba esperaba 400 y obtuvo 200. Se
trasladaron las reglas al servicio y se comprobó el estado persistido antes
de actualizar. Se revisaron las guías, el diagrama y la descripción de los 20
pedidos de Insomnia. Se conservan arriba los resultados de cada etapa histórica.

Verificación: 63 pruebas aprobadas con `./mvnw clean test`. Se reprodujeron
los 20 pedidos de la colección por HTTP, con un servidor real y una base H2
temporal en archivo. Se verificaron el reinicio, la explosión con vidas
finales 0, 11 y 4 y el rechazo del PUT sin cambios de vida ni posición.

### 21. POO-09: contrato OpenAPI de A1

Marcelo acordó los roles de su entrega y preguntó qué es OpenAPI y si podía
prepararlo antes de recibir las APIs de sus compañeros. Con OpenCode
(`openai/gpt-6.2-astra`) se describieron el CRUD, los sonidos, la explosión
y sus errores en `openapi.json` a partir de los controladores y del servicio.
El README indica cómo leer el contrato e importarlo en Insomnia.

### 22. Publicación con Swagger UI — POO-09

Marcelo comparó el contrato estático con el enfoque propuesto por Elias y pidió
generarlo con `springdoc-openapi`. Con OpenCode (`openai/gpt-6.2-astra`) se
incorporó la versión 2.8.17 a Spring Boot 3.5.16. Las anotaciones del controlador
documentan el CRUD, las dos reglas, sus efectos en H2 y los errores. Swagger UI
e Insomnia consumen el mismo contrato; `/openapi.json` conserva el enlace anterior.

Verificación: 63 pruebas aprobadas con `./mvnw clean test`. Con el servidor
iniciado mediante `./mvnw spring-boot:run` y H2 en memoria se consultaron Swagger UI,
su configuración y los documentos JSON/YAML. Los ejemplos de creación y explosión
dejaron vidas 0, 11 y 4; el Cerdo con vida 11, una segunda explosión y el intento
de detonar un Zombie devolvieron 400. Inso CLI validó el JSON sin observaciones.

## Alcance del registro

El proyecto anterior también se desarrolló con asistencia de IA, incluyendo
código, pruebas y apoyo con Git y pull requests. Esta bitácora detalla la
continuación POO-06/07/08; los hashes citados permiten localizar sus pasos en el
historial. Los commits iniciales de POO-06 fueron ejecutados por el estudiante
en su terminal; los commits de POO-08 detallados en la sección 18 fueron
ejecutados por el agente con autorización del estudiante.
