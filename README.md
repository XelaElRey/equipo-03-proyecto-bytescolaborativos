# 🎮 Plataforma de Torneos + 🧠 Motor de Recomendaciones  
### Proyecto backend desarrollado con Spring Boot – Equipo 03

Este repositorio contiene la implementación del backend para una plataforma de **gestión de torneos de videojuegos online**, que incorpora un **motor de recomendaciones de productos** para los usuarios.

El proyecto está dividido en **dos dominios principales**:

---

# 🏆 1. Dominio Principal – Plataforma de Torneos

Este dominio gestiona todo lo relacionado con la organización de torneos y la participación de jugadores.

## 📌 Entidades principales

### **Tournament**
Representa un torneo disponible en la plataforma.  
Incluye información como nombre, fecha, juego asociado, reglas, etc.

### **Participation**
Registra la participación de un usuario en un torneo.  
Cada usuario puede inscribirse en múltiples torneos.

## 🔗 Relaciones
- **Tournament 1:N Participation**  
  Un torneo puede tener múltiples participantes.  
  Un usuario puede participar en múltiples torneos.

---

# 🧠 2. Dominio Secundario – Motor de Recomendaciones

Este módulo forma parte del valor añadido de la plataforma, proporcionando recomendaciones de productos a los usuarios en función de sus valoraciones.

## 📌 Entidades asociadas

### **User**
Usuarios registrados en la plataforma. Además de participar en torneos, forman parte del sistema de recomendaciones.

### **Product**
Productos recomendables como periféricos, videojuegos, skins, etc.

### **Ratings**
Valoraciones que los usuarios asignan a productos.

### **Recommendation**
Representa una recomendación generada por el motor, asociada a múltiples productos y usuarios.

## 🔗 Relaciones

- **User 1:N Ratings**  
  Un usuario puede valorar muchos productos.

- **Product 1:N Ratings**  
  Un producto puede recibir valoraciones de muchos usuarios.

- **Product N:N Recommendations**  
  Las recomendaciones se generan a partir de valoraciones combinadas.  
  Para ello existe una tabla intermedia:

  - **products_recommendation**

---

# 🏗️ Arquitectura general

- **Java 17**
- **Spring Boot**
  - Spring Web  
  - Spring Data JPA  
  - Spring Security + JWT  
- **PostgreSQL**
- **Lombok**
- **Swagger / OpenAPI 3**
- **Maven**

La aplicación está organizada en capas:

- `controller`
- `service`
- `repository`
- `model`
- `dto`
- `configuration`
- `security`

---

# 🔐 Seguridad

El acceso está protegido mediante **JWT (JSON Web Tokens)**.  
El módulo de seguridad incluye:

- Registro y login de usuarios  
- Roles (PLAYER / ADMIN)  
- Protección de endpoints  
- Generación y validación de tokens JWT  

---

# 🧪 Endpoints principales (resumen)

### 👥 Auth & Users
- `POST /api/auth/register` – Registrar usuario  
- `POST /api/auth/login` – Autenticación  
- `GET /api/auth/users` – Obtener lista de usuarios  
- `DELETE /api/auth/users/{id}` – Eliminar usuario  

### 🏆 Torneos
- `GET /api/tournaments` – Listado de torneos  
- `POST /api/tournaments` – Crear torneo  
- `POST /api/tournaments/{id}/join` – Participar en un torneo  

### 🧠 Motor de Recomendaciones
- `GET /api/recommendations/{userId}` – Obtener recomendaciones personalizadas  
- `POST /api/ratings` – Registrar valoración  
- `GET /api/products` – Listado de productos  

---

# 🗄️ Base de datos – Esquema simplificado



## ▶️ Clonar repositorio

```bash
git clone https://github.com/XelaElRey/equipo-03-proyecto-bytescolaborativos.git
cd equipo-03-proyecto-bytescolaborativos
```
## ⚙️ Configurar variables de entorno
**Editar el archivo devcontainer.env**
```bash
# PostgreSQL settings 
POSTGRES_DB=
POSTGRES_USER=
POSTGRES_PASSWORD=

# Spring Boot DB variables
DB_NAME=
DB_USER=
DB_PASSWORD=

# JDBC URLs for each environment
DB_URL_DEV=
DB_URL_PROD=

# JWT security
JWT_SECRET_KEY=
ACCESS_TOKEN_EXPIRATION=
```

## 🚀 Ejecución del Proyecto

Para iniciar la aplicación en local:

```bash
mvn spring-boot:run
```


## 📘 Documentación de la API

La aplicación incluye documentación generada automáticamente con Swagger y OpenAPI.

👉 Swagger UI

Accede desde:
```bash
/swagger-ui.html
```
👉 OpenAPI

Documentación en formato JSON:
```bash
/api-docs
```

## 👥 Desarrolladores

Proyecto desarrollado por Equipo 03 Bytes Colaborativos 

- **Alex** – [@XelaElRey](https://github.com/XelaElRey)  
- **Lucas** – [@Lsterpino](https://github.com/Lsterpino)







