# Detall Sublim — Handover

Documento de entrega y continuidad operativa del proyecto Detall Sublim.

El objetivo de este documento es identificar los servicios, accesos y responsabilidades necesarios para que la aplicación pueda mantenerse operativa independientemente de la persona que haya realizado su desarrollo inicial.

No se incluyen contraseñas, tokens, claves privadas ni API keys.

---

## 1. Aplicación entregada

URL oficial:

```text
https://www.detallsublim.es
```

Estado:

```text
Producción operativa
```

El alcance principal está completado:

```text
web corporativa
catálogo
categorías
solicitudes de presupuesto
mensajes de contacto
panel administrativo
gestión de productos
gestión de usuarios
roles y permisos
presupuestos PDF
correo transaccional
base de datos persistente
almacenamiento persistente
CI/CD
monitorización
backups
seguridad de producción
```

---

## 2. Servicios que deben conservarse

La continuidad de Detall Sublim depende de los siguientes servicios:

| Servicio     | Función                              |
| ------------ | ------------------------------------ |
| IONOS        | Dominios y DNS                       |
| Railway      | Hosting de aplicación y MySQL        |
| GitHub       | Código fuente, Pull Requests y CI/CD |
| Resend       | Correo transaccional                 |
| UptimeRobot  | Monitorización                       |
| Google Drive | Copia externa de backups             |

La pérdida de acceso a cualquiera de estos servicios puede dificultar el mantenimiento de la plataforma.

---

## 3. Dominios

Dominios asociados:

```text
detallsublim.es
detallsublim.com
```

Dominio canónico:

```text
https://www.detallsublim.es
```

`detallsublim.com` se utiliza como dominio de protección y redirección hacia `.es`.

El propietario debe mantener control sobre:

```text
renovaciones
facturación
DNS
SSL
datos de contacto
```

---

## 4. Railway

Railway aloja:

```text
aplicación
base de datos MySQL
almacenamiento persistente
variables de entorno
deployments
logs
métricas
```

La cuenta responsable de producción debe mantenerse activa y con un método de pago válido si el plan utilizado lo requiere.

El propietario debe conservar control administrativo o de facturación sobre el proyecto de producción.

El desarrollador puede mantener acceso técnico si existe autorización.

---

## 5. GitHub

Repositorio:

```text
RaulBaron373/detallSublim
```

Rama principal:

```text
master
```

Los cambios deben seguir el flujo:

```text
branch
→ Pull Request
→ CI
→ CodeQL
→ Dependency Review
→ merge
→ Railway
```

No deben almacenarse secretos dentro del repositorio.

---

## 6. Resend

Resend gestiona el correo transaccional de la aplicación.

Dominio de envío:

```text
mail.detallsublim.es
```

Se utiliza para:

```text
notificaciones
presupuestos
creación de acceso
restablecimiento de contraseñas
comunicaciones automáticas
```

La API key debe existir únicamente en el entorno seguro de producción.

Si se regenera una API key, debe actualizarse también la variable correspondiente de Railway.

---

## 7. UptimeRobot

La monitorización externa incluye:

```text
Web pública
https://www.detallsublim.es/

Backend / health
https://www.detallsublim.es/management/health
```

Las alertas deben dirigirse a una cuenta supervisada por el propietario.

---

## 8. Backups

Las copias externas se conservan en:

```text
Google Drive
└── Detall Sublim
    └── Backups
        ├── MySQL
        └── Historias
```

Debe mantenerse la política definida en:

```text
docs/OPERATIONS.md
```

Los backups no deben guardarse únicamente en el mismo proveedor que aloja producción.

---

## 9. Cuenta administrativa del propietario

Existe una cuenta administrativa definitiva del propietario.

Esta cuenta debe disponer de:

```text
ROLE_ADMIN
ROLE_USER
```

El propietario debe mantener su contraseña privada.

La contraseña no debe compartirse con desarrolladores, proveedores o documentación.

Si se necesita acceso técnico adicional, debe crearse una cuenta independiente.

---

## 10. Cuenta técnica del desarrollador

Una cuenta técnica puede conservarse mientras exista una necesidad de soporte o mantenimiento autorizada.

Se recomienda:

