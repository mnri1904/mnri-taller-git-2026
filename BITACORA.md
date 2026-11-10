# Bitacora de asistencia de IA — POO-06, POO-07 y POO-08

## Identificacion

- Estudiante: Marcelo Nicolas Romero Insfran (`mnri1904`).
- Dominio: Minecraft.
- Marca del agente: **OpenCode**.
- Modelo exacto informado por la herramienta en esta continuacion:
  **`openai/gpt-6.1-sol`** (nombre: `gpt-6.1-sol`).
- Modalidad: asistencia para interpretar el enunciado, implementar cambios,
  explicar conceptos y verificar el proyecto. Se utilizo IA para generar y
  modificar codigo, pruebas y documentacion.

## Resumen de prompts y trabajo realizado

### 1. Interpretacion de la continuacion

Se solicito avanzar paso a paso sobre el repositorio del taller. Se compartieron
el enunciado POO-06, la rubrica y el template de paquetes. El agente comparo
estos requisitos con el proyecto existente e identifico los cambios pendientes:
paquetes, constructores sobrecargados, sobrecarga de un mensaje y documentacion.

### 2. Organizacion de paquetes

Se compartio la salida de la creacion de la rama `mnri-poo-06`. El agente movio
las clases a `domain` y `rest.controller`, ubico Application en el paquete raiz
y actualizo imports y pruebas. La compilacion limpia paso 11 pruebas.
El estudiante registro el cambio con el commit `b05ce08`.

### 3. Constructores sobrecargados

Se compartio el resultado del commit de paquetes para continuar al siguiente
paso. El agente agrego constructores simples y con vida y posicion inicial,
con delegacion mediante `this` y `super`, y modifico el controller para alimentar
el constructor con parametros URL. Se verificaron valores validos, limites de
vida y respuestas HTTP 400: 21 pruebas aprobadas.
El estudiante registro el cambio con el commit `ca02c8c`.

### 4. Sobrecarga de un mensaje del dominio

Se compartio el commit de constructores para continuar. El agente implemento
`recibirDanio(int cantidad, int proteccion)` junto con la firma existente de
un argumento. La proteccion descuenta puntos del golpe y la validacion queda
en el dominio. Se expuso como parametro opcional del endpoint de danio.
Las pruebas verificaron absorcion parcial y total, argumentos invalidos,
desaparicion y ambas firmas por HTTP: 29 pruebas aprobadas.
El estudiante registro el cambio con el commit `7fe99db`.

### 5. Documentacion

Se compartio el commit de sobrecarga para continuar con el README y la bitacora.
El agente actualizo el README con licencia Apache 2.0, organizacion de paquetes,
constructores, diferencia entre sobrecarga y sobreescritura, ejemplos HTTP y
diagrama Mermaid. Tambien redacto esta bitacora como resumen de la asistencia,
sin copiar un transcript de chat.

### 6. Interfaz compartida entre familias

Se solicito declarar en `domain` una capacidad compartida por dos clases de
familias distintas y devolver su mensaje mediante REST en JSON. Tras una
implementacion inicial en `mromero-tplp3-2026/Minecraft`, el estudiante compartio
una captura del proyecto `mnri-taller-git-2026` para ubicar el trabajo aqui.
El agente agrego `Sonoro`, la familia `Animal` y `Cerdo`, e implemento la misma
interfaz en el `Zombie` existente de la familia `Mob`. `SonidoController`
invoca `emitirSonido()` mediante una referencia `Sonoro`. Se agregaron pruebas
HTTP para ambos mensajes y para el tipo no soportado, junto con la documentacion.

### 7. Contrato visual para entidades dibujables

Se solicito agregar `UICapable` a todos los elementos dibujables del dominio.
El agente declaro los mensajes `imagen`, `posicion`, `sonido` y `velocidad`,
implemento el contrato en `Entidad` y completo el comportamiento en Creeper,
Zombie y Cerdo. La posicion se expone mediante el record inmutable `PosicionUI`.
Se documento el contrato y se agregaron pruebas de movimiento, estado de carga
y desaparicion a traves de la interfaz.

### 8. Consulta del contrato visual desde el navegador

