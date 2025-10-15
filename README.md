# Spacius 🏛️

**Spacius** es una aplicación móvil Android desarrollada en Kotlin para la **gestión y reserva de espacios públicos en Guayaquil**. Permite a los usuarios explorar lugares disponibles, realizar reservas con horarios específicos y visualizar sus reservas tanto en un calendario interactivo como en un mapa con geolocalización.

## ✨ Características Principales

- 🔐 **Sistema de Autenticación**: Registro e inicio de sesión seguro
- 📍 **Exploración de Lugares**: 10 espacios públicos predefinidos de Guayaquil
- 📅 **Sistema de Reservas**: Reserva lugares con fecha y horario específico
- 🗓️ **Calendario Interactivo**: Visualización mensual de reservas activas
- 🗺️ **Mapa de Reservas**: Geolocalización de tus lugares reservados
- 🔄 **Sincronización en Tiempo Real**: Actualización automática entre secciones
- 💾 **Persistencia Local**: Base de datos Room para almacenamiento offline

## 🎯 Funcionalidades Detalladas

### 🔐 Autenticación
- Registro de nuevos usuarios (nombre, email, contraseña)
- Inicio de sesión con validación de credenciales
- Base de datos SQLite para gestión de usuarios
- Persistencia de sesión activa

### 🏛️ Exploración de Lugares
- **10 lugares públicos de Guayaquil**:
  - Canchas del Parque Samanes
  - Canchas de Handball
  - Área de Picnic Parque Samanes
  - Canchas de Vóley Playero
  - Parque de la Octava Alborada
  - Parques Acuáticos (Bastión Popular, Juan Montalvo, Metropolitano)
  - Estadios (Capwell, Monumental) para bodas colectivas
- Vista en lista con imágenes (Glide)
- Información detallada de cada lugar (descripción, horarios, ubicación)
- **Filtrado inteligente**: Solo muestra lugares disponibles (no reservados)

### 📅 Sistema de Reservas
- **Selección de fecha**: DatePicker para elegir día de reserva
- **Horario personalizado**: TimePickerDialog para hora inicio y fin
- **Vista previa en mapa**: Ubicación del lugar antes de reservar
- **Confirmación instantánea**: Feedback visual al completar reserva
- **Validación de formulario**: Verifica que todos los campos estén completos

### 🗓️ Calendario Interactivo
- **Vista mensual** con navegación entre meses (anterior/siguiente)
- **Marcado visual** de fechas con reservas activas
- **Lista de eventos**: Muestra reservas del mes actual
- **Detalles completos**: Nombre, fecha, horario de cada reserva
- **Click en evento**: Abre vista detallada de la reserva
- **Actualización automática** al crear o cancelar reservas

### 🗺️ Mapa de Reservas
- **Google Maps integrado** con marcadores personalizados
- **Solo muestra tus reservas**: Filtrado de lugares reservados
- **Info Window detallado**:
  - 📍 Nombre del lugar
  - 📅 Fecha de reserva
  - 🕐 Horario (inicio - fin)
  - 👤 Usuario
- **Zoom automático** a la primera reserva
- **Centrado en Guayaquil** (-2.170998, -79.922359)
- **Actualización en tiempo real** al cambiar reservas

### 📋 Gestión de Reservas
- **Ver detalles**: Información completa de cada reserva
- **Cancelar reserva**: Eliminación con confirmación
- **Sincronización automática**:
  - Lugar vuelve a aparecer en Home
  - Marcador desaparece del mapa
  - Fecha se desmarca en calendario

### ⚙️ Configuración
- Pantalla de ajustes de perfil
- Opción de cerrar sesión
- Interfaz Material Design moderna

## 🏗️ Arquitectura y Estructura

### Arquitectura de Datos
```
📦 Base de Datos Room (spacius_db)
├── 📊 Tabla: lugares
│   ├── id (PK, autoincrement)
│   ├── nombre
│   ├── descripcion
│   ├── latitud / longitud
│   ├── imagenUrl
│   ├── fechaDisponible
│   └── horaDisponible
│
└── 📊 Tabla: reservas
    ├── id (PK, autoincrement)
    ├── idLugar (FK a lugares)
    ├── fecha
    ├── horaInicio / horaFin
    └── nombreUsuario

📦 Base de Datos SQLite (spacius.db)
└── 📊 Tabla: usuarios
    ├── id (PK, autoincrement)
    ├── nombre
    ├── email (UNIQUE)
    └── password
```

