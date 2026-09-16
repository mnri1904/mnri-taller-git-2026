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
