package br.com.project.api.dto;

import java.time.LocalDateTime;

public class ProcessDTOVisualizar {

    private int id;
    private String npu;
    private LocalDateTime data_cadastro;
    private String uf;
    private String fileUrl;

    // Construtor, getters e setters
    public ProcessDTOVisualizar(int id, String npu, LocalDateTime data_cadastro, String uf, String fileUrl) {
        this.id = id;
        this.npu = npu;
        this.data_cadastro = data_cadastro;
        this.uf = uf;
        this.fileUrl = fileUrl;
    }

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

    public LocalDateTime getData_cadastro() {
        return data_cadastro;
    }

    public void setData_cadastro(LocalDateTime data_cadastro) {
        this.data_cadastro = data_cadastro;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}
