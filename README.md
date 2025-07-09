# 🩺 Sistema Distribuido de Monitoreo Médico - ENCUENTRO

Este sistema distribuido permite el monitoreo de signos vitales en tiempo real, generación de alertas médicas críticas, reportes diarios y notificaciones automáticas.

## 📁 Estructura del Proyecto

```text
.
├── ms-api-gateway
├── ms-eureka
├── ms-patient-data-collector
├── ms-health-analyzer
├── ms-care-notifier
```

## ⚙️ Requisitos

- Java 21
- Maven
- Docker (para RabbitMQ y CockroachDB)
- Spring Boot 3.5+
- RabbitMQ
- CockroachDB
- IntelliJ IDEA (recomendado para ejecutar el proyecto)
- Postman o Locust (para pruebas)

## 🚀 Pasos para Ejecutar

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/tuusuario/encuentro-monitoring.git
   cd encuentro-monitoring
   ```

2. **Levantar servicios de infraestructura (RabbitMQ + CockroachDB)**
   Usa Docker para iniciar los servicios:
   ```bash
   docker compose up -d
   ```
   - Verifica que CockroachDB esté accesible en `localhost:26257`.
   - Verifica que RabbitMQ esté accesible en `localhost:15672` (usuario: `guest`, contraseña: `guest`).

3. **Configurar y ejecutar microservicios en IntelliJ IDEA**
   - Abre IntelliJ IDEA y cargar cada proyecto.
   - Asegúrese de que el JDK 21 esté configurado en el proyecto (`File > Project Structure > Project SDK`).
   - Siga este orden para ejecutar los microservicios:
     1. **Eureka Server** (`ms-eureka`):
        - Abre el módulo `ms-eureka` en IntelliJ.
        - Ejecuta la clase principal (`EurekaServerApplication`) usando la configuración de Spring Boot (`Run > Run 'EurekaServerApplication'`).
     2. **Resto de microservicios** (pueden ejecutarse en cualquier orden después):
        - `ms-patient-data-collector`: Ejecuta `PatientDataCollectorApplication`.
        - `ms-health-analyzer`: Ejecuta `HealthAnalyzerApplication`.
        - `ms-care-notifier`: Ejecuta `CareNotifierApplication`.
     3. **API Gateway** (`ms-api-gateway`):
        - Abre el módulo `ms-api-gateway` en IntelliJ.
        - Ejecuta la clase principal (`ApiGatewayApplication`).
        
   - IntelliJ IDEA detectará automáticamente las configuraciones de Spring Boot para cada módulo. Usa el botón "Run" o crea configuraciones de ejecución si es necesario (`Run > Edit Configurations > Add New Configuration > Spring Boot`).

4. **Acceder al panel de Eureka**
   - Abre un navegador y visita: `http://localhost:8761`.
   - Verás todos los servicios registrados: `SERVICIO-PATIENT-DATA`, `SERVICIO-HEALTH-ANALYZER`, `SERVICIO-CARE-NOTIFIER` y `API-GATEWAY`.

## 📡 Flujo de Peticiones
Todas las peticiones pasan a través del API Gateway (localhost:8000) que enruta según el path
### 🧾 1. Registro de Signos Vitales
- **Ruta**: `POST /conjunta/2p/vital-signs`
- **Microservicio**: `ms-patient-data-collector`
- **Descripción**: Guarda el signo vital y publica un evento a RabbitMQ.
- **Ejemplo de payload**:
  ```json
  {
    "deviceId": "sensor-abc",
    "type": "heart-rate",
    "value": 150.0,
    "timestamp": "2025-07-09T12:45:00Z"
  }
  ```

### 🧠 2. Análisis de Signos Vitales
- **Microservicio**: `ms-health-analyzer`
- **Evento escuchado**: `new.vital.sign`
- **Descripción**:
  - Guarda en la base de datos.
  - Si hay valores críticos, genera una alerta.
  - Publica evento `critical.alert`.

### 🔔 3. Notificación de Alertas
- **Microservicio**: `ms-care-notifier`
- **Eventos escuchados**: `critical.alert`, `device.offline.alert`, `daily.report.generated`
- **Descripción**:
  - Clasifica alertas (`EMERGENCY`, `WARNING`, `INFO`).
  - Las alertas `EMERGENCY` se notifican al instante.
  - Las demás se agrupan y notifican cada 30 minutos.

### 🗓️ 4. Tareas Programadas
Desde `ms-health-analyzer`:

| Tarea                     | Frecuencia        | Acción                                      |
|---------------------------|-------------------|---------------------------------------------|
| Generar reporte diario    | Cada 24h          | Calcula métricas y emite `daily.report.generated` |
| Detectar inactivos        | Cada 6h           | Si un sensor no envió datos en 24h → `device.offline.alert` |
| Archivar datos viejos     | Día 1 de cada mes | Elimina registros con más de 2 años         |

## 🧪 Pruebas con Locust
- **Archivo listo**: `locustfile.py`
- **Ejecutar pruebas**:
  ```bash
  locust -f locustfile.py
  ```
- Abre `http://localhost:8089` en un navegador, configura usuarios, tasa de solicitudes y monitorea las respuestas.

## 🔍 Endpoints de Prueba

| Microservicio         | Ruta                                    | Propósito                       |
|-----------------------|-----------------------------------------|---------------------------------|
| Notifier              | `GET /conjunta/2p/notifier`             | Verificar estado                |
| Notifier              | `POST /conjunta/2p/notifier/mock/email` | Simulación de email             |
| Notifier              | `POST /conjunta/2p/notifier/mock/sms`   | Simulación de SMS               |
| Analyzer              | `GET /conjunta/2p/health/alerts`        | Listar alertas médicas          |
| Analyzer              | `GET /conjunta/2p/health/vitals`        | Ver historial de signos         |
| Collector             | `GET /conjunta/2p/vital-signs/{deviceId}` | Historial por sensor          |

## ✅ Estado Esperado
- **Consola de Eureka**: Todos los servicios activos.
- **RabbitMQ**: Colas activas (`new.vital.sign`, `critical.alert`, etc.).
- **Base de datos**: Registros en las tablas `vital_sign`, `medical_alert`, `notification`.
- **Notificaciones**: Simuladas por consola.

## 👨‍⚕️ Créditos
Desarrollado por Yorman Oña y Martin Suquillo