```text
cuenta independiente
credenciales independientes
sin compartir contraseña
retirada cuando deje de ser necesaria
```

No debe utilizarse la cuenta del propietario para tareas ordinarias de desarrollo.

---

## 11. Variables y secretos

Los secretos de producción incluyen, entre otros:

```text
contraseña de MySQL
JWT secret
Resend API key
credenciales administrativas
claves SSH
```

Nunca deben almacenarse en:

```text
Git
README
issues
Pull Requests
capturas públicas
documentos compartidos sin protección
```

Las variables deben mantenerse dentro de Railway o del servicio correspondiente.

---

## 12. Claves SSH

Las claves privadas utilizadas para operaciones técnicas deben permanecer únicamente en equipos autorizados.

Nunca deben copiarse al repositorio.

Nunca debe compartirse:

```text
clave privada
passphrase
contenido de archivos id_ed25519
```

Las claves pueden revocarse y reemplazarse cuando sea necesario.

---

## 13. Antes de una modificación de producción

Antes de cualquier modificación relevante:

```text
1. Confirmar el objetivo del cambio.
2. Crear backup si afecta a datos o infraestructura.
3. Crear una rama independiente.
4. Implementar y probar localmente.
5. Crear Pull Request.
6. Esperar CI y análisis de seguridad.
7. Realizar merge.
8. Esperar deployment.
9. Verificar health.
10. Ejecutar smoke test.
```

---

## 14. Cambios especialmente sensibles

Requieren especial cuidado:

```text
migraciones de base de datos
cambios de dominio
cambios DNS
cambios de variables de entorno
cambios JWT
cambio de API keys
eliminación de usuarios ADMIN
cambios en almacenamiento persistente
restauración de backups
modificación de volúmenes
```

Nunca deben realizarse simultáneamente varios cambios sensibles sin necesidad.

---

## 15. Recuperación de acceso

Si se pierde acceso a una cuenta técnica:

```text
no reutilizar ni compartir credenciales de otra persona
crear o recuperar una cuenta autorizada
revocar accesos antiguos cuando sea necesario
```

Si se pierde acceso al propietario de un servicio, debe utilizarse el procedimiento oficial de recuperación del proveedor correspondiente.

---

## 16. Transferencia futura

Si el proyecto cambia de desarrollador o proveedor, deben transferirse o revisarse como mínimo:

```text
GitHub
Railway
IONOS
Resend
UptimeRobot
Google Drive
cuentas ADMIN
documentación operativa
backups
variables de entorno
DNS
```

Los secretos deben transferirse mediante mecanismos seguros, nunca mediante el repositorio.

---

## 17. Documentación técnica

Documentación principal:

```text
README.md
docs/OPERATIONS.md
docs/HANDOVER.md
```

El README describe el proyecto y su arquitectura.

`OPERATIONS.md` contiene los procedimientos de mantenimiento.

`HANDOVER.md` contiene los requisitos para la continuidad y transferencia del servicio.

---

## 18. Estado de entrega

En el momento de la entrega:

```text
web pública operativa
dominio definitivo operativo
HTTPS operativo
backend operativo
MySQL operativo
panel administrativo operativo
cuenta ADMIN del propietario validada
correo transaccional operativo
presupuestos PDF operativos
monitorización operativa
backups comprobados
restauración de MySQL probada
seguridad final revisada
CI operativo
CodeQL operativo
Dependency Review operativo
```

---

## 19. Responsabilidad operativa

El propietario es responsable de mantener activos:

```text
dominios
cuentas de proveedores
métodos de pago
direcciones de contacto
accesos administrativos
copias externas
```

El responsable técnico es responsable, cuando exista contrato o autorización de mantenimiento, de:

```text
código
deployments
diagnóstico técnico
actualizaciones
seguridad técnica
backups operativos
restauraciones
resolución de incidencias
```

Las responsabilidades concretas pueden variar según el acuerdo de mantenimiento vigente.

---

## 20. Cierre

Detall Sublim se entrega como una aplicación Full Stack funcional y desplegada en producción.

A partir de la primera versión estable, cualquier cambio posterior deberá tratarse como:

```text
mantenimiento
corrección
mejora
nueva funcionalidad
o nueva versión
```

y debe conservar la trazabilidad mediante Git y Pull Requests.
