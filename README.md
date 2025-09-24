# Spacius

Una aplicación móvil Android desarrollada en Kotlin que proporciona funcionalidades de gestión de usuarios y calendario.

## Características

- **Sistema de Autenticación**: Login y registro de usuarios
- **Calendario Interactivo**: Visualización mensual con navegación entre meses
- **Navegación Bottom**: Interfaz de navegación principal con múltiples secciones
- **Base de Datos Local**: Almacenamiento de usuarios usando SQLite

## Funcionalidades Principales

### 🔐 Autenticación
- Registro de nuevos usuarios (nombre, email, contraseña)
- Inicio de sesión con validación
- Gestión de sesiones de usuario

### 📅 Calendario
- Vista mensual del calendario
- Navegación entre meses (anterior/siguiente)
- Selección de fechas
- Interfaz responsive con EdgeToEdge

### 🧭 Navegación
- Bottom Navigation con 4 secciones:
  - Inicio
  - Calendario
  - Mapa (pendiente)
  - Perfil (pendiente)

## Estructura del Proyecto

```
app/src/main/java/com/example/spacius/
├── LoginActivity.kt          # Pantalla de inicio de sesión
├── RegisterActivity.kt       # Pantalla de registro
├── MainActivity.kt           # Pantalla principal con navegación
├── CalendarActivity.kt       # Actividad del calendario
├── CalendarAdapter.kt        # Adaptador para el GridView del calendario
└── UserDatabaseHelper.kt     # Helper para la base de datos SQLite
```

## Requisitos

- Android Studio
- Android SDK (API 21+)
- Kotlin

## Instalación

1. Clona el repositorio
2. Abre el proyecto en Android Studio
3. Sincroniza las dependencias de Gradle
4. Ejecuta la aplicación en un emulador o dispositivo físico

## Uso

1. **Primera vez**: Regístrate creando una nueva cuenta
2. **Iniciar sesión**: Usa tus credenciales para acceder
3. **Navegar**: Usa el menú inferior para acceder a diferentes secciones
4. **Calendario**: Navega entre meses y selecciona fechas

## Tecnologías Utilizadas

- **Kotlin**: Lenguaje de programación principal
- **Android SDK**: Framework de desarrollo
- **SQLite**: Base de datos local
- **Material Design**: Componentes de UI
- **EdgeToEdge**: Diseño moderno de Android

## Estado del Proyecto

- ✅ Sistema de autenticación completo
- ✅ Calendario básico funcional
- ✅ Navegación principal
- 🚧 Mapa (en desarrollo)
- 🚧 Perfil de usuario (en desarrollo)

## Contribuir

Este es un proyecto en desarrollo. Las contribuciones son bienvenidas para mejorar la funcionalidad existente o agregar nuevas características.

## Licencia

Este proyecto es de uso educativo y personal.
