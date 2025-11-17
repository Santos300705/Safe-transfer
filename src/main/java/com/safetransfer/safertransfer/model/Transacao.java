package com.safetransfer.safertransfer.model;

import jakarta.persistence.*;

@Entity
@Table(name = "transacao")
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String chavePix;
    private String nomeInformado;
    private String nomeReal;
    private String statusValidacao; // VÁLIDO ou DIVERGENTE

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public Transacao() {}

    // Getters e Setters
    public Long getId() { return id; }
    public String getChavePix() { return chavePix; }
    public void setChavePix(String chavePix) { this.chavePix = chavePix; }
    public String getNomeInformado() { return nomeInformado; }
    public void setNomeInformado(String nomeInformado) { this.nomeInformado = nomeInformado; }
    public String getNomeReal() { return nomeReal; }
    public void setNomeReal(String nomeReal) { this.nomeReal = nomeReal; }
    public String getStatusValidacao() { return statusValidacao; }
    public void setStatusValidacao(String statusValidacao) { this.statusValidacao = statusValidacao; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}