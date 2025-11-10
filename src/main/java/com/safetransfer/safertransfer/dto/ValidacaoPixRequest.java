package com.safetransfer.safertransfer.dto;

public class ValidacaoPixRequest {

    @NotBlank(message = "chavePix é obrigatória")
    @Size(max = 120, message = "chavePix não pode ultrapassar 120 caracteres")
    private String chavePix;

    @NotBlank(message = "nomeInformado é obrigatório")
    @Size(max = 160, message = "nomeInformado não pode ultrapassar 160 caracteres")
    private String nomeInformado;

    @NotNull(message = "valor é obrigatório")
    @DecimalMin(value = "0.01", inclusive = true, message = "valor deve ser maior que zero")
    private Double valor;

    @NotNull(message = "usuarioId é obrigatório")
    private Long usuarioId;

    // === Construtor padrão (necessário para desserializar JSON) ===
    public ValidacaoPixRequest() {}

    // === Getters/Setters ===
    public String getChavePix() { return chavePix; }
    public void setChavePix(String chavePix) { this.chavePix = chavePix; }

    public String getNomeInformado() { return nomeInformado; }
    public void setNomeInformado(String nomeInformado) { this.nomeInformado = nomeInformado; }

    public Double getValor() { return valor; }
    public void setValor(Double valor) { this.valor = valor; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
}