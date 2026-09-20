# Detall Sublim — Operations Runbook

Documento operativo para el mantenimiento de la aplicación Detall Sublim en producción.

Este documento no contiene contraseñas, tokens, API keys ni secretos. Las credenciales deben mantenerse exclusivamente en los servicios correspondientes o en un gestor seguro autorizado.

---

## 1. Producción

Aplicación pública:

```text
https://www.detallsublim.es
```

Health check:

```text
https://www.detallsublim.es/management/health
```

Resultado esperado:

```json
{
  "status": "UP"
}
```

El dominio principal de producción es:

```text
www.detallsublim.es
```

El dominio:

```text
detallsublim.com
```

se mantiene reservado y redirige al dominio oficial `.es`.

---

## 2. Arquitectura de producción

La solución está formada por:

```text
Internet
   │
   ▼
IONOS DNS / dominio
   │
   ▼
Railway
   │
   ├── Aplicación Spring Boot
   │     ├── Angular
   │     ├── REST API
   │     ├── Spring Security / JWT
   │     └── almacenamiento persistente
   │
   └── MySQL
         └── volumen persistente
```

Servicios externos:

```text
Resend       → correo transaccional
UptimeRobot  → monitorización externa
GitHub       → repositorio y CI/CD
Google Drive → copia externa de backups
```

---

## 3. Despliegue

La rama de producción es:

```text
master
```

Railway está conectado al repositorio GitHub.

Flujo habitual:

```text
rama de trabajo
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
 producción
```

No se recomienda realizar cambios directamente sobre `master`.

---

## 4. Comprobaciones después de un despliegue

Después de cualquier despliegue de producción deben revisarse como mínimo:

```text
1. Deployment finalizado correctamente en Railway.
2. https://www.detallsublim.es carga correctamente.
3. /management/health devuelve status UP.
4. No aparecen errores nuevos relevantes en los logs.
5. UptimeRobot continúa mostrando ambos monitores como UP.
```

Comprobación rápida mediante PowerShell:

```powershell
(Invoke-WebRequest "https://www.detallsublim.es/" -UseBasicParsing).StatusCode
```

Resultado esperado:

```text
200
```

Health:

```powershell
Invoke-RestMethod "https://www.detallsublim.es/management/health" |
    ConvertTo-Json -Depth 5
```

Resultado esperado:

```text
status = UP
```

---

## 5. Monitorización

UptimeRobot supervisa:

```text
https://www.detallsublim.es/
```

y:

```text
https://www.detallsublim.es/management/health
```

Los monitores están configurados con comprobaciones periódicas y alertas por correo.

Una alerta aislada debe confirmarse antes de considerar que existe una incidencia real.

Ante una alerta:

```text
1. Comprobar la web manualmente.
2. Comprobar /management/health.
3. Revisar el último deployment de Railway.
4. Revisar logs de la aplicación.
5. Revisar MySQL si la aplicación no puede acceder a datos.
```

---

## 6. Métricas

Se recomienda revisar mensualmente:

```text
CPU
RAM
almacenamiento de la aplicación
almacenamiento de MySQL
errores HTTP 5xx
latencia
```

Referencias operativas recomendadas:

```text
RAM sostenida por encima de ~85 % → revisar
MySQL por encima de ~70–80 % del volumen → ampliar o limpiar antes de alcanzar el límite
errores 5xx persistentes → investigar
incidencias reales de UptimeRobot → investigar inmediatamente
```

Un pico aislado no implica necesariamente una incidencia.

---

## 7. Logs

Logs de aplicación mediante Railway CLI:

```powershell
railway logs `
  --service detallSublim `
  --environment production `
  --lines 200
```

Logs de MySQL:

```powershell
railway logs `
  --service MySQLProd `
  --environment production `
  --lines 200
```

Los mensajes `WARNING` deben analizarse según su contexto.

Un `WARNING` no debe tratarse automáticamente como un `ERROR`.

---

## 8. Backups

Railway no dispone actualmente de backups nativos habilitados para esta configuración.

Por ello se utiliza una estrategia de copias externas.

Se respaldan independientemente:

```text
MySQL
almacenamiento persistente de historias
```

---

## 9. Política de backups

Política mínima:

```text
1 backup semanal
1 backup adicional antes de cambios relevantes
mínimo 4 backups semanales recientes
1 backup mensual durante al menos 3 meses
copia local
copia externa
prueba periódica de restauración
```

Las copias externas se almacenan en Google Drive:

```text
Detall Sublim/
└── Backups/
    ├── MySQL/
    └── Historias/
```

Los backups nunca deben añadirse al repositorio Git.

---

## 10. Backup de MySQL

El backup de producción se realiza mediante `mysqldump`.

La ejecución debe realizarse desde el entorno de Railway o mediante conexión autorizada para evitar exponer credenciales.

Ejemplo de comando remoto:

```bash
MYSQL_PWD="$MYSQLPASSWORD" mysqldump \
  -h "$MYSQLHOST" \
  -P "$MYSQLPORT" \
  -u "$MYSQLUSER" \
  --single-transaction \
  --quick \
  --routines \
  --triggers \
  --events \
  --hex-blob \
  --no-tablespaces \
  --set-gtid-purged=OFF \
  "$MYSQLDATABASE"
```

