package br.com.project.api.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "process")
@Getter
@Setter
public class ProcessModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String npu;
    private LocalDateTime data_cadastro;
    private LocalDateTime data_visualizacao;
    private String municipio;
    private String uf;
    private String upload_documento;
}
