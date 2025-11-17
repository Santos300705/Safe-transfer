package com.safetransfer.safertransfer.dto;

public class ValidacaoPixRequest {

    @NotBlank(message = "chavePix é obrigatória")
    @Size(max = 120, message = "chavePix não pode ultrapassar 120 caracteres")
    private String chavePix;

    public ValidacaoPixRequest() {
    }

    public String getChavePix() {
        return chavePix;
    }

    public void setChavePix(String chavePix) {
        this.chavePix = chavePix;
    }

    public Long getUsuarioId() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUsuarioId'");
    }

    public String getNomeInformado() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getNomeInformado'");
    }
}
