# Subastas - Plataforma de Subastas en Tiempo Real

Aplicación Android nativa para subastas en línea en tiempo real. Los usuarios pueden publicar artículos para subastar, ofertar sobre artículos de otros usuarios y seguir el historial de pujas en directo mediante WebSocket.

---

## Tabla de Contenidos

- [Características](#características)
- [Capturas de pantalla](#capturas-de-pantalla)
- [Arquitectura](#arquitectura)
- [Tecnologías y Librerías](#tecnologías-y-librerías)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Requisitos](#requisitos)
- [Configuración y Ejecución](#configuración-y-ejecución)
- [Permisos](#permisos)
- [API y WebSocket](#api-y-websocket)
- [Base de Datos Local](#base-de-datos-local)
- [Equipo](#equipo)

---

## Características

### Autenticación
- Registro de nuevos usuarios
- Inicio de sesión con JWT (Bearer Token)
- Persistencia de sesión mediante DataStore
- Cierre de sesión

### Subastas (Productos)
- Listado de subastas activas con precio actual
- Detalle completo de cada subasta: vendedor, precio inicial, precio actual, estado, fechas de inicio y fin
- Creación de nuevas subastas con nombre, descripción, precio inicial, imagen y fechas
- Subida de imagen desde galería o cámara (la opción de cámara se muestra únicamente si el dispositivo dispone de dicha funcionalidad)
- Eliminación de subasta propia

### Pujas en Tiempo Real
- Realizar pujas sobre subastas activas
- Actualización automática del historial de pujas mediante WebSocket
- Caché local de pujas con Room (disponible sin conexión)
- Notificación con vibración (haptic feedback) cuando otro usuario supera tu puja
- Reproducción de sonido de victoria al declararse un ganador
- Banner visual de ganador con el nombre y cantidad ganadora

### Perfil de Usuario
- Visualización de datos del perfil
- Edición de nombre y contraseña
- Eliminación de cuenta

### Tareas en Segundo Plano y Hardware
- Sincronización transparente a través de un `CoroutineWorker` con **WorkManager**.
- Ahorro de la energía del dispositivo mediante Constraints (`RequiresBatteryNotLow`, `NetworkType.CONNECTED`).
- Sistema de **Runtime Permissions** reactivo con Compose manejando diálogos interactivos de Rationale (Ubicación, Cámara).

---

## Capturas de Pantalla

> Las imágenes se añadirán próximamente.

| Login | Home | Detalle de Subasta | Pujas |
|---|---|---|---|
| — | — | — | — |

---

## Arquitectura

La aplicación sigue los principios de **Clean Architecture** combinados con el patrón de presentación **MVVM**.

```
┌─────────────────────────────────────────────────┐
│                 Presentation Layer               │
│   Compose Screens  ←→  HiltViewModels           │
│              StateFlow<UIState>                  │
└────────────────────┬────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────┐
│                  Domain Layer                    │
│        Use Cases  ·  Repository Interfaces       │
│           Entidades de dominio puras             │
└────────────────────┬────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────┐
│                   Data Layer                     │
│  Repository Impls  ·  Retrofit APIs  ·  DTOs    │
│  Room DAOs  ·  DataStore  ·  WebSocketManager   │
└─────────────────────────────────────────────────┘
```

### Decisiones de diseño clave

- **Single Activity**: toda la navegación se gestiona con Jetpack Compose Navigation y un `NavHostController` central.
- **Inyección de dependencias**: Hilt con módulos por capa (`NetworkModule`, `DatabaseLiteModule`, `HardwareModule`) y módulos de feature para los bindings de repositorio.
- **Estado inmutable**: cada pantalla expone un `StateFlow<UIState>` con data classes selladas, sin mutaciones directas desde la UI.
- **Gestión de errores**: `runCatching` / `Result<T>` en la capa de datos; `ApiErrorParser` convierte cuerpos HTTP de error en mensajes legibles para el usuario.
- **Abstracción de hardware**: interfaz `FeatureManager` e implementación `AndroidFeatureManager` inyectada vía Hilt, usada para determinar en tiempo de ejecución si el dispositivo tiene cámara.

---

## Tecnologías y Librerías

| Categoría | Librería |
|---|---|
| Lenguaje | Kotlin |
| UI | Jetpack Compose BOM · Material 3 · Extended Icons · Google Fonts |
| Navegación | `androidx.navigation.compose` |
| Inyección de dependencias | Hilt Android · `hilt-navigation-compose` |
| Networking | Retrofit 2 · `retrofit2-kotlinx-serialization-converter` |
| Serialización | `kotlinx.serialization.json` |
| Carga de imágenes | Coil (`coil-compose`) |
| WebSocket | OkHttp (integrado en el cliente Retrofit) |
| Concurrencia Términal | WorkManager (`work-runtime-ktx`, `hilt-work`) |
| Persistencia local | Room (runtime · ktx · compilador KSP) con @Relation |
| Preferencias | AndroidX DataStore (Preferences) |
| Build tooling | KSP (Kotlin Symbol Processing) · Secrets Gradle Plugin |
| Testing | JUnit · AndroidX Test · Espresso · Compose UI Test |

---

## Estructura del Proyecto

```
com.josetoanto.subastas/
├── App.kt                              # Clase Application con @HiltAndroidApp
├── MainActivity.kt                     # Única Activity del proyecto
│
├── core/
│   ├── database/
│   │   ├── AppDataBase.kt              # Base de datos Room
│   │   ├── dao/
│   │   │   ├── PujaDao.kt              # Operaciones CRUD de pujas
│   │   │   └── ProductoDao.kt          # Mutaciones transaccionales compuestas
│   │   └── entities/
│   │       ├── PujaEntity.kt           # Entidad local Room
│   │       ├── ProductoEntity.kt       # Entidad base de Subastas 
│   │       └── ProductoWithPujas.kt    # Clase Relacional DTO (1:N)
│   ├── di/
│   │   ├── DatabaseLiteModule.kt       # Módulo Hilt para Room
│   │   ├── HardwareModule.kt           # Módulo Hilt para hardware
│   │   ├── NetworkModule.kt            # Retrofit, OkHttp, Interceptor JWT
│   │   └── Qualifiers.kt              # Qualifiers Hilt personalizados
│   ├── hardware/
│   │   ├── data/AndroidFeatureManager.kt
│   │   └── domain/FeatureManager.kt    # Interfaz de abstracción de hardware
│   ├── navigation/
│   │   ├── Navigation.kt               # NavHost con todas las rutas
│   │   └── Screens.kt                  # Sealed class con rutas tipadas
│   ├── ui/
│   │   ├── components/                 # Composables base reutilizables (Botones, EmptyStates)
│   │   └── theme/                      # Color, Tipografía, Tema
│   ├── utils/
│   │   ├── ApiErrorParser.kt           # Parseo de errores HTTP
│   │   ├── HapticUtils.kt             # Vibración al ser superado en puja
│   │   └── SoundUtils.kt              # Sonido de victoria con SoundPool
│   ├── websocket/
│   │   └── WebSocketManager.kt         # Singleton WebSocket con SharedFlow
│   └── worker/
│       └── SyncWorker.kt               # Trabajo de fondo gestionado por HiltWork
│
└── features/
    ├── auth/
    │   ├── data/                       # AuthApi, TokenDataStore, Repo impl, DTOs, Mapper
    │   ├── domain/                     # User, AuthToken, AuthRepository, Use Cases
    │   └── presentation/               # LoginScreen, RegisterScreen, AuthViewModel
    ├── productos/
    │   ├── data/                       # ProductosApi, Repo impl, DTOs, Mapper
    │   ├── domain/                     # Producto, ProductoDetail, Repository, 5 Use Cases
    │   └── presentation/               # HomeScreen, ProductoDetailScreen, CreateProductoScreen, ViewModels
    ├── pujas/
    │   ├── data/                       # PujasApi, Repo impl con caché, DTOs, Mapper
    │   ├── domain/                     # Puja, Ganador, PujasRepository, 3 Use Cases
    │   └── presentation/               # PujasScreen, PujasViewModel, PujaCard
    └── profile/
        ├── data/                       # ProfileApi, Repo impl, DTOs, Mapper
        ├── domain/                     # Profile, ProfileRepository, 3 Use Cases
        └── presentation/               # ProfileScreen, ProfileViewModel
```

### Flujo de navegación

```
Login  ←→  Register
  │
  ▼
Home (listado de subastas)
  │
  ├──► ProductoDetail (detalle + botón eliminar si es propietario)
  │         │
  │         └──► PujasScreen (formulario de puja + historial en vivo)
  │
  ├──► CreateProductoScreen (crear nueva subasta)
  │
  └──► ProfileScreen (perfil, edición, eliminación de cuenta)
```

---

## Requisitos

| Requisito | Valor |
|---|---|
| Android mínimo | **7.0 Nougat (API 24)** |
| Android objetivo | **Android 16 (API 36)** |
| Kotlin | 2.x |
| Compatibilidad Java | **Java 11** |
| Android Studio | Ladybug o superior (con soporte para KSP y Compose) |

---

## Configuración y Ejecución

### 1. Clonar el repositorio

```bash
git clone https://github.com/<usuario>/Subastas.git
cd Subastas
```

### 2. Abrir en Android Studio

Abre el proyecto en Android Studio (File → Open). Espera a que Gradle sincronice las dependencias.

### 3. Variables de entorno (si aplica)

El proyecto usa el **Secrets Gradle Plugin**. Si existe un archivo `secrets.properties` en la raíz, asegúrate de crearlo con los valores necesarios (por ejemplo, URL base del API si está externalizada).

### 4. Compilar y ejecutar

Conecta un dispositivo Android o inicia un emulador con API 24+ y pulsa **Run** en Android Studio, o desde terminal:

```bash
./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```

### 5. Ejecutar tests

```bash
# Tests unitarios
./gradlew test

# Tests instrumentados (requiere dispositivo/emulador)
./gradlew connectedAndroidTest
```

---

## Permisos

| Permiso | Uso |
|---|---|
| `INTERNET` | Llamadas a la API REST y conexión WebSocket |
| `ACCESS_FINE_LOCATION` | Fetch del Location Client con diálogos Reactivos tipo Rationale en pantalla de listados |
| `READ_MEDIA_IMAGES` | Selección de imágenes desde galería (Android 13+) |
| `READ_EXTERNAL_STORAGE` *(maxSdkVersion 32)* | Selección de imágenes desde galería (Android 12 e inferior) |
| `CAMERA` | Captura de fotografías para nuevas subastas |
| `VIBRATE` | Feedback háptico al ser superado en una puja |

La funcionalidad de cámara se declara con `required="false"`, por lo que la aplicación puede instalarse en dispositivos sin cámara; en ese caso, la opción de capturar foto queda oculta.

---

## API y WebSocket

### Base URL

```
http://3.211.145.251:8000/api/v1/
```

### Endpoints principales

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/auth/register` | Registro de usuario |
| `POST` | `/auth/login` | Inicio de sesión, devuelve JWT |
| `GET` | `/productos/` | Listado de subastas |
| `GET` | `/productos/{id}` | Detalle de subasta |
| `POST` | `/productos/` | Crear subasta |
| `DELETE` | `/productos/{id}` | Eliminar subasta |
| `GET` | `/pujas/{producto_id}` | Historial de pujas |
| `POST` | `/pujas/` | Realizar una puja |
| `GET` | `/pujas/ganador/{producto_id}` | Consultar ganador |
| `GET` | `/users/me` | Perfil del usuario autenticado |
| `PUT` | `/users/me` | Actualizar perfil |
| `DELETE` | `/users/me` | Eliminar cuenta |

### Autenticación

Todas las rutas protegidas requieren el header:

```
Authorization: Bearer <token>
```

El token se almacena en DataStore y se inyecta automáticamente mediante un `AuthInterceptor` en OkHttp.

### WebSocket

```
ws://3.211.145.251:8000/api/v1/pujas/ws/{producto_id}
```

El `WebSocketManager` gestiona el ciclo de vida de la conexión y expone un `SharedFlow<String>`. El `PujasViewModel` se suscribe a este flujo y recarga el listado de pujas ante cualquier mensaje entrante, garantizando actualizaciones en tiempo real.

---

## Base de Datos Local (Room)

Room se usa poderosamente para **cachear el historial de operaciones y el estado**, proporcionando tanto acceso offline como eficiencia modular en la interfaz de persistencia.

### Uso estratégico: Relaciones DAA 
Añadimos **Relaciones 1:N** entre tablas base:
- **`ProductoEntity` y `PujaEntity`**: Empaquetadas bajo en la data class `ProductoWithPujas` vía la anotación `@Relation`.
- **Transacciones Optimizadas**: Las funciones críticas están etiquetados con `@Transaction` en `ProductoDao.kt`, aislando así operaciones compuestas (solicitud de las tablas padre-hijos en cascada) haciéndolo más seguro bajo bloqueos multi-hilo o concurrencias asincronicas.

**Tabla: `pujas`**

| Columna | Tipo | Descripción |
|---|---|---|
| `id` | `Int` (PK) | Identificador de la puja |
| `producto_id` | `Int` | ID de la subasta asociada |
| `usuario_id` | `Int` | ID del usuario que pujó |
| `nombre_postor` | `String` | Nombre del postor |
| `cantidad` | `Double` | Cantidad ofertada |
| `fecha` | `String` | Fecha y hora de la puja |

**Estrategia de caché en `PujasRepositoryImpl`:**
1. Se leen primero los datos del caché local.
2. Se intenta la llamada remota.
3. En caso de éxito remoto: se limpia el caché del producto y se reemplaza con los datos frescos.
4. En caso de error remoto: se devuelven los datos en caché si están disponibles.

---

## Equipo

| Desarrollador | Rol |
|---|---|
| Josetoanto | Desarrollo Android |
| Joaquín | Desarrollo Android |

---

*Proyecto desarrollado con Kotlin y Jetpack Compose.*
