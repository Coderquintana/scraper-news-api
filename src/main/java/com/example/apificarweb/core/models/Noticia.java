package com.example.apificarweb.core.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Noticia {
    private String fecha;
    private String enlace;
    private String enlaceFoto;
    private String titulo;
    private String resumen;
    private String contenidoFoto;
    private String contentTypeFoto;
}
