# Spacius 🏛️

**Spacius** es una aplicación móvil Android desarrollada en Kotlin para la **gestión y reserva de espacios públicos en Guayaquil**. Permite a los usuarios explorar lugares disponibles, realizar reservas con horarios específicos y visualizar sus reservas tanto en un calendario interactivo como en un mapa con geolocalización.

## ✨ Características Principales

- 🔐 **Autenticación con Firebase**: Registro e inicio de sesión seguro en la nube
- 📍 **Exploración de Lugares**: 10 espacios públicos predefinidos de Guayaquil
- 📅 **Sistema de Reservas**: Reserva lugares con fecha y horario específico
- 🗓️ **Calendario Interactivo**: Visualización mensual de reservas activas
- 🗺️ **Mapa de Reservas**: Geolocalización de tus lugares reservados
- 🔄 **Sincronización en Tiempo Real**: Firestore actualiza todas las vistas automáticamente
- ☁️ **Persistencia en la Nube**: Firebase Firestore para almacenamiento sincronizado

## 🎯 Funcionalidades Detalladas

### 🔐 Autenticación
- Registro de nuevos usuarios con **Firebase Authentication**
- Inicio de sesión con validación de credenciales en la nube
- Gestión segura de sesiones con Firebase Auth
- Persistencia automática de sesión activa
- Manejo de errores específicos (formato, contraseña incorrecta, conexión)

### 🏛️ Exploración de Lugares
- **10 lugares públicos de Guayaquil** almacenados en **Firestore**:
  - Canchas del Parque Samanes
  - Canchas de Handball
  - Área de Picnic Parque Samanes
  - Canchas de Vóley Playero
  - Parque de la Octava Alborada
  - Parques Acuáticos (Bastión Popular, Juan Montalvo, Metropolitano)
  - Estadios (Capwell, Monumental) para bodas colectivas
- Vista en lista con imágenes (Glide)
- Información detallada de cada lugar (descripción, horarios, ubicación)
- **Filtrado inteligente**: Solo muestra lugares no reservados por el usuario actual
- **Inicialización automática**: Los lugares se crean en Firestore al primer uso

### 📅 Sistema de Reservas
- **Selección de fecha**: DatePicker para elegir día de reserva
- **Horario personalizado**: TimePickerDialog para hora inicio y fin
- **Vista previa en mapa**: Ubicación del lugar antes de reservar
- **Confirmación instantánea**: Feedback visual al completar reserva
- **Validación de formulario**: Verifica que todos los campos estén completos
- **Guardado en Firestore**: Sincronización automática con la nube
- **Sistema de disponibilidad**: Verifica conflictos de horario antes de confirmar

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

### Arquitectura de Datos
```
☁️ Firebase (Backend)
├── 🔐 Firebase Authentication
│   ├── Registro de usuarios
│   ├── Inicio de sesión
│   └── Gestión de sesiones
│
└── 🔥 Cloud Firestore
    ├── 📊 Colección: lugares
    │   ├── id (auto-generado)
    │   ├── nombre
    │   ├── descripcion
    │   ├── latitud / longitud
    │   ├── imagenUrl
    │   ├── capacidad
    │   └── disponible (boolean)
    │
    └── 📊 Colección: reservas
        ├── id (auto-generado)
        ├── lugarId (referencia)
        ├── nombreLugar
        ├── fecha (YYYY-MM-DD)
        ├── horaInicio / horaFin
        ├── usuarioId (Firebase Auth UID)
        ├── usuarioEmail
        └── estado (activa/cancelada)
```