El resultado debe comprimirse:

```text
detallsublim-prod-YYYY-MM-DD_HHMMSS.sql.gz
```

Después de generar una copia deben verificarse:

```text
archivo existente
tamaño mayor que cero
hash SHA-256
estructura SQL válida
```

---

## 11. Restauración de MySQL

Las restauraciones deben probarse primero en una base de datos temporal.

Nunca probar una restauración directamente sobre producción.

Procedimiento recomendado:

```text
1. Descomprimir el backup.
2. Crear una instancia MySQL temporal.
3. Crear una base de datos vacía.
4. Importar el SQL.
5. Confirmar que las tablas existen.
6. Revisar que no se produzcan errores.
7. Destruir el entorno temporal.
```

Una restauración en producción solo debe realizarse ante una incidencia real y después de generar un backup adicional del estado existente si es posible.

---

## 12. Backup del almacenamiento persistente

Los archivos persistentes de historias se encuentran en producción bajo:

```text
/data/historias
```

El backup puede generarse mediante:

```bash
tar -C /data -czf historias-backup.tar.gz historias
```

Formato recomendado:

```text
detallsublim-historias-prod-YYYY-MM-DD_HHMMSS.tar.gz
```

Después debe validarse:

```text
integridad del archivo
estructura interna
número de archivos
número de directorios
hash SHA-256
```

---

## 13. Restauración del almacenamiento

Antes de restaurar producción:

```text
1. Extraer el archivo en una carpeta temporal.
2. Confirmar que existe la carpeta historias.
3. Verificar la estructura.
4. Confirmar los archivos esperados.
5. Solo entonces realizar una restauración sobre producción.
```

No sobrescribir `/data/historias` sin disponer previamente de un backup del estado actual.

---

## 14. Correo transaccional

El correo de aplicación utiliza Resend.

Subdominio de envío:

```text
mail.detallsublim.es
```

Variables principales:

```text
RESEND_API_KEY
RESEND_FROM_EMAIL
DETALL_SUBLIM_NOTIFICATION_EMAIL
APP_BASE_URL
```

El DNS incluye registros de autenticación gestionados para el dominio de envío.

Si dejan de enviarse correos:

```text
1. Comprobar estado del dominio en Resend.
2. Revisar actividad/envíos en Resend.
3. Confirmar que RESEND_API_KEY existe en Railway.
4. Confirmar que RESEND_FROM_EMAIL es válido.
5. Revisar logs de Spring Boot.
6. Realizar un envío funcional controlado.
```

Nunca copiar la API key en issues, commits, documentación o mensajes públicos.

---

## 15. Variables de entorno

Variables relevantes de producción:

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

Los valores reales no deben almacenarse en Git.

`.env.example` sirve únicamente como referencia.

---

## 16. Seguridad

Producción utiliza:

```text
Spring Security
JWT
autorización por roles
CSP
HSTS
Referrer Policy
Permissions Policy
rate limiting
restricción de endpoints administrativos
```

En producción:

```text
registro público         → deshabilitado
activación pública       → deshabilitada
password reset init      → deshabilitado públicamente
Swagger/OpenAPI          → deshabilitado/protegido
Actuator                 → únicamente health expuesto públicamente
```

El endpoint público:

```text
/management/health
```

solo debe mostrar información general de disponibilidad.

---

## 17. Usuarios administradores

Debe existir siempre al menos una cuenta administrativa válida y controlada por el propietario.

Antes de eliminar, desactivar o retirar permisos a una cuenta administrativa:

```text
1. Confirmar que existe otro ADMIN operativo.
2. Iniciar sesión con esa cuenta.
3. Verificar acceso al panel.
4. Confirmar Gestión de usuarios.
5. Solo entonces modificar la cuenta anterior.
```

Especial atención a:

```text
DETALL_SUBLIM_INITIAL_ADMIN_LOGIN
```

El usuario indicado por esta variable debe seguir siendo compatible con el proceso de inicialización de producción.

No debe eliminarse o perder `ROLE_ADMIN` sin revisar primero la configuración de Railway.

---

## 18. Gestión de incidencias

Ante una incidencia:

```text
1. Identificar si afecta a web, backend, base de datos o correo.
2. Revisar UptimeRobot.
3. Revisar Railway.
4. Comprobar health.
5. Revisar logs.
6. Identificar el último cambio desplegado.
7. Evitar cambios simultáneos no relacionados.
8. Generar backup antes de una intervención destructiva.
9. Aplicar la corrección mínima necesaria.
10. Validar producción.
```

Si la incidencia coincide con un nuevo deployment, debe considerarse una reversión controlada al último commit estable.

---

## 19. Mantenimiento periódico

Mensualmente:

```text
revisar UptimeRobot
revisar métricas de Railway
revisar espacio de MySQL
revisar logs relevantes
revisar estado de Resend
revisar dependencias
revisar GitHub Actions
confirmar backups recientes
```

Trimestralmente:

```text
realizar una prueba de restauración de backup
revisar accesos administrativos
revisar servicios y cuentas externas
```

---

## 20. Principio operativo

Producción debe mantenerse mediante cambios pequeños, trazables y reversibles.

Antes de cualquier cambio relevante:

```text
backup
rama independiente
Pull Request
CI
revisión
merge
deploy
health check
smoke test
```
