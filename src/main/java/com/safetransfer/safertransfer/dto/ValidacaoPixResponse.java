package com.safetransfer.safertransfer.dto;

public class ValidacaoPixResponse {

    private String status;   // "VÁLIDO", "DIVERGENTE" ou "ERRO"
    private String mensagem; // texto pra exibir pro usuário
    private String nomeReal; // nome que veio do banco

    public ValidacaoPixResponse() {}

    public ValidacaoPixResponse(String status, String mensagem, String nomeReal) {
        this.status = status;
        this.mensagem = mensagem;
        this.nomeReal = nomeReal;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }

    public String getNomeReal() { return nomeReal; }
    public void setNomeReal(String nomeReal) { this.nomeReal = nomeReal; }
}