### Estructura del Proyecto
```
app/src/main/java/com/example/spacius/
├── 🔐 Autenticación
│   ├── LoginActivity.kt           # Pantalla de inicio de sesión (Firebase Auth)
│   ├── RegisterActivity.kt        # Registro de nuevos usuarios (Firebase Auth)
│   └── SpaciusApplication.kt      # Inicialización de Firebase
│
├── 🏠 Navegación Principal
│   └── MainActivity.kt             # Activity principal con BottomNav
│
├── 📱 Fragments (UI)
│   ├── HomeFragment.kt            # Lista de lugares disponibles
│   ├── ReservaFragment.kt         # Formulario de reserva
│   ├── ReservaExitosaFragment.kt  # Confirmación de reserva
│   ├── CalendarFragment.kt        # Calendario con eventos
│   ├── MapsFragment.kt            # Mapa de reservas
│   ├── SettingsFragment.kt        # Configuración de usuario
│   └── DetalleReservaFragment.kt  # Detalle y cancelación
│
├── ☁️ Repositorio de Datos (Firestore)
│   └── data/
│       ├── FirestoreRepository.kt # Operaciones CRUD con Firestore
│       └── FirestoreModels.kt     # Data classes (LugarFirestore, ReservaFirestore)
│
└── 🎨 Adaptadores
    └── CalendarAdapter.kt         # GridView del calendario
```

### Layouts
```
app/src/main/res/layout/
├── activity_login.xml              # Pantalla de login
├── activity_register.xml           # Pantalla de registro
├── activity_main.xml               # Contenedor principal con BottomNav
├── fragment_home.xml               # Lista de lugares
├── fragment_calendar.xml           # Vista de calendario
├── fragment_maps.xml               # Contenedor del mapa
├── fragment_reserva_exitosa.xml    # Formulario de reserva
├── fragment_detalle_reserva.xml    # Detalles de reserva
├── fragment_settings.xml           # Configuración
├── item_evento.xml                 # Item de evento en calendario
├── item_lugar.xml                  # Item de lugar en lista
└── calendar_day_item.xml           # Día individual del calendario
```

## 🛠️ Tecnologías y Librerías

### Core
- **Kotlin 1.9+**: Lenguaje principal
- **Android SDK 36**: Target SDK
- **Min SDK 24**: Android 7.0+ (Nougat)
- **Gradle (Kotlin DSL)**: Sistema de build

### Backend y Base de Datos
- **Firebase Authentication**: Autenticación de usuarios en la nube
- **Cloud Firestore**: Base de datos NoSQL en tiempo real
- **Firebase BOM 33.4.0**: Gestión de versiones de Firebase
- **Kotlin Coroutines**: Operaciones asíncronas
- **Firebase Analytics**: Analíticas de uso (opcional)

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
- **Cuenta de Firebase** (se incluye google-services.json en el proyecto)
- **API Key de Google Maps** (incluida en el proyecto)

### Dispositivo/Emulador
- **Android 7.0 (API 24)** o superior
- **Conexión a Internet** (requerida para Firebase, imágenes y mapas)
- **Servicios de Google Play** (para Google Maps y Firebase)

## 🚀 Instalación y Configuración

### 1. Clonar el Repositorio
```bash
git clone https://github.com/davmatute-lang/spacius.git
cd spacius
```

### 2. Abrir en Android Studio
- File → Open → Seleccionar carpeta del proyecto
- Esperar sincronización automática de Gradle

### 3. Configurar Firebase (Opcional - ya configurado)
El proyecto ya incluye `google-services.json`. Si necesitas tu propio proyecto:

1. Crear proyecto en [Firebase Console](https://console.firebase.google.com/)
2. Agregar app Android con package `com.example.spacius`
3. Descargar `google-services.json` y colocarlo en `app/`
4. Habilitar Authentication (Email/Password)
5. Crear base de datos Firestore

### 4. Configurar Google Maps (Opcional)
Si necesitas tu propia API Key:
```xml
<!-- AndroidManifest.xml -->
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="TU_API_KEY_AQUI" />
```

### 5. Ejecutar la Aplicación
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

- ✅ **Autenticación con Firebase** - Completo
- ✅ **Base de datos Firestore** - Completo
- ✅ **Exploración de lugares** - Completo
- ✅ **Sistema de reservas** - Completo
- ✅ **Calendario con eventos** - Completo
- ✅ **Mapa de reservas** - Completo
- ✅ **Sincronización en tiempo real** - Completo
- ✅ **Filtrado inteligente** - Completo
- ✅ **Sistema de disponibilidad** - Completo
- ✅ **Cancelación de reservas** - Completo
- ✅ **Código optimizado** - Limpieza realizada (Oct 2025)
- 🚧 **Perfil de usuario completo** - En desarrollo
- 🚧 **Notificaciones push** - Planeado
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


## 🔧 Detalles Técnicos Avanzados

### Gestión de Estado
- **Kotlin Coroutines**: Para operaciones asíncronas eficientes
- **Firebase Listeners**: Observación de cambios en tiempo real
- **Singleton Pattern**: Instancia única del calendario
- **Fragment Tag Management**: Gestión inteligente de fragments
- **Lifecycle Scope**: Coroutines atadas al ciclo de vida de components

### Optimizaciones
- **Lazy Loading**: Carga perezosa de fragments
- **View Recycling**: RecyclerView para listas eficientes
- **Image Caching**: Glide con caché de imágenes
- **Firestore Queries Optimizadas**: Índices y consultas eficientes
- **Coroutine Lifecycle**: Scope atado al ciclo de vida
- **Código limpio**: Eliminación de archivos sin usar (Oct 2025)
  - Removidas activities obsoletas (CalendarActivity, MapsActivity, MapsScreenFragment)
  - Eliminados layouts no utilizados
  - Logs de debug optimizados

### Validaciones
- **Formulario de registro**: Validación de email y contraseña
- **Formulario de reserva**: 
  - Fecha obligatoria
  - Hora inicio y fin requeridas
  - Validación de formato
- **Prevención de duplicados**: No permite reservar lugar ya reservado

### Sincronización
```kotlin
Flujo de Sincronización Automática con Firebase:
1. Usuario realiza acción (reservar/cancelar)
2. Se guarda en Firestore (nube)
3. Firestore actualiza en tiempo real
4. MainActivity se notifica del cambio
5. MainActivity actualiza fragments:
   - HomeFragment (onResume - recarga lugares)
   - CalendarFragment (listener de Firestore)
   - MapsFragment (onResume - recarga marcadores)
6. UI se actualiza automáticamente
7. Cambios sincronizados entre todos los dispositivos
```

## 🧪 Testing y CI/CD

### GitHub Actions
- **Workflow automatizado** en `.github/workflows/android-tests.yml`
- **Branches monitoreadas**: 
  - `main`
  - `Ashlee_Coello`
  - `Dani-Freire`
  - `Diego_Rubio`


## 🔒 Consideraciones de Seguridad

### Implementadas
- ✅ Validación de inputs en formularios
- ✅ Autenticación con Firebase (encriptación en la nube)
- ✅ Reglas de seguridad de Firestore
- ✅ Consultas parametrizadas (Firestore SDK)

### Pendientes (Recomendadas)
- ⚠️ **API Key de Google Maps**: Expuesta en AndroidManifest
- ⚠️ **Ofuscación de código**: ProGuard/R8 en release
- ⚠️ **Reglas de Firestore más estrictas**: Validación de datos en backend

### Mejoras de Seguridad Sugeridas
```kotlin
// Mover API Keys a local.properties
// y usar BuildConfig

// Implementar reglas de Firestore más estrictas:
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /reservas/{reservaId} {
      allow read: if request.auth != null;
      allow create: if request.auth != null && 
                      request.resource.data.usuarioId == request.auth.uid;
      allow update, delete: if request.auth != null && 
                              resource.data.usuarioId == request.auth.uid;
    }
  }
}
```

## 📊 Métricas del Proyecto

### Estadísticas de Código
- **Total de archivos Kotlin**: 15 clases principales (optimizado Oct 2025)
- **Líneas de código**: ~3,000+ líneas (reducido tras limpieza)
- **Activities**: 3 (Login, Register, Main)
- **Fragments**: 7 (Home, Reserva, ReservaExitosa, Calendar, Maps, Settings, Detalle)
- **Layouts XML**: 12 archivos (optimizado)
- **Base de datos**: Firebase Firestore + Firebase Authentication

### Performance
- **Min SDK**: 24 (Android 7.0+) - ~92% de dispositivos
- **Target SDK**: 36 (Android 14)
- **Tamaño de APK**: ~10-14 MB (con Firebase incluido)
- **Tiempo de inicio**: <2 segundos (requiere conexión inicial a Firebase)

### Compatibilidad
- ✅ Android 7.0 (Nougat) - Android 14
- ✅ Teléfonos y tablets
- ✅ Orientación portrait y landscape
- ✅ Diferentes tamaños de pantalla



### Corto Plazo (v1.1)
- [ ] Reglas de seguridad Firestore más estrictas
- [ ] Validación mejorada de emails (formato completo)
- [ ] Modo oscuro
- [ ] Animaciones de transición mejoradas
- [ ] Mensajes de confirmación antes de cancelar
- [ ] Manejo de estado offline mejorado

### Medio Plazo (v1.5)
- [ ] Edición de reservas existentes
- [ ] Sistema de favoritos para lugares
- [ ] Historial completo de reservas pasadas
- [ ] Búsqueda y filtros por tipo de lugar
- [ ] Compartir reservas (Share Intent)
- [ ] Notificaciones push con Firebase Cloud Messaging
- [ ] Sistema de verificación de disponibilidad en tiempo real

### Largo Plazo (v2.0)
- [ ] Arquitectura MVVM + Repository Pattern completo
- [ ] Firebase Cloud Functions para lógica de backend
- [ ] Sistema de puntuación/reviews de lugares
- [ ] Chat o comentarios en lugares
- [ ] Integración con Google Calendar
- [ ] Widget de próximas reservas
- [ ] Múltiples usuarios por cuenta (familias)
- [ ] Panel de administración web

## 🐛 Problemas Conocidos

### Issues Actuales
- API Key de Google Maps visible en código fuente (baja prioridad)
- Requiere conexión a internet constante para Firebase

### Guía para Contribuidores

Este proyecto sigue GitFlow con múltiples branches de desarrollo:

**Branches principales:**
- `main`: Código estable en producción
- `Sistema-de-Disponibilidad-Fire-Base`: Branch activo con Firebase (actual)
- `Ashlee_Coello`: Desarrollo por Ashlee
- `Dani-Freire`: Desarrollo por Dani
- `Diego_Rubio`: Desarrollo por Diego


## 👥 Equipo de Desarrollo

- **Repositorio**: [davmatute-lang/spacius](https://github.com/davmatute-lang/spacius)
- **Organización**: davmatute-lang
- **Proyecto**: Spacius - Sistema de Reservas de Espacios Públicos

### Contribuidores Activos
- Ashlee Coello
- Dani Freire
- Diego Rubio

## 📄 Licencia

Este proyecto es de uso educativo y personal. Desarrollado como proyecto académico.

### Uso Permitido
- ✅ Uso educativo
- ✅ Aprendizaje y referencia
- ✅ Modificaciones personales

### Créditos
Si usas este código, por favor da crédito al equipo original.

---

## 📋 Changelog

### v1.0.1 - Octubre 20, 2025
**🔄 Migración a Firebase y Optimización del Código**

**Nuevas Características:**
- ✅ Migración completa de Room a **Firebase Firestore**
- ✅ Implementación de **Firebase Authentication**
- ✅ Sincronización en tiempo real entre dispositivos
- ✅ Sistema de disponibilidad de lugares mejorado

**Mejoras Técnicas:**
- ✅ Código más limpio y mantenible
- ✅ Build más rápido
- ✅ APK optimizado
- ✅ Estructura de proyecto consistente

---

<div align="center">

**Desarrollado con ❤️ en Kotlin para Android**

![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Android](https://img.shields.io/badge/Android-7.0+-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Material Design](https://img.shields.io/badge/Material%20Design-3-757575?style=for-the-badge&logo=material-design&logoColor=white)

**Spacius** © 2025 | Guayaquil, Ecuador 🇪🇨

</div>
