package com.example.apificarweb.core.services;

import com.example.apificarweb.core.models.Noticia;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

@Slf4j
@Service
public class WebScraperService {

    private static final String BASE_URL = "https://www.abc.com.py/buscador/?query=";

    @Value("${webdriver.browser}")
    private String browser;

    public List<Noticia> buscarNoticias(String query, boolean incluirImagen) {
        WebDriver driver = getWebDriver();
        List<Noticia> noticias = new ArrayList<>();

        try {
            driver.get(BASE_URL + query);
            List<WebElement> elementos = driver.findElements(By.className("queryly_item_row"));

            for (WebElement elemento : elementos) {
                Noticia noticia = new Noticia();
                noticia.setTitulo(getTextOrEmpty(elemento, By.className("queryly_item_title")));
                noticia.setEnlace(getHref(elemento));
                noticia.setResumen(getTextOrEmpty(elemento, By.className("queryly_item_description")));
                noticia.setFecha(getTextOrEmpty(elemento, By.cssSelector("div[style*='font-size:12px']")));

                // 🔥 Obtener la URL de la imagen
                String imageUrl = getImageUrl(elemento);
                noticia.setEnlaceFoto(imageUrl);

                // ✅ Si `f=true`, convertimos la imagen a Base64
                if (incluirImagen && imageUrl != null) {
                    String[] imagenBase64 = obtenerImagenBase64(imageUrl);
                    noticia.setContenidoFoto(imagenBase64[0]);   // Imagen en Base64
                    noticia.setContentTypeFoto(imagenBase64[1]); // Content-Type
                }

                noticias.add(noticia);
            }
        } catch (Exception e) {
            log.error("❌ Error en el scraping: {}", e.getMessage());
        } finally {
            try {
                driver.quit();
            } catch (Exception e) {
                log.warn("⚠️ Error cerrando WebDriver: {}", e.getMessage());
            }
        }

        return noticias;
    }


    private WebDriver getWebDriver() {
        switch (browser.toLowerCase()) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                log.info("🚀 Iniciando WebDriver para Chrome...");
                return new ChromeDriver();
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                log.info("🚀 Iniciando WebDriver para Firefox...");
                return new FirefoxDriver();
            case "edge":
            default:
                WebDriverManager.edgedriver().setup();
                log.info("🚀 Iniciando WebDriver para Edge...");
                EdgeOptions options = new EdgeOptions();
                options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1080");
                options.addArguments("--lang=es-ES"); // ✅ Fuerza el idioma español
                options.addArguments("--disable-blink-features=AutomationControlled"); // ✅ Evita detección de bots
                return new EdgeDriver(options);

        }
    }

    private String getTextOrEmpty(WebElement element, By by) {
        try {
            return element.findElement(by).getText();
        } catch (NoSuchElementException e) {
            return "";
        }
    }

    private String getHref(WebElement element) {
        try {
            String href = element.findElement(By.tagName("a")).getAttribute("href");
            return href.startsWith("https") ? href : "https://www.abc.com.py" + href;
        } catch (NoSuchElementException e) {
            return "";
        }
    }

    private String getImageUrl(WebElement element) {
        try {
            WebElement imageContainer = element.findElement(By.className("queryly_advanced_item_imagecontainer"));
            String style = imageContainer.getAttribute("style");

            log.info("🔍 Analizando estilo de imagen: {}", style);

            // Expresión regular para capturar la primera URL dentro del atributo style
            Pattern pattern = Pattern.compile("background-image:\\s*url\\([\"']?(.*?)[\"']?\\)");
            Matcher matcher = pattern.matcher(style);

            if (matcher.find()) {
                String imageUrl = matcher.group(1);

                // Si la URL es relativa, añadir el dominio
                if (!imageUrl.startsWith("http")) {
                    imageUrl = "https://www.abc.com.py" + imageUrl;
                }

                log.info("✅ Imagen extraída correctamente: {}", imageUrl);
                return imageUrl;
            } else {
                log.warn("⚠️ No se pudo encontrar una URL válida en el estilo.");
            }
        } catch (Exception e) {
            log.warn("⚠️ Error al extraer la imagen: {}", e.getMessage());
        }
        return null;
    }

    private String[] obtenerImagenBase64(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return new String[]{null, null};
        }

        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();

            String contentType = connection.getContentType();
            InputStream inputStream = connection.getInputStream();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outputStream.close();

            // Convertimos la imagen a Base64
            String base64Image = Base64.getEncoder().encodeToString(outputStream.toByteArray());

            return new String[]{base64Image, contentType};
        } catch (Exception e) {
            log.warn("❌ Error al descargar imagen {}: {}", imageUrl, e.getMessage());
            return new String[]{null, null};
        }
    }
}
