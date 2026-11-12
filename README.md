# Consodig Consultores - Authentication Service

Microservicio backend de autenticación y gestión de perfiles para la plataforma de consultoría de **Consodig Consultores**. Centraliza las cuentas de acceso y administra la información de administradores, evaluadores y evaluandos.

## Funcionalidades

- Inicio de sesión mediante correo y contraseña.
- Validación de cuentas habilitadas y contraseñas protegidas con BCrypt.
- Identificación de roles: `ADMIN`, `EVALUADOR` y `EVALUANDO`.
- CRUD y desactivación de administradores, evaluadores y evaluandos.
- Gestión de líneas de investigación y sus asociaciones con perfiles.
- Registro de información académica y referencias profesionales de evaluadores.
- Carga, actualización, consulta y eliminación de documentos de evaluadores mediante Cloudinary.
- Recuperación y restablecimiento de contraseñas mediante correo electrónico.

## Flujo de autenticación

1. El usuario envía su correo y contraseña.
2. El servicio localiza la cuenta y verifica que esté habilitada.
3. BCrypt compara la contraseña recibida con el hash almacenado.
4. Se consulta el perfil correspondiente al rol de la cuenta.
5. La respuesta incluye el identificador, correo, rol y datos básicos del usuario.

## Stack tecnológico

- Java 21
- Spring Boot 3.5.7
- Spring Web
- Spring Data JPA / Hibernate
- MySQL
- Spring Security Crypto / BCrypt
- Cloudinary
- SendGrid
- Thymeleaf
- Maven
- Docker

## Requisitos

- JDK 21
- MySQL
- Git
- Cuenta de Cloudinary
- Cuenta de SendGrid

El repositorio incluye Maven Wrapper.

## Configuración desde cero

```bash
git clone https://github.com/BSebastianGonzalez/cc_authentication.git
cd cc_authentication
```

Crea la base de datos:

```sql
CREATE DATABASE consodig;
```

Configura las variables de entorno:

```properties
DB_URL=jdbc:mysql://localhost:3306/consodig
DB_USER=tu_usuario_mysql
DB_PASSWORD=tu_contraseña_mysql
SENDGRID_API_KEY=tu_clave_de_sendgrid
CLOUDINARY_NAME=tu_cloud_name
CLOUDINARY_KEY=tu_api_key
CLOUDINARY_SECRET=tu_api_secret
```

No subas credenciales reales al repositorio.

En Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

En macOS o Linux:

```bash
./mvnw spring-boot:run
```

El servicio utiliza el puerto `80` por defecto según su configuración actual.

## API REST

Todos los endpoints utilizan el prefijo `/api`.

### Autenticación

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/api/auth/login` | Iniciar sesión |
| `POST` | `/api/auth/forgot-password` | Solicitar recuperación de contraseña |
| `POST` | `/api/auth/reset-password` | Restablecer contraseña con token |
| `GET` | `/api/auth/email/{id}` | Consultar correo por ID de cuenta |

### Administración y perfiles

| Recurso | Ruta base | Operaciones principales |
|---|---|---|
| Administradores | `/api/admin` | Crear, listar, consultar, actualizar y desactivar |
| Evaluadores | `/api/evaluadores` | Crear, listar, consultar, actualizar y desactivar |
| Evaluandos | `/api/evaluandos` | Crear, listar, consultar, actualizar y desactivar |
| Líneas de investigación | `/api/lineas-investigacion` | Crear, listar, consultar, actualizar y eliminar |

### Documentos de evaluadores

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/api/evaluadores/{evaluadorId}/archivos` | Crear documentos |
| `PUT` | `/api/evaluadores/{evaluadorId}/archivos` | Actualizar documentos |
| `GET` | `/api/evaluadores/{evaluadorId}/archivos` | Consultar metadatos |
| `GET` | `/api/evaluadores/{evaluadorId}/archivos/fotocopia` | Obtener fotocopia |
| `GET` | `/api/evaluadores/{evaluadorId}/archivos/certificados` | Obtener certificados |
| `GET` | `/api/evaluadores/{evaluadorId}/archivos/cuenta-bancaria` | Obtener certificado bancario |
| `DELETE` | `/api/evaluadores/{evaluadorId}/archivos` | Eliminar documentos |

## Arquitectura

```text
src/main/java/com/cc/be/
├── config/       # CORS, Cloudinary y BCrypt
├── controller/   # Endpoints REST
├── dto/          # Objetos de entrada y salida
├── model/        # Entidades JPA y enumeraciones
├── repository/   # Repositorios Spring Data
└── service/      # Casos de uso y reglas de negocio
```

La entidad `Account` centraliza la autenticación y se relaciona con los perfiles de administrador, evaluador y evaluando mediante relaciones uno a uno. Los documentos se persisten como referencias a recursos de Cloudinary.

## Pruebas y compilación

```powershell
.\mvnw.cmd test
.\mvnw.cmd clean package
```

El artefacto se genera en `target/cc-be-0.0.1-SNAPSHOT.jar`.

## Docker

```bash
./mvnw clean package -DskipTests
docker build -t consodig-authentication .
docker run --rm -p 80:80 --env-file .env consodig-authentication
```

## Integración

Este microservicio proporciona la identidad y los perfiles utilizados por el servicio de gestión de proyectos:

<https://github.com/BSebastianGonzalez/cc_project_management>

## Consideraciones

- Las contraseñas se almacenan utilizando BCrypt.
- Las cuentas pueden desactivarse sin eliminar el perfil asociado.
- Los documentos sensibles se almacenan en Cloudinary y sus referencias en MySQL.
- Para producción se recomiendan migraciones versionadas en lugar de `ddl-auto=update`.
- Las credenciales deben mantenerse como secretos de entorno.
