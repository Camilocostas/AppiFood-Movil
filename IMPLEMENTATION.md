# 📑 Guía de Implementación Actual - AppiFood Móvil

Este documento detalla los cambios técnicos realizados para profesionalizar la estructura del proyecto y el flujo de trabajo del equipo.

---

## 🌿 1. Flujo de Trabajo (GitFlow)
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
   - Sube tu rama: `git push origin feature/nombre-de-tu-tarea`.
   - Abre un **Pull Request (PR)** en GitHub hacia `develop`.
   - Al menos un compañero debe revisar y aprobar el PR.

---

## 🚀 2. Implementaciones Técnicas Realizadas

### A. Arquitectura por Capas (Clean Architecture Lite)
Se separó la lógica de negocio de la interfaz:
- `data/`: Repositorios, modelos y API.
- `ui/components/`: Elementos reutilizables (ej: `RestaurantCard.kt`).
- `ui/screens/`: Pantallas principales.

### B. Navegación Type-Safe (Seguridad de Tipos)
- **Archivo:** `navigation/Screen.kt`
- **Uso:** Navegar usando `Screen.Home.route`. Evita errores de escritura manual de texto.

### C. Capa de Red (Networking con Retrofit)
- **Networking:** Configurado en `data/api/RetrofitClient.kt`.
- **URL Base:** `http://10.0.2.2:8000/api/` (Localhost desde el emulador).
- **Modelos:** Definidos en `data/model/Restaurant.kt`.

### D. Visualización (Previews de Compose)
- Se añadieron `@Preview` en pantallas principales.
- **Uso:** Pestaña **Design** de Android Studio para ver la UI sin ejecutar la app.

### E. Compatibilidad SDK 36 (Última Versión)
- Se actualizó `compileSdk` y `targetSdk` a la **versión 36**.
- **Razón:** Necesario para soportar las últimas librerías de AndroidX y resolver errores de metadatos AAR.

---

## 🛠️ 3. Requisitos para el Equipo
1. **Instalar SDK 36:** Ir a *Settings > Languages & Frameworks > Android SDK* y descargar la API 36.
2. **Sincronizar Gradle:** Al clonar, hacer clic en el icono del elefante (Sync).
3. **Backend:** Tener el backend de Laravel corriendo en el puerto 8000.
