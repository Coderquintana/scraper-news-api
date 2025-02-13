# Scraper News API

## Descripción

Este proyecto es una API REST en **Java con Spring Boot** que permite realizar **web scraping** en un periódico digital para obtener noticias.

El API obtiene noticias a partir de un término de búsqueda y devuelve información como título, resumen, fecha, enlace y la imagen asociada (opcionalmente en Base64).

---

## Requisitos

Para ejecutar este proyecto, necesitas:

- **Java 21** o superior
- **Maven 3.8+**
- **PostgreSQL** (o puedes modificar `application.properties` para otro motor de BD)
- **Git**
- **Navegador Chromium / Edge / Firefox instalado** (para Selenium)

---

## Instalación y Ejecución

1. **Clonar el repositorio**

   ```sh
   git clone https://github.com/tuusuario/scraper-news-api.git
   cd scraper-news-api
   ```

2. **Configurar la base de datos**

   - Asegúrate de tener PostgreSQL corriendo.
   - Crea una base de datos llamada `scraper`.
   - Modifica `src/main/resources/application.properties` si es necesario.

3. **Compilar el proyecto**

   ```sh
   mvn clean install
   ```

4. **Ejecutar la aplicación**

   ```sh
   mvn spring-boot:run
   ```

---

## Endpoints de la API

### **1️⃣ Buscar noticias**

**Request:**

```http
GET /api-scraper/consulta?q=tu_busqueda
```

**Opcionales:**

- `f=true` (incluye imagen en Base64)

**Headers:**

```http
X-API-KEY: 123456789ABCDEF
X-API-SIGNATURE: TuFirmaHMAC
```

**Ejemplo de respuesta:**

```json
[
    {
        "fecha": "12/02/2025 08:51 A. M.",
        "enlace": "https://www.abc.com.py/noticia",
        "enlaceFoto": "https://www.abc.com.py/imagen.jpg",
        "titulo": "Ejemplo de noticia",
        "resumen": "Este es un resumen de la noticia",
        "contenidoFoto": "iVBORw0KGgoAAAANSUhEUg...",
        "contentTypeFoto": "image/jpeg"
    }
]
```

### **2️⃣ Errores**

- **400:** `{"codigo": "g268", "error": "Parámetros inválidos"}`
- **403:** `{"codigo": "g103", "error": "No autorizado"}`
- **500:** `{"codigo": "g100", "error": "Error interno del servidor"}`

---

## Estructura del Proyecto

```
├── src/
│   ├── main/java/com/example/apificarweb/
│   │   ├── core/controllers/    # Controladores REST
│   │   ├── core/models/         # Modelos de datos
│   │   ├── core/services/       # Lógica de negocio y scraping
│   │   ├── core/repositories/   # Repositorios JPA
│   │   ├── core/config/         # Configuración de API Keys y seguridad
│   │   ├── core/exceptions/     # Manejo global de errores
│   ├── resources/
│   │   ├── application.properties  # Configuración de la BD y API Keys
```

---

## Notas Finales

- Se usó **Selenium WebDriver** para el scraping.
- El API usa **validación con API Keys** para mayor seguridad.
- Se incluye una opción para obtener las imágenes en **Base64**.

### 🔥 **Autor:** Francisco **(Coloca tu usuario de GitHub/GitLab)** 🚀