Se pregunto como ver `UICapable` en el navegador. El agente agrego
`UIController`, que consulta imagen, posicion, sonido y velocidad mediante
la interfaz y los expone en `/api/minecraft/ui/{tipo}` como JSON. Se agregaron
pruebas HTTP para las tres entidades y el error por tipo desconocido.

### 9. CRUD REST en tres capas y H2 — 2026-10-28

El estudiante solicito un CRUD REST de `Entidad`, con arquitectura de tres
capas, validacion en un service y persistencia H2 mediante Spring Data. Como
habia varias copias del dominio, el agente pregunto en que proyecto trabajar
y el estudiante eligio `mnri-taller-git-2026`.

Con **OpenCode**, modelo **`openai/gpt-6.1-sol`**, se agregaron las dependencias
JPA/H2, el mapeo `SINGLE_TABLE` de la jerarquia existente, un repositorio para
`Entidad`, un service transaccional y un controller con DTOs y codigos HTTP.
La base de desarrollo es de archivo y las pruebas usan H2 en memoria. Se
documento el uso en `RUN.md` y se ejecuto `./mvnw test`: 54 pruebas aprobadas,
incluyendo 14 del CRUD. Los cambios previos del estudiante se conservaron.

### 10. Adecuacion al enunciado POO-08 — 2026-10-28

El estudiante pregunto por Insomnia y pidio usar esta sesion para realizar
`docs/ejercicio-poo-08-crud-h2-insomnia-2026-10-28.md` del clon local
`afq-taller-git-2024`. El agente leyo el enunciado y la rubrica, comparo sus
requisitos con el proyecto y completo la coleccion importable de Insomnia,
las respuestas de diseño del README, el diagrama de capas y las especificaciones
para Classroom.

Se agrego `findByVidaGreaterThan` como consulta derivada de Spring Data y un
endpoint que invoca `Sonoro.emitirSonido()` sobre Zombies y Cerdos persistidos,
de familias distintas. Se mantuvieron los endpoints anteriores de demostracion.
Se agrego una prueba del protocolo sobre datos persistidos. La coleccion fue
generada por el agente en formato de exportacion Insomnia v4; su importacion
y recorrido visual quedan como comprobacion manual del estudiante.

Verificacion final de esta etapa: `./mvnw test` aprobo 55 pruebas, sin fallos
ni errores. Ademas se ejecuto un recorrido HTTP automatizado leyendo los 12
pedidos del archivo de coleccion contra Spring Boot real, con H2 en un archivo
temporal: se comprobaron los codigos esperados, la ausencia de filas tras el
POST invalido, ambos mensajes de `Sonoro`, la consola H2 y la conservacion de
vida y posicion despues de detener y volver a iniciar el proceso Java.
Esto verifica los pedidos y la persistencia; no registra una importacion visual
en la aplicacion Insomnia. Los commits y su publicacion siguen pendientes.

### 11. Regla de negocio de explosiones — 2026-10-29

El estudiante pidio agregar una regla de negocio conectada a REST, con tres
clases del modelo participando en una simulacion de explosiones de Minecraft.
El agente propuso un Creeper persistido que daña a Zombies y Cerdos en un
radio de 3 bloques; el estudiante eligio daño decreciente con la distancia.

Con OpenCode y el modelo `openai/gpt-6.1-sol` se agrego `Creeper.detonarSobre`,
el resultado inmutable `ResultadoExplosion` y el endpoint POST
`/api/minecraft/entidades/{id}/explosion`. El service ejecuta todo en una
transaccion JPA. Se validan tipo y estado del detonador, radio y vida minima;
las muertes conservan la fila con vida cero. Se agregaron pruebas de dominio
y REST, y se extendio la coleccion con los pedidos 13–18 y el entorno `creeper_id`.
`./mvnw test` aprobo 60 pruebas sin fallos ni errores.
Se verificaron por HTTP los 18 pedidos de la coleccion contra un servidor real
con una base temporal H2 en archivo. Despues de reiniciar el proceso, se
confirmaron las vidas finales Creeper 0, Zombie 11 y Cerdo 4.

### 12. Jerarquia coherente de bloques — 2026-10-29