### Estructura del Proyecto
```
app/src/main/java/com/example/spacius/
├── 🔐 Autenticación
│   ├── LoginActivity.kt           # Pantalla de inicio de sesión
│   ├── RegisterActivity.kt        # Registro de nuevos usuarios
│   └── UserDatabaseHelper.kt      # SQLite para usuarios
│
├── 🏠 Navegación Principal
│   └── MainActivity.kt             # Activity principal con BottomNav
│
├── 📱 Fragments (UI)
│   ├── ui/
│   │   ├── HomeFragment.kt        # Lista de lugares disponibles
│   │   └── ReservaFragment.kt     # Formulario de reserva
│   ├── CalendarFragment.kt        # Calendario con eventos
│   ├── MapsFragment.kt            # Mapa de reservas
│   ├── SettingsFragment.kt        # Configuración de usuario
│   └── DetalleReservaFragment.kt  # Detalle y cancelación
│
├── 💾 Base de Datos (Room)
│   └── data/
│       ├── AppDatabase.kt         # Configuración Room
│       ├── Lugar.kt               # Entity de lugares
│       ├── Reserva.kt             # Entity de reservas
│       ├── LugarDao.kt            # Queries de lugares
│       └── ReservaDao.kt          # Queries de reservas
│
└── 🎨 Adaptadores
    ├── CalendarAdapter.kt         # GridView del calendario
    └── LugarAdapter.kt            # RecyclerView de lugares
```

### Layouts
```
app/src/main/res/layout/
├── activity_login.xml              # Pantalla de login
├── activity_register.xml           # Pantalla de registro
├── activity_main.xml               # Contenedor principal
├── fragment_home.xml               # Lista de lugares
├── fragment_calendar.xml           # Vista de calendario
├── fragment_maps.xml               # Contenedor del mapa
├── fragment_reserva_exitosa.xml    # Formulario de reserva
├── fragment_detalle_reserva.xml    # Detalles de reserva
├── fragment_settings.xml           # Configuración
├── item_lugar.xml                  # Item de lugar en lista
├── item_evento.xml                 # Item de evento en calendario
└── calendar_day_item.xml           # Día individual del calendario
```

## 🛠️ Tecnologías y Librerías

### Core
- **Kotlin 1.9+**: Lenguaje principal
- **Android SDK 36**: Target SDK
- **Min SDK 24**: Android 7.0+ (Nougat)
- **Gradle (Kotlin DSL)**: Sistema de build

### Base de Datos
- **Room 2.6.1**: ORM para SQLite
- **SQLite**: Base de datos local
- **Kotlin Coroutines**: Operaciones asíncronas

### UI/UX
- **Material Design 3**: Componentes de UI modernos
- **BottomNavigationView**: Navegación principal
- **RecyclerView**: Listas eficientes
- **GridView**: Vista de calendario
- **EdgeToEdge**: Interfaz inmersiva

### Mapas e Imágenes
- **Google Maps SDK 18.2.0**: Integración de mapas
- **Glide 4.16.0**: Carga de imágenes optimizada

### Testing
- **JUnit**: Pruebas unitarias
- **Espresso**: Pruebas de UI
- **AndroidX Test**: Framework de testing

## 📋 Requisitos del Sistema

### Desarrollo
- **Android Studio** Hedgehog (2023.1.1) o superior
- **JDK 11** o superior
- **Gradle 8.0+**
- **API Key de Google Maps** (incluida en el proyecto)

### Dispositivo/Emulador
- **Android 7.0 (API 24)** o superior
- **Conexión a Internet** (para cargar imágenes y mapas)
- **Servicios de Google Play** (para Google Maps)

## 🚀 Instalación y Configuración

### 1. Clonar el Repositorio
```bash
git clone https://github.com/davmatute-lang/spacius.git
cd spacius
```

### 2. Abrir en Android Studio
- File → Open → Seleccionar carpeta del proyecto
- Esperar sincronización automática de Gradle

### 3. Configurar Google Maps (Opcional)
Si necesitas tu propia API Key:
```xml
<!-- AndroidManifest.xml -->
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="TU_API_KEY_AQUI" />
```

### 4. Ejecutar la Aplicación
- Conectar dispositivo Android o iniciar emulador
- Click en Run (▶️) o `Shift + F10`

