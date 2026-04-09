# 📑 Guía de Implementación Actual - AppiFood Móvil

Este documento detalla la arquitectura profesional del proyecto, las últimas integraciones realizadas y el flujo de trabajo obligatorio para el equipo.

---

## 🌿 1. Flujo de Trabajo (GitFlow)
Para mantener el código ordenado, usaremos el siguiente flujo:

1. **Ramas Principales:**
   - `main`: Código estable y listo para producción.
   - `develop`: Rama base de desarrollo e integración. **Es la rama por defecto.**

2. **Desarrollo de Funcionalidades:**
   ```bash
   git checkout develop
   git pull origin develop
   git checkout -b feature/nombre-de-tu-tarea
   ```

3. **Integración:**
   - Sube tu rama: `git push origin feature/nombre-de-tu-tarea`.
   - Abre un **Pull Request (PR)** hacia `develop`.
   - Al menos un compañero debe revisar y aprobar el PR para poder hacer el merge.

---

## 🚀 2. Reporte de Integraciones Técnicas (Abril 2024)

Se ha realizado una reestructuración profunda para convertir el prototipo en una aplicación de nivel profesional:

1. **Arquitectura MVVM:** Se ha implementado el patrón Modelo-Vista-ViewModel para separar la lógica de la interfaz.
2. **Navegación Type-Safe:** Uso de `Sealed Classes` en `navigation/Screen.kt` para rutas seguras.
3. **Networking (Retrofit):** Configuración de cliente API para conectar con el backend de Laravel (`10.0.2.2:8000`).
4. **Actualización SDK 36:** Migración a la última versión de Android para soportar librerías modernas de AndroidX.
5. **Integración de Avances:** Se han fusionado las vistas anteriores (Carrito, Perfil, Filtros) dentro de la nueva arquitectura profesional.

---

## 💡 3. Reglas de Oro para el Desarrollo

Para mantener la calidad y el orden del proyecto, a partir de ahora:

- **Lógica:** TODO el manejo de estados, llamadas a API y lógica de negocio va en el **ViewModel** (`ui/viewmodel/`).
- **Datos:** Las clases de datos (POJOs), modelos y lógica de red van en **`data/model/`** o **`data/api/`**.
- **Interfaz (UI):** Los archivos en **`ui/screens/`** deben ser "tontos"; solo se encargan de pintar la UI basándose en lo que el ViewModel les provea.
- **Componentes:** Elementos pequeños y repetitivos (botones, cards) deben ir en **`ui/components/`**.

---

## 🛠️ 4. Requisitos para el Equipo
1. **Sincronizar Gradle:** Al clonar o actualizar, haz clic en el icono del elefante (Sync).
2. **Instalar SDK 36:** Descarga la API 36 desde el SDK Manager de Android Studio.
3. **Backend:** Asegúrate de tener Laravel corriendo en el puerto 8000 para las pruebas de red.