El estudiante solicito agregar una jerarquia coherente con Minecraft. Con
OpenCode y `openai/gpt-6.1-sol`, el agente agrego `Bloque` como base abstracta
y las hijas `Piedra` y `Tierra`, con resistencia privada, minado validado,
constructores simples y sobrecargados, y recursos distintos al romperse.
Ambas cumplen `UICapable` con posicion fija y velocidad cero; se expone su
lectura visual mediante el `UIController` existente. Se actualizo el README
con la diferencia entre la familia de bloques y la de entidades, ejemplos y
el diagrama Mermaid. Se agregaron pruebas de minado, bordes y respuestas HTTP.
Verificacion: `./mvnw test` aprobo 68 pruebas, sin fallos ni errores.

### 13. Correccion: la jerarquia solicitada era de excepciones

El estudiante aclaro que el prompt anterior estaba incompleto: necesitaba una
jerarquia de excepciones coherente con Minecraft, no una familia de bloques.
El agente retiro exclusivamente la familia Bloque/Piedra/Tierra y sus cambios
de demostracion agregados en el paso anterior. Ese registro se conserva arriba
como historial de la interpretacion corregida.

Con OpenCode y `openai/gpt-6.1-sol` se agrego `MinecraftException` como raiz,
`ReglaMinecraftException` y las especializaciones `VidaInvalidaException`,
`EntidadDesaparecidaException` y `ExplosionInvalidaException`. La excepcion
`EntidadNoEncontradaException` se movio de service a domain.exception y paso a
ser otra rama de la raiz. Se conectaron a los constructores, acciones de dominio
y service, y se centralizo la traduccion REST a 400/404 con un advice.
Se ajustaron pruebas para verificar los tipos especificos y se actualizo el
diagrama. La familia persistida y las reglas de explosion no se eliminaron.
Verificacion de la correccion: `./mvnw clean test` aprobo 62 pruebas sin
fallos ni errores, incluyendo el CRUD, los errores HTTP y la simulacion.

### 14. Handlers especificos y errores distinguibles

El estudiante pidio avanzar de a un paso y autorizo primero mejorar los errores
REST. Con OpenCode y `openai/gpt-6.1-sol` se agrego un handler por excepcion
concreta y un campo `codigo` distinto, conservando el mensaje `error` y los
estados HTTP previos. Se ajustaron las pruebas REST para comprobar cada codigo
y la documentacion de las respuestas. Esta etapa no cambia las reglas del juego
ni agrega pedidos a la coleccion de Insomnia.
Verificacion: `./mvnw test` aprobo las 62 pruebas, incluyendo las comprobaciones
de los cinco codigos de error.

### 15. Coleccion Insomnia con un caso por excepcion

El estudiante ejecuto las pruebas curl y compartio respuestas con los cinco
codigos esperados. Luego autorizo completar la coleccion. Con OpenCode y
`openai/gpt-6.1-sol` se agregaron los pedidos 19 (intentar detonar un Zombie)
y 20 (POST sin tipo), la variable zombie_id y descripciones de los cinco casos
de error. El ID inexistente se guarda como texto para evitar redondeo en clientes
JavaScript. Se actualizo la guia de importacion y ejecucion del README.
La importacion visual en Insomnia queda pendiente de realizar con el estudiante.

### 16. Acompañamiento en Insomnia y documentacion de evidencias

El estudiante solicito instrucciones de a un paso y compartio capturas de la
instalacion de Insomnia Desktop 13.4.0, la importacion, los pedidos y H2. Se
aclaro la diferencia entre Inso CLI y la aplicacion grafica. En Scratch Pad los
pedidos se importaron, pero las variables del entorno quedaron vacias; se
configuraron con Ctrl+E, filas Text y los IDs reales devueltos por el servicio.

Se observaron el CRUD del Zombie 7, la consulta posterior al reinicio con vida
15 y posicion (4,-2), el borrado y su 404. El protocolo Sonoro devolvio mensajes
de ambas familias. La explosion del Creeper 9 afecto al Zombie 10 (vida final
11) y al Cerdo 11 (vida final 4); el GET posterior y SELECT en H2 confirmaron
los resultados. Las capturas mostraron los cinco codigos de error esperados.