## 📱 Guía de Uso

### Primera Vez
1. **Registro**: Crea una cuenta con nombre, email y contraseña
2. **Login**: Inicia sesión con tus credenciales

### Explorar y Reservar
1. **Home**: Navega por los lugares disponibles
2. **Seleccionar lugar**: Click en "Reservar"
3. **Formulario de reserva**:
   - Selecciona fecha
   - Define hora de inicio
   - Define hora de fin
   - Confirma la reserva
4. **Visualizar**: La reserva aparece en calendario y mapa

### Gestionar Reservas
1. **Calendario**: Ve tus eventos del mes
2. **Click en evento**: Abre detalles completos
3. **Mapa**: Visualiza ubicaciones de tus reservas
4. **Cancelar**: Desde el detalle de la reserva

### Navegación
- **🏠 Inicio**: Explorar lugares disponibles
- **📅 Calendario**: Ver tus reservas por fecha
- **🗺️ Mapa**: Ver ubicaciones reservadas
- **⚙️ Perfil**: Ajustes y cerrar sesión

## 🔄 Flujo de Datos

```mermaid
Usuario → Home → Selecciona Lugar → Formulario Reserva
                                          ↓
                                    Guarda en Room DB
                                          ↓
                    ┌─────────────────────┼─────────────────────┐
                    ↓                     ↓                     ↓
              HomeFragment          CalendarFragment      MapsFragment
           (oculta lugar)        (marca fecha + evento)  (agrega marcador)
```

## 🎨 Capturas y Demos

### Lugares Disponibles
- Lista visual con imágenes de alta calidad
- Información de disponibilidad
- Botón de reserva directo

### Calendario Interactivo
- Vista mensual clara
- Fechas marcadas visualmente
- Lista de eventos debajo del calendario

### Mapa de Reservas
- Marcadores solo de lugares reservados
- Info windows con detalles completos
- Zoom y navegación intuitivos

## 🧪 Testing

### Ejecutar Tests
```bash
# Pruebas unitarias
./gradlew test

# Pruebas instrumentadas
./gradlew connectedAndroidTest
```

### Tests Incluidos
- `LoginActivityTest.kt`: Validación de login
- Tests de base de datos Room
- Tests de UI con Espresso

## 🚧 Estado del Proyecto

- ✅ **Sistema de autenticación** - Completo
- ✅ **Base de datos Room** - Completo
- ✅ **Exploración de lugares** - Completo
- ✅ **Sistema de reservas** - Completo
- ✅ **Calendario con eventos** - Completo
- ✅ **Mapa de reservas** - Completo
- ✅ **Sincronización entre vistas** - Completo
- ✅ **Filtrado inteligente** - Completo
- 🚧 **Perfil de usuario completo** - En desarrollo
- 🚧 **Notificaciones** - Planeado
- 🚧 **Historial de reservas** - Planeado

## 🎨 Características de UI/UX

### Diseño Visual
- **Paleta de Colores Moderna**:
  - Azul primario: `#007AFF`
  - Verde secundario: `#4CAF50`
  - Morado reservas: `#FFBB86FC`
  - Gradientes suaves en fondos
- **Material Design 3**: Componentes modernos y consistentes
- **Edge-to-Edge**: Aprovecha toda la pantalla del dispositivo
- **Modo Claro**: Interfaz luminosa y clara (modo oscuro planeado)

### Animaciones y Transiciones
- Transiciones suaves entre fragments
- Animaciones de zoom en mapas
- Feedback visual en botones y acciones
- Marcadores animados en calendario

### Accesibilidad
- Content Descriptions en todos los elementos
- Tamaños de fuente legibles
- Contraste adecuado en colores
- Navegación intuitiva

### Estados de Carga y Vacío
- **Sin lugares disponibles**: Mensaje amigable invitando a revisar calendario
- **Sin reservas en mapa**: Texto motivacional para hacer primera reserva
- **Sin eventos en calendario**: Indicador de mes sin actividad
- **Carga de imágenes**: Placeholders mientras cargan

## 🔧 Detalles Técnicos Avanzados

### Gestión de Estado
- **Kotlin Coroutines**: Para operaciones asíncronas eficientes
- **LiveData Pattern**: Observación de cambios en datos
- **Singleton Pattern**: Instancia única del calendario
- **Fragment Tag Management**: Gestión inteligente de fragments

