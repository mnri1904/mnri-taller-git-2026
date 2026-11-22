# Bitácora del Taller de Git y Modelado Orientado a Objetos

Registro histórico del taller de septiembre. El diseño vigente de POO-08 se
describe en el [README](README.md).

## Datos de la entrega

- **Estudiante:** Marcelo Nicolás Romero Insfrán
- **Usuario de GitHub:** `mnri1904`
- **Comisión:** CYT646 F
- **Dominio elegido:** Minecraft
- **Repositorio:** [mnri1904/mnri-taller-git-2026](https://github.com/mnri1904/mnri-taller-git-2026)
- **Licencia:** Apache License 2.0

## Objetivo

El trabajo consistió en publicar una API REST propia con Spring Boot y aplicar
al dominio Minecraft el modelado desarrollado en las clases del 2 y 3 de
septiembre. La solución utiliza ocultamiento de información, herencia,
sobreescritura y polimorfismo. El proceso quedó registrado mediante commits,
ramas y pull requests.

## Bitácora de trabajo

### 1. Creación del repositorio

Se creó el repositorio público `mnri-taller-git-2026` con un archivo README y
la licencia Apache 2.0. Se configuró Git con el usuario `mnri1904` y se utilizó
SSH para las operaciones posteriores de `pull` y `push`.

El repositorio contiene un `.gitignore` para Java, Maven y los entornos de
desarrollo. Los directorios generados, como `target/`, no se versionan.

### 2. Proyecto Spring Boot

Se incorporó un proyecto generado con las siguientes características:

- Maven.
- Java 21.
- Spring Boot 3.5.16.
- Dependencia Spring Web.
- Maven Wrapper mediante `mvnw`.

La aplicación se ejecuta con:

```bash
./mvnw spring-boot:run
```

El servidor inicia en `http://localhost:8080`.

### 3. Incorporación del dominio Minecraft

El código del dominio se ubica en
`src/main/java/py/edu/uc/lp3/minecraft`. La jerarquía principal está formada
por:

- `Entidad`: clase abstracta que concentra vida, vida máxima y posición.
- `Mob`: clase abstracta que hereda de `Entidad` y declara `atacar()`.
- `Creeper`: implementa un ataque basado en la carga de una explosión.
- `Zombie`: implementa un ataque cuerpo a cuerpo.
- `EstadoEntidad`: representación inmutable del estado que puede exponerse
  como JSON.

Los atributos del dominio son privados. El controller no modifica directamente
la vida, la posición ni el estado interno; utiliza mensajes como `mover`,
`recibirDanio`, `atacar` y `explotar`. De esta manera no se puede dejar una
entidad en un estado inválido mediante asignaciones externas.

### 4. API REST

Se agregó `IndexController`, que responde a:

```text
GET /
```

También se agregó `MobController`, que recibe el tipo de mob y su posición por
la URL:

```text
GET /api/minecraft/mobs/creeper?x=3&z=2
GET /api/minecraft/mobs/zombie?x=1&z=-2
```

El controller construye un `Creeper` o un `Zombie`, pero conserva la referencia
con el tipo padre `Mob`. Luego invoca `atacar()` sin decidir en el controller
cómo debe comportarse cada clase concreta.

Ejemplo de respuesta del Creeper:

```json
{
  "entidad": {
    "tipo": "Creeper",
    "vida": 20,
    "vidaMaxima": 20,
    "posicionX": 3,
    "posicionZ": 2,
    "viva": true
  },
  "comportamiento": "El Creeper comenzó a cargar su explosión"
}
```

Ejemplo de respuesta del Zombie:

```json
{
  "entidad": {
    "tipo": "Zombie",
    "vida": 20,
    "vidaMaxima": 20,
    "posicionX": 1,
    "posicionZ": -2,
    "viva": true
  },
  "comportamiento": "El Zombie atacó cuerpo a cuerpo al jugador"
}
```

### 5. Herencia y polimorfismo

`Mob` declara el método abstracto `atacar()`. `Creeper` y `Zombie` son dos
especializaciones independientes que lo sobreescriben. El resultado enviado
como JSON depende del objeto concreto, aunque `MobController` utiliza el tipo
padre `Mob`.

El modelo queda representado por el siguiente diagrama:

```mermaid
classDiagram
    direction TB
    Entidad <|-- Mob
    Mob <|-- Creeper
    Mob <|-- Zombie

    class Entidad {
        <<abstract>>
        -int vidaMaxima
        -int vida
        -int posicionX
        -int posicionZ
        +tipo() String
        +mover(int x, int z)
        +recibirDanio(int cantidad)
        +estado() EstadoEntidad
    }

    class Mob {
        <<abstract>>
        +mover(int x, int z)
        +atacar() String
    }

    class Creeper {
        -boolean cargado
        +atacar() String
        +explotar() String
    }

    class Zombie {
        +atacar() String
    }
```

El diagrama corresponde a la versión del taller de septiembre.

### 6. Colaboración y revisión

Se realizaron contribuciones mediante ramas y pull requests:

- [PR recibido #1: Zombie como especialización de Mob](https://github.com/mnri1904/mnri-taller-git-2026/pull/1), creado por `NicoRodriguez85677` y fusionado en el repositorio personal.
- [PR enviado #1: Ghast con ataque encapsulado](https://github.com/NicoRodriguez85677/NRodriguez-taller-git-2026/pull/1), aprobado y fusionado en el repositorio del compañero.
- [PR enviado #3: Changelog con pedido de cambios](https://github.com/NicoRodriguez85677/NRodriguez-taller-git-2026/pull/3).

En el último PR, el reviewer solicitó agregar al Changelog una sección de
verificación con `./mvnw test`. El cambio se aplicó en la misma rama mediante
un segundo commit y se hizo `push`; luego el PR fue aprobado y fusionado. Esto
permitió practicar el ciclo completo de revisión sin reemplazar ni cerrar la
rama original.

## Verificación final

Se ejecutaron las pruebas automatizadas con:

```bash
./mvnw test
```

Resultado final:

```text
Tests run: 11, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

También se inició la aplicación y se verificaron por HTTP los endpoints `/`,
`/api/minecraft/mobs/creeper` y `/api/minecraft/mobs/zombie`. Los tres
respondieron correctamente con JSON.

El commit final publicado en `main` es `488b122`, y la rama local quedó limpia
y sincronizada con `origin/main`.

## Conclusión

La entrega publica un servicio HTTP funcional y versionado. El dominio mantiene
su estado encapsulado, comparte reglas mediante herencia y selecciona el
comportamiento concreto por polimorfismo. El historial y los pull requests
registran tanto el desarrollo individual como la colaboración y la revisión de
cambios.
