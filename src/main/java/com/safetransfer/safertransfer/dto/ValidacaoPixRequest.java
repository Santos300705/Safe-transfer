package com.safetransfer.safertransfer.dto;

public class ValidacaoPixRequest {

    @NotBlank(message = "chavePix é obrigatória")
    @Size(max = 120, message = "chavePix não pode ultrapassar 120 caracteres")
    private String chavePix;

    @NotBlank(message = "nomeInformado é obrigatório")
    @Size(max = 160, message = "nomeInformado não pode ultrapassar 160 caracteres")
    private String nomeInformado;

    public ValidacaoPixRequest() {}

    public String getChavePix() { return chavePix; }
    public void setChavePix(String chavePix) { this.chavePix = chavePix; }

    public String getNomeInformado() { return nomeInformado; }
    public void setNomeInformado(String nomeInformado) { this.nomeInformado = nomeInformado; }
}