### Optimizaciones
- **Lazy Loading**: Carga perezosa de fragments
- **View Recycling**: RecyclerView para listas eficientes
- **Image Caching**: Glide con caché de imágenes
- **Database Singleton**: Una sola instancia de Room
- **Coroutine Lifecycle**: Scope atado al ciclo de vida

### Validaciones
- **Formulario de registro**: Validación de email y contraseña
- **Formulario de reserva**: 
  - Fecha obligatoria
  - Hora inicio y fin requeridas
  - Validación de formato
- **Prevención de duplicados**: No permite reservar lugar ya reservado

### Sincronización
```kotlin
Flujo de Sincronización Automática:
1. Usuario realiza acción (reservar/cancelar)
2. Se guarda en Room Database
3. Se notifica a MainActivity
4. MainActivity actualiza:
   - HomeFragment (onResume)
   - CalendarFragment (función específica)
   - MapsFragment (onResume)
5. UI se actualiza automáticamente
```

## 🧪 Testing y CI/CD

### GitHub Actions
- **Workflow automatizado** en `.github/workflows/android-tests.yml`
- **Branches monitoreadas**: 
  - `main`
  - `Ashlee_Coello`
  - `Dani-Freire`
  - `Diego_Rubio`
- **Tests en cada push/PR**: Ejecuta tests instrumentados
- **Emulador**: API 30, Google APIs, x86_64

### Tests Implementados
```kotlin
// Tests Instrumentados
- LoginActivityTest.kt
  ✓ Validación de credenciales
  ✓ Navegación post-login
  ✓ Mensajes de error

// Tests Unitarios
- ExampleUnitTest.kt
  ✓ Operaciones básicas
  ✓ Lógica de negocio
```

### Ejecutar Tests Localmente
```bash
# Tests unitarios
./gradlew test

# Tests instrumentados (requiere emulador/dispositivo)
./gradlew connectedAndroidTest

# Todos los tests
./gradlew check

# Build de release
./gradlew assembleRelease
```

## 🔒 Consideraciones de Seguridad

### Implementadas
- ✅ Validación de inputs en formularios
- ✅ Consultas SQL parametrizadas (Room)
- ✅ Base de datos local (no expuesta)

### Pendientes (Recomendadas)
- ⚠️ **Encriptación de contraseñas**: Actualmente en texto plano
- ⚠️ **API Key de Google Maps**: Expuesta en AndroidManifest
- ⚠️ **Ofuscación de código**: ProGuard/R8 en release

### Mejoras de Seguridad Sugeridas
```kotlin
// Implementar BCrypt para contraseñas
implementation("org.mindrot:jbcrypt:0.4")

// Mover API Keys a local.properties
// y usar BuildConfig
```

## 📊 Métricas del Proyecto

### Estadísticas de Código
- **Total de archivos Kotlin**: 18 clases principales
- **Líneas de código**: ~3,500+ líneas
- **Activities**: 4 (Login, Register, Main, Calendar)
- **Fragments**: 8 (Home, Reserva, Calendar, Maps, Settings, Detalle, etc.)
- **Layouts XML**: 15+ archivos
- **Base de datos**: 2 tablas Room + 1 tabla SQLite

### Performance
- **Min SDK**: 24 (Android 7.0+) - ~92% de dispositivos
- **Target SDK**: 36 (Android 14)
- **Tamaño de APK**: ~8-12 MB (estimado)
- **Tiempo de inicio**: <2 segundos

### Compatibilidad
- ✅ Android 7.0 (Nougat) - Android 14
- ✅ Teléfonos y tablets
- ✅ Orientación portrait y landscape
- ✅ Diferentes tamaños de pantalla

## 🌐 Configuración de Google Maps

### Obtener tu Propia API Key

1. **Google Cloud Console**:
   ```
   https://console.cloud.google.com/
   ```

2. **Crear proyecto** y habilitar APIs:
   - Maps SDK for Android
   - Places API (si se necesita)

3. **Crear credenciales** (API Key)

4. **Agregar a tu proyecto**:
   ```xml
   <!-- AndroidManifest.xml -->
   <meta-data
       android:name="com.google.android.geo.API_KEY"
       android:value="TU_API_KEY_AQUI" />
   ```

