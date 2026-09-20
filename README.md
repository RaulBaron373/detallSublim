# Detall Sublim

Aplicación web Full Stack desarrollada para **Detall Sublim**, empresa especializada en productos personalizados.

La plataforma combina una **web corporativa pública**, un **catálogo de productos**, un sistema de **solicitudes de presupuesto** y un **panel privado de administración** para centralizar la gestión interna del negocio.

> Proyecto desarrollado como parte de mi formación en Desarrollo de Aplicaciones Web (DAW) y evolucionado hasta una aplicación desplegada y operativa en producción.

---

## Producción

La aplicación se encuentra actualmente desplegada y operativa en producción.

**Sitio web oficial:**

[https://www.detallsublim.es](https://www.detallsublim.es)

El dominio `detallsublim.com` también está reservado y redirige al dominio oficial `.es`.

### Estado actual

- Aplicación desplegada en producción.
- Dominio personalizado y HTTPS.
- Base de datos MySQL persistente.
- Envío de correo transaccional mediante Resend.
- Generación de presupuestos en PDF.
- Monitorización externa de disponibilidad.
- Backups operativos de base de datos y almacenamiento persistente.
- CI, análisis de seguridad y revisión de dependencias mediante GitHub Actions.
- Sistema de autenticación y autorización basado en JWT.
- Cuenta administrativa definitiva del propietario configurada.

---

## Vista general

![Página principal de Detall Sublim](docs/screenshots/home.jpg)

Detall Sublim nace como una solución para digitalizar la presencia de la empresa, presentar su catálogo de productos y centralizar las solicitudes realizadas por los clientes.

La aplicación está dividida en dos áreas principales:

- **Área pública**, orientada a clientes y visitantes.
- **Panel de administración**, destinado a la gestión interna del negocio.

No se trata de un e-commerce: los productos se presentan mediante catálogo y el cliente solicita un presupuesto personalizado antes de realizar cualquier contratación.

---

## Funcionalidades

### Área pública

La web pública permite:

- consultar información corporativa;
- conocer los servicios ofrecidos;
- visualizar el catálogo de productos;
- filtrar productos por categoría;
- consultar precios orientativos;
- conocer las tecnologías de personalización disponibles;
- enviar consultas mediante el formulario de contacto;
- solicitar presupuestos personalizados;
- navegar desde dispositivos móviles, tablets y escritorio mediante una interfaz responsive.

### Panel de administración

El panel privado permite:

- autenticación segura de usuarios;
- gestión de productos;
- gestión de categorías;
- gestión de solicitudes de presupuesto;
- gestión de mensajes de contacto;
- gestión de usuarios;
- gestión de roles y permisos;
- actualización del estado de las solicitudes;
- definición del precio final de un presupuesto;
- definición de tiempos estimados;
- generación de presupuestos en PDF;
- envío de respuestas y notificaciones por correo electrónico;
- restablecimiento administrativo de contraseñas.

---

## Catálogo de productos

Los clientes pueden consultar los productos disponibles y filtrarlos por categoría.

![Catálogo de productos](docs/screenshots/catalogo.jpg)

Cada producto puede contener información como:

- nombre;
- descripción;
- precio orientativo;
- categoría;
- imagen;
- disponibilidad o estado.

El catálogo tiene carácter informativo. La contratación final se gestiona mediante una solicitud de presupuesto.

---

## Solicitudes de presupuesto

Los clientes pueden seleccionar un producto y enviar una solicitud indicando la información necesaria para preparar una propuesta personalizada.

![Formulario de solicitud de presupuesto](docs/screenshots/presupuesto.jpg)

Las solicitudes quedan almacenadas en la base de datos y pasan a estar disponibles inmediatamente en el panel de administración.

Desde el panel es posible revisar la petición, preparar la propuesta económica y responder al cliente.

---

## Presupuestos en PDF

El sistema permite generar documentos PDF asociados a las solicitudes de presupuesto.

Cuando una solicitud es procesada, el administrador puede:

- definir la información económica;
- preparar la respuesta;
- generar el documento PDF;
- enviarlo al cliente mediante correo electrónico.

Esto permite gestionar el proceso comercial desde una única plataforma.

---

## Formulario de contacto

La web incorpora un formulario público mediante el cual los visitantes pueden contactar con Detall Sublim.

Los mensajes:

1. se almacenan en la base de datos;
2. aparecen en el panel administrativo;
3. generan una notificación mediante el sistema de correo transaccional.

Los formularios públicos incorporan limitación de solicitudes para reducir abuso automatizado.

---

## Panel de administración

![Panel de administración](docs/screenshots/panel-admin.jpg)

El panel centraliza las principales operaciones internas de la aplicación.

Desde él se puede acceder a:

- Productos
- Categorías
- Solicitudes
- Mensajes
- Gestión de usuarios

El acceso requiere autenticación y los permisos dependen del rol asignado al usuario.

---

## Gestión de solicitudes

![Gestión de solicitudes de presupuesto](docs/screenshots/panel-solicitudes.jpg)

Las solicitudes pueden consultarse y administrarse desde el panel interno.

El administrador puede revisar los datos proporcionados por el cliente, actualizar el estado de la solicitud, preparar el presupuesto y enviar la respuesta correspondiente.

---

## Gestión de productos

![Gestión de productos](docs/screenshots/crud-productos.jpg)

El panel permite mantener actualizado el catálogo público.

Entre los datos gestionables se encuentran:

- nombre;
- descripción;
- precio orientativo;
- categoría;
- imagen;
- estado.

---

## Gestión de usuarios

Los usuarios internos se administran exclusivamente desde el panel.

El sistema permite:

- crear usuarios;
- activar o desactivar cuentas;
- asignar autoridades;
- administrar permisos;
- iniciar un restablecimiento de contraseña;
- eliminar usuarios.

No existe registro público de usuarios.

Las operaciones administrativas requieren los permisos correspondientes.

---

## Correo transaccional

El envío de correo en producción se realiza mediante **Resend** utilizando su API HTTPS.

Se utiliza un subdominio específico para correo transaccional:

```text
mail.detallsublim.es
```

El dominio dispone de los registros DNS necesarios para autenticar los envíos.

Los correos transaccionales se utilizan, entre otros casos, para:

- notificaciones de formularios de contacto;
- comunicaciones relacionadas con solicitudes;
- envío de presupuestos;
- creación de acceso para usuarios;
- restablecimiento de contraseñas.

Las credenciales y API keys no se almacenan en el repositorio.

---

## Stack tecnológico

### Frontend

- Angular 21
- TypeScript 5
- HTML5
- SCSS
- Bootstrap 5
- ng-bootstrap
- Angular standalone components
- Lazy loading

### Backend

- Java 21
- Spring Boot 3.4
- Spring Security
- Spring Data JPA
- REST APIs
- JWT
- Maven

### Persistencia

- MySQL
- Hibernate
- Liquibase

### Servicios e infraestructura

- Railway
- Resend
- IONOS
- UptimeRobot
- GitHub Actions
- Docker

### Desarrollo

- JHipster 8.11.0
- Visual Studio Code
- Git
- GitHub
- Figma

---

## Arquitectura

La aplicación utiliza una arquitectura Full Stack en la que Angular y Spring Boot se distribuyen conjuntamente en producción.

```text
                         Internet
                            │
                            ▼
                  https://www.detallsublim.es
                            │
                            ▼
                     Railway / HTTPS
                            │
                            ▼
                ┌──────────────────────┐
                │     Spring Boot      │
                │                      │
                │  Angular production  │
                │  REST API            │
                │  Spring Security     │
                │  Business services   │
                └──────────┬───────────┘
                           │
                 ┌─────────┴─────────┐
                 │                   │
                 ▼                   ▼
              MySQL                Resend
           persistente          Email API HTTPS
                 │
                 ▼
        Persistencia de datos
```

El backend también dispone de almacenamiento persistente para los archivos que requieren conservarse entre despliegues.

---

## Despliegue

La aplicación está desplegada en **Railway**.

La infraestructura principal está formada por:

```text
Railway Project
│
├── Aplicación Detall Sublim
│   ├── Angular
│   ├── Spring Boot
│   ├── API REST
│   └── almacenamiento persistente
│
└── MySQL
    └── almacenamiento persistente
```

El despliegue de producción se realiza desde la rama:

```text
master
```

Railway está conectado al repositorio de GitHub y el despliegue espera la validación del flujo de integración continua antes de actualizar producción.

---

## CI/CD y calidad

El repositorio utiliza GitHub Actions para validar los cambios antes de integrarlos en la rama principal.

Actualmente se ejecutan controles como:

### CI

Valida la construcción y las pruebas del proyecto.

### CodeQL

Realiza análisis estático orientado a detectar posibles problemas de seguridad.

### Dependency Review

Analiza cambios en las dependencias antes de integrar Pull Requests.

El flujo de trabajo habitual es:

```text
Feature / Fix branch
        │
        ▼
   Pull Request
        │
        ├── CI
        ├── CodeQL
        └── Dependency Review
        │
        ▼
      master
        │
        ▼
     Railway
        │
        ▼
    Producción
```

---

## Seguridad

La aplicación utiliza **Spring Security** y autenticación mediante **JWT**.

Entre las medidas implementadas se encuentran:

- sesiones de backend stateless;
- autenticación mediante Bearer JWT;
- autorización basada en roles;
- rutas administrativas protegidas;
- registro público de usuarios deshabilitado;
- activación pública de cuentas deshabilitada;
- inicio público de restablecimiento de contraseña deshabilitado;
- restablecimiento iniciado desde administración;
- limitación de peticiones en formularios públicos;
- Content Security Policy;
- HSTS;
- protección frente a carga en frames no autorizados;
- Referrer Policy;
- Permissions Policy;
- endpoints administrativos de Actuator protegidos;
- Swagger/OpenAPI deshabilitado en producción;
- secretos configurados exclusivamente mediante variables de entorno.

Los endpoints no reconocidos dentro de `/api/**` se bloquean por defecto.

---

## Health check

Producción dispone de un endpoint público limitado exclusivamente al estado general del servicio:

```text
/management/health
```

No expone detalles internos de configuración.

Los demás endpoints administrativos de Actuator permanecen protegidos o no expuestos en producción.

---

## Monitorización

La disponibilidad de producción se supervisa externamente mediante **UptimeRobot**.

Se monitorizan:

```text
https://www.detallsublim.es/
https://www.detallsublim.es/management/health
```

Esto permite detectar interrupciones tanto de la web pública como de la aplicación backend.

---

## Backups

Se mantiene una estrategia de copias de seguridad independiente de la infraestructura de producción.

Se realizan backups de:

### Base de datos MySQL

Se utilizan copias lógicas mediante `mysqldump`.

### Almacenamiento persistente

Los archivos almacenados en el volumen persistente también disponen de copia independiente.

La política operativa contempla:

- backup semanal;
- backup adicional antes de cambios relevantes;
- conservación de varias copias recientes;
- copia externa;
- pruebas periódicas de restauración.

Los archivos de backup nunca deben almacenarse dentro del repositorio Git.

La documentación operativa detallada se encuentra en:

```text
docs/OPERATIONS.md
```

---

## Variables de entorno

La configuración sensible de producción se proporciona mediante variables de entorno.

Entre las variables principales se encuentran:

```text
SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD

JHIPSTER_SECURITY_AUTHENTICATION_JWT_BASE64_SECRET

RESEND_API_KEY
RESEND_FROM_EMAIL
DETALL_SUBLIM_NOTIFICATION_EMAIL

APP_BASE_URL

APPLICATION_STORAGE_HISTORIAS_LOCATION

DETALL_SUBLIM_INITIAL_ADMIN_LOGIN
```

El repositorio contiene `.env.example` únicamente como referencia de nombres y estructura.

**Nunca deben añadirse credenciales reales, contraseñas, API keys o secretos al repositorio.**

---

## Ejecución en local

### Requisitos

Para trabajar con el proyecto se recomienda disponer de:

- Java 21
- Node.js
- npm
- MySQL
- Git
- Docker Desktop para determinadas pruebas de integración

El proyecto incluye Maven Wrapper.

---

### Base de datos

Por defecto, el entorno de desarrollo utiliza una base de datos MySQL local:

```text
detallsublim
```

El servicio MySQL debe estar disponible antes de iniciar el backend.

---

### Backend

En Windows PowerShell:

```powershell
.\mvnw.cmd
```

En macOS o Linux:

```bash
./mvnw
```

El backend estará disponible normalmente en:

```text
http://localhost:8080
```

---

### Frontend

En otra terminal:

```bash
npm start
```

El frontend de desarrollo estará disponible normalmente en:

```text
http://localhost:4200
```

---

## Pruebas

El proyecto dispone de pruebas unitarias y de integración.

Algunas pruebas de integración utilizan **Testcontainers**, por lo que requieren Docker operativo.

Para ejecutar la validación Maven:

```powershell
.\mvnw.cmd verify
```

En macOS o Linux:

```bash
./mvnw verify
```

---

## Estructura general

```text
detallSublim/
│
├── src/
│   ├── main/
│   │   ├── java/          Backend Spring Boot
│   │   ├── resources/     Configuración, Liquibase y templates
│   │   └── webapp/        Frontend Angular
│   │
│   └── test/              Tests
│
├── docs/
│   └── screenshots/
│
├── Dockerfile
├── pom.xml
├── package.json
├── angular.json
├── .env.example
└── README.md
```

---

## Entornos

El proyecto contempla dos entornos principales de infraestructura:

### Production

Entorno utilizado por la aplicación pública y los usuarios finales.

### Staging

Entorno reservado para validaciones técnicas previas cuando sea necesario.

Las pruebas ordinarias de desarrollo deben realizarse localmente o en una rama independiente antes de afectar a producción.

---

## Operación y mantenimiento

Las tareas principales de mantenimiento incluyen:

- comprobar alertas de disponibilidad;
- revisar periódicamente consumo de CPU y memoria;
- controlar crecimiento del volumen de MySQL;
- revisar logs después de despliegues;
- realizar backups periódicos;
- realizar un backup adicional antes de cambios estructurales;
- comprobar `/management/health` después de despliegues;
- revisar los resultados de GitHub Actions;
- mantener dependencias actualizadas de forma controlada.

La documentación de operación se mantiene en:

```text
docs/OPERATIONS.md
```

La documentación de entrega y continuidad se mantiene en:

```text
docs/HANDOVER.md
```

---

## Flujo de desarrollo

El desarrollo utiliza ramas específicas para cada cambio.

Ejemplo:

```text
master
  │
  ├── feature/...
  ├── fix/...
  └── docs/...
```

Los cambios deben integrarse mediante Pull Request después de superar las validaciones correspondientes.

No se recomienda desarrollar directamente sobre `master`.

---

## Estado del proyecto

**Aplicación funcional y operativa en producción.**

El alcance principal previsto para Detall Sublim está completado:

- web corporativa;
- catálogo;
- solicitudes de presupuesto;
- formulario de contacto;
- panel administrativo;
- productos;
- categorías;
- solicitudes;
- mensajes;
- usuarios;
- autenticación y autorización;
- presupuestos PDF;
- correo transaccional;
- despliegue productivo;
- dominio personalizado;
- persistencia;
- backups;
- monitorización;
- CI;
- análisis de seguridad.

Las futuras modificaciones se consideran evolución o mantenimiento del producto y no funcionalidades pendientes de la primera versión estable.

---

## Posibles evoluciones futuras

Entre las mejoras que podrían desarrollarse en futuras versiones se encuentran:

- automatización adicional de backups;
- ampliación de métricas y observabilidad;
- nuevas funcionalidades comerciales;
- ampliación del sistema de contenidos;
- mejoras adicionales de accesibilidad;
- optimización continua de rendimiento;
- ampliación de cobertura automatizada;
- evolución de la infraestructura según el crecimiento del tráfico.

Estas mejoras no forman parte de los requisitos necesarios para el funcionamiento actual de la plataforma.

---

## Documentación

La documentación técnica y operativa del proyecto se encuentra dentro del directorio:

```text
docs/
```

Documentos principales:

```text
docs/OPERATIONS.md
docs/HANDOVER.md
```

---

## Sobre el proyecto

Detall Sublim ha sido desarrollado de forma individual como proyecto Full Stack, cubriendo:

- análisis;
- diseño;
- frontend;
- backend;
- modelado de datos;
- API REST;
- seguridad;
- persistencia;
- correo transaccional;
- generación documental;
- testing;
- CI/CD;
- despliegue;
- monitorización;
- operación de producción.

El proyecto fue generado inicialmente con **JHipster 8.11.0** y posteriormente personalizado y ampliado para implementar los requisitos específicos de Detall Sublim.

---

## Autor

**Raúl Barón Gómez**

Full Stack Developer

Tecnologías principales:

- Angular
- TypeScript
- Java
- Spring Boot
- MySQL

[LinkedIn](https://www.linkedin.com/in/raulbarongomez/) · [GitHub](https://github.com/RaulBaron373)
