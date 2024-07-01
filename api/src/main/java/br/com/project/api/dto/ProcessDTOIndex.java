package br.com.project.api.dto;

import java.time.LocalDateTime;

public class ProcessDTOIndex {
    private int id;
    private String npu;
    private LocalDateTime dataCadastro;
    private String municipio;
    private String uf;

    // Construtor
    public ProcessDTOIndex(int id, String npu, LocalDateTime dataCadastro, String uf, String municipio) {
        this.id = id;
        this.npu = npu;
        this.dataCadastro = dataCadastro;
        this.municipio = municipio;
        this.uf = uf;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNpu() {
        return npu;
    }

    public void setNpu(String npu) {
        this.npu = npu;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }
}
