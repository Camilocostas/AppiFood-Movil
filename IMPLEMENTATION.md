# 📑 Guía de Implementación Actual - AppiFood Móvil

Este documento detalla los cambios técnicos realizados para profesionalizar la estructura del proyecto y el flujo de trabajo del equipo.

## 🌿 Flujo de Trabajo (GitFlow)
Para mantener el código ordenado, usaremos el siguiente flujo:

1. **Ramas Principales:**
   - `main`: Solo código estable y listo para producción.
   - `develop`: Integración de todas las funcionalidades. Es la rama por defecto.

2. **Crear una Funcionalidad:**
   ```bash
   git checkout develop
   git pull origin develop
   git checkout -b feature/nombre-de-tu-tarea
   ```

3. **Subir Cambios:**
   - No subas directamente a `develop`.
   - Sube tu rama: `git push origin feature/nombre-de-tu-tarea`.
   - Abre un **Pull Request (PR)** en GitHub hacia `develop`.
   - Al menos un compañero debe revisar y aprobar el PR.

## 🚀 Implementaciones Técnicas

### 1. Arquitectura de Navegación (Type-Safe)
- **Archivo:** `navigation/Screen.kt`
- **Uso:** Navegar usando `Screen.Home.route`. Evita usar Strings manuales.

### 2. Capa de Datos (Retrofit)
- **Networking:** Configurado en `data/api/RetrofitClient.kt`.
- **URL Base:** `http://10.0.2.2:8000/api/` (Apunta al localhost del PC desde el emulador).
- **Modelos:** Encontrarás ejemplos en `data/model/Restaurant.kt`.

### 3. Estructura de Carpetas
- `data/`: Repositorios, modelos y API.
- `ui/components/`: Componentes Compose reutilizables.
- `ui/screens/`: Pantallas completas.

### 4. Visualización (Previews)
- Se añadieron `@Preview` en las pantallas principales.
- Usa la pestaña **Design** de Android Studio para previsualizar sin ejecutar la app.

---

## 🛠️ Requisitos previos
- Hacer **Sync Project with Gradle Files** (icono del elefante) al clonar.
- Tener el backend de Laravel corriendo en el puerto 8000.
