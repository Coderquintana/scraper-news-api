package com.example.apificarweb.core.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "api_keys")
@Getter
@Setter
public class ApiKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "key_value", unique = true, nullable = false)
    private String keyValue;

    @Column(name = "signature", nullable = false)
    private String signature;

    @Column(name = "activo", nullable = false)
    private boolean activo;
}