El estudiante autorizo actualizar la documentacion y usar las capturas si
era necesario. Con OpenCode y `openai/gpt-6.1-sol` se redacto el registro
`docs/EVIDENCIAS_POO_08.md` a partir de lo visible, sin inventar archivos de
imagen ni enlaces. Se completaron en README y especificaciones las dos reglas,
la justificacion de excepciones no comprobadas, los handlers y las instrucciones
reproducibles de Scratch Pad. No se modifico codigo ni se hicieron commits en
esta etapa. Las notas historicas de pasos previos que decian pendiente la
prueba visual quedan superadas por esta verificacion manual.

### 17. Incorporacion de capturas como archivos de evidencia

El estudiante quiso incluir las imagenes, no solo sus resultados transcritos.
Se explico que las capturas pegadas en el chat no estaban automaticamente
guardadas en el repositorio. El estudiante guardo tres PNG en `docs/evidencias/`:
`01-tabla-h2.png`, `02-explosion.png` y `03-error-creeper-desaparecido.png`.
El agente verifico visualmente los tres archivos e inserto sus enlaces de
imagen relativos en `docs/EVIDENCIAS_POO_08.md`, actualizando las especificaciones
de entrega. Son capturas reales aportadas por el estudiante, no generadas por IA.

### 18. Registro en commits separados para POO-08

Con autorizacion del estudiante, el agente ejecuto los comandos Git locales,
sin publicar cambios remotos. Se separaron las unidades pedidas por la consigna:

| Commit | Aporte |
| --- | --- |
| `83bf83c` | Dependencias JPA/H2 y propiedades de desarrollo. |
| `17181bd` | Familia persistible y contratos del dominio. |
| `3598e31` | Repositorio Spring Data y consulta de entidades vivas. |
| `c33e75b` | CRUD transaccional, lecturas de protocolos y pruebas. |
| `c6e646d` | Reglas y simulacion de explosiones persistida. |
| `9cce55e` | Excepciones, handlers especificos y codigos de error. |
| `7d2fa2f` | Coleccion Insomnia con 20 pedidos. |

Para separar cambios de archivos compartidos se prepararon versiones parciales
en el indice Git, sin sobrescribir los archivos de trabajo. Las versiones del
modelo, CRUD y simulacion se probaron en copias temporales del arbol preparado:
29, 55 y 60 pruebas aprobadas respectivamente. La version completa con
excepciones aprobo 62 pruebas. Se valido la estructura JSON de la coleccion.

El ultimo commit de esta secuencia incorpora README, RUN, esta bitacora,
especificaciones de Classroom y las tres capturas enlazadas. Su hash se obtiene
del historial, no se inventa dentro del mismo commit. La publicacion y el enlace
final de Classroom se completan despues de confirmar el envio a GitHub.

### 19. Publicacion personal del estudiante y cuarta captura

El estudiante prefirio ejecutar el push personalmente y dejar evidencia. Ejecuto
`git push origin mnri-poo-06` y compartio la salida exitosa
`5c46c46..2eccb40`. Luego guardo `docs/evidencias/04-publicacion-github.png`.
El agente leyo la captura, confirmo visualmente su contenido y agrego su enlace
al registro de evidencias. Se completo en las especificaciones la URL exacta
del commit publicado y se actualizo el estado del README.

Esta cuarta captura se incorpora despues de la publicacion original, por lo
que necesita un nuevo commit documental y otro push. El agente no ejecuto el
push: lo hizo el estudiante. No se considera entregado en Classroom hasta que
el estudiante lo envie alli.

## Alcance del registro

El proyecto anterior tambien se desarrollo con asistencia de IA, incluyendo
codigo, pruebas y apoyo con Git y pull requests. Esta bitacora detalla la
continuacion POO-06/07/08; los hashes citados permiten localizar sus pasos en el
historial. Los commits iniciales de POO-06 fueron ejecutados por el estudiante
en su terminal; los commits de POO-08 detallados en la seccion 18 fueron
ejecutados por el agente con autorizacion del estudiante.