5. **Restricciones de seguridad**:
   - Restringir por aplicación (SHA-1)
   - Limitar APIs habilitadas

## 🔮 Roadmap y Próximas Mejoras

### Corto Plazo (v1.1)
- [ ] Encriptación de contraseñas con BCrypt
- [ ] Validación mejorada de emails (formato completo)
- [ ] Modo oscuro
- [ ] Animaciones de transición mejoradas
- [ ] Mensajes de confirmación antes de cancelar

### Medio Plazo (v1.5)
- [ ] Edición de reservas existentes
- [ ] Sistema de favoritos para lugares
- [ ] Historial completo de reservas pasadas
- [ ] Búsqueda y filtros por tipo de lugar
- [ ] Compartir reservas (Share Intent)
- [ ] Notificaciones locales de recordatorio

### Largo Plazo (v2.0)
- [ ] Arquitectura MVVM + Repository Pattern
- [ ] Backend con API REST
- [ ] Autenticación con Firebase
- [ ] Sincronización en la nube
- [ ] Sistema de puntuación/reviews
- [ ] Chat o comentarios en lugares
- [ ] Integración con Google Calendar
- [ ] Widget de próximas reservas
- [ ] Múltiples usuarios por cuenta

## 🐛 Problemas Conocidos

### Issues Actuales
- Contraseñas almacenadas sin encriptar (alta prioridad)
- API Key de Google Maps visible en código fuente
- Sin recuperación de contraseña

### Limitaciones
- Sin modo offline completo (mapas requieren internet)
- Sin sincronización entre dispositivos
- Límite de una reserva por lugar

## 🤝 Contribuir

### Guía para Contribuidores

Este proyecto sigue GitFlow con múltiples branches de desarrollo:

**Branches principales:**
- `main`: Código estable en producción
- `Ashlee_Coello`: Desarrollo por Ashlee
- `Dani-Freire`: Desarrollo por Dani
- `Diego_Rubio`: Desarrollo por Diego

**Proceso de contribución:**

1. **Fork** el proyecto

2. **Clonar** tu fork:
   ```bash
   git clone https://github.com/TU-USUARIO/spacius.git
   ```

3. **Crear branch** para tu feature:
   ```bash
   git checkout -b feature/NombreFeature
   ```

4. **Desarrollar** y hacer commits:
   ```bash
   git commit -m "feat: descripción del cambio"
   ```

5. **Push** a tu fork:
   ```bash
   git push origin feature/NombreFeature
   ```

6. **Abrir Pull Request** a la rama correspondiente

### Convenciones de Código
- **Kotlin Style Guide**: Seguir convenciones oficiales de Kotlin
- **Comentarios**: Usar español para comentarios del equipo
- **Commits**: Usar conventional commits (feat, fix, docs, etc.)
- **Nombres**: CamelCase para clases, camelCase para funciones

### Áreas para Contribuir
- 🐛 Corrección de bugs
- ✨ Nuevas funcionalidades
- 📝 Mejoras en documentación
- 🎨 Mejoras de UI/UX
- 🧪 Agregar más tests
- 🔒 Mejoras de seguridad

## 👥 Equipo de Desarrollo

- **Repositorio**: [davmatute-lang/spacius](https://github.com/davmatute-lang/spacius)
- **Organización**: davmatute-lang
- **Proyecto**: Spacius - Sistema de Reservas de Espacios Públicos

### Contribuidores Activos
- Ashlee Coello
- Dani Freire
- Diego Rubio

## 📞 Soporte y Contacto

- **Issues**: [GitHub Issues](https://github.com/davmatute-lang/spacius/issues)
- **Documentación**: Ver este README
- **Wiki**: (Próximamente)

## 📄 Licencia

Este proyecto es de uso educativo y personal. Desarrollado como proyecto académico.

### Uso Permitido
- ✅ Uso educativo
- ✅ Aprendizaje y referencia
- ✅ Modificaciones personales

### Créditos
Si usas este código, por favor da crédito al equipo original.

---

<div align="center">

**Desarrollado con ❤️ en Kotlin para Android**

![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Android](https://img.shields.io/badge/Android-7.0+-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Material Design](https://img.shields.io/badge/Material%20Design-3-757575?style=for-the-badge&logo=material-design&logoColor=white)

**Spacius** © 2025 | Guayaquil, Ecuador 🇪🇨

</div>
