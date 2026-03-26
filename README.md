# 🍽️ AppiFood - Movil 

Aplicación móvil para descubrir restaurantes filtrados por presupuesto, tipo de comida, ubicación y preferencias del usuario. Proyecto destinado para la ciudad de Popayán.

## 🛠️ Tecnologías
- **Entorno:** Android Studio
- **Lenguaje:** Kotlin
- **UI:** Jetpack Compose (Material 3)
- **Navegación:** Navigation Compose
- **Arquitectura:** Estructura modular por pantallas (Screens)
- **Pendientes/Próximamente:** Retrofit (API), Room (DB Local), Coil (Imágenes), Firebase.

## 👥 Equipo
| Integrante | GitHub |
|---|---|
| Mauricio Bustamante | [@MauricioB32](https://github.com/MauricioB32) |
| Hugo Muñoz          | [@HugoMunoz](https://github.com/HugoMunoz) |
| Cristian Acosta     | [@Camilocostas](https://github.com/Camilocostas) |
| Esteban Anaya       | [@Ebam11](https://github.com/Ebam11) |
| Jairo Montes        | [@adsojairo](https://github.com/adsojairo) |

## 🚀 Cómo clonar y ejecutar
1. Abrir con **Android Studio** (Koala o superior recomendado).
2. Sincronizar **Gradle**.
3. Ejecutar en emulador o dispositivo físico (**API mínima: 24 - Android 7.0**).

## 🌿 Flujo de ramas
- `main` → Producción / Versión estable.
- `develop` → Rama de integración del equipo.
- `feature/nombre-funcionalidad` → Desarrollo de nuevas características.

## 📋 Convención de commits
Seguimos el estándar de [Conventional Commits](https://www.conventionalcommits.org/):
- `feat`: Nueva funcionalidad.
- `fix`: Corrección de un error.
- `docs`: Cambios en la documentación.
- `refactor`: Mejora del código sin cambiar su comportamiento.
- `ui`: Cambios relacionados exclusivamente con el diseño/interfaz.

- ## 🔌 Conexión con Backend
- **API:** Laravel (REST)
- **Autenticación:** Laravel Sanctum (Bearer Token)
- **Cliente HTTP:** Retrofit + OkHttp
