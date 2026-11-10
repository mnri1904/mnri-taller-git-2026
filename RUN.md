# Instrucciones de ejecucion

Requisitos: JDK 21.

```bash
./mvnw spring-boot:run
```

La API escucha en el puerto `8080`.

## Verificacion

Ejecutar las pruebas automatizadas antes de iniciar la aplicacion:

```bash
./mvnw test
```

Con la API iniciada, consultar el estado del Creeper:

```bash
curl http://localhost:8080/api/minecraft/creeper
```

## CRUD REST de Entidad

El CRUD usa tres capas: `EntidadController` (HTTP y DTOs), `EntidadService`
(reglas de negocio y transacciones) y `EntidadRepository` (Spring Data JPA).
La clase abstracta `Entidad` y sus subtipos se persisten en la tabla `entidades`
con herencia JPA `SINGLE_TABLE` y una columna discriminadora `tipo`.

Base de la API: `http://localhost:8080/api/minecraft/entidades`.

| Metodo | Ruta | Resultado |
| --- | --- | --- |
| POST | `/api/minecraft/entidades` | Crea: `201 Created`, JSON y cabecera `Location` |
| GET | `/api/minecraft/entidades` | Lista: `200 OK` y arreglo JSON |
| GET | `/api/minecraft/entidades/{id}` | Consulta: `200 OK` |
| PUT | `/api/minecraft/entidades/{id}` | Actualiza: `200 OK` |
| DELETE | `/api/minecraft/entidades/{id}` | Elimina: `204 No Content` |
| GET | `/api/minecraft/entidades/sonidos` | Lista mensajes de entidades persistidas mediante `Sonoro` |

Crear una entidad:

```bash
curl -i -X POST http://localhost:8080/api/minecraft/entidades \
  -H 'Content-Type: application/json' \
  -d '{"tipo":"Zombie","vida":20,"posicionX":0,"posicionZ":0}'
```

Ejemplo de respuesta (el ID es generado por H2):

```json
{"id":1,"tipo":"Zombie","vida":20,"vidaMaxima":20,"posicionX":0,"posicionZ":0,"viva":true}
```

Consultar, actualizar y eliminar (reemplazar `1` por el ID recibido):

```bash
curl http://localhost:8080/api/minecraft/entidades
curl http://localhost:8080/api/minecraft/entidades/1
curl -X PUT http://localhost:8080/api/minecraft/entidades/1 \
  -H 'Content-Type: application/json' \
  -d '{"tipo":"Zombie","vida":15,"posicionX":4,"posicionZ":-2}'
curl -i -X DELETE http://localhost:8080/api/minecraft/entidades/1
```

### Validaciones

- `tipo`, `vida`, `posicionX` y `posicionZ` son obligatorios en POST y PUT.
- Tipos permitidos: `Zombie`, `Creeper` y `Cerdo` (sin distinguir mayusculas).
- Vida entre 1 y 20 para Zombie/Creeper; entre 1 y 10 para Cerdo.
- El tipo de una entidad existente no puede cambiarse.
- Las validaciones responden `400 Bad Request` con `{"error":"mensaje"}`.
- Consultar, actualizar o eliminar un ID inexistente responde `404 Not Found`.

### Base H2

Los datos se conservan entre reinicios en `./data/minecraft.mv.db`, relativo al
directorio desde el que se inicia la aplicacion. Hibernate actualiza el esquema
con `spring.jpa.hibernate.ddl-auto=update`.

Consola: <http://localhost:8080/h2-console>. Usar URL JDBC
`jdbc:h2:file:./data/minecraft`, usuario `sa` y contraseña vacia.

Las pruebas usan una base H2 en memoria independiente y verifican el CRUD,
la reconstruccion de los tres subtipos desde la base, validaciones y errores 404.

## Insomnia (POO-08)

Importar `insomnia/poo-08-minecraft.json` con **Import → File**. El entorno
incluye `base_url`, `entidad_id`, `creeper_id`, `zombie_id` e `id_inexistente`.
En Scratch Pad 13.4.0, si no se cargan, presionar Ctrl+E y agregar esas variables
como Text: URL `http://localhost:8080`, ID inexistente `9223372036854775807`
y los IDs que devuelvan los POST. Ejecutar los pedidos en
orden; tras crear el Zombie, copiar el ID recibido a `entidad_id`. Repetir GET
despues de PUT, despues del POST invalido y despues de reiniciar el servicio.
Crear el Cerdo antes de consultar sonidos. Borrar el Zombie al terminar y
confirmar el 404 posterior. El recorrido detallado esta en el README.

## Simular una explosion

Reiniciar el servicio si estaba ejecutandose antes de incorporar el endpoint.
En Insomnia, el pedido 12 crea un Creeper en (5,5): copiar su ID a `creeper_id`.
Los pedidos 13 y 14 crean Zombie y Cerdo en (6,5) y (7,5). El 15 ejecuta:

```bash
curl -X POST http://localhost:8080/api/minecraft/entidades/ID_CREEPER/explosion
```

Reemplazar `ID_CREEPER` por el ID recibido. Responde `200`: el Creeper queda
con vida 0, el Zombie con 11 y el Cerdo con 4. El radio es 3 bloques y el daño
disminuye con la distancia. Los pedidos 16–18 consultan los cambios y verifican
que repetir la explosion devuelve `400`. Todo el estado queda guardado en H2.

El pedido 19 intenta detonar al Zombie (`zombie_id` copiado del pedido 13):
400/EXPLOSION_INVALIDA. El pedido 20 envia `{}`: 400/REGLA_MINECRAFT_INVALIDA.
El caso de vida invalida esta en el 06 y el de ID inexistente en el 09.

El recorrido manual ya se verifico en Insomnia y H2. Ver
`docs/EVIDENCIAS_POO_08.md` para los resultados observados en las capturas.
