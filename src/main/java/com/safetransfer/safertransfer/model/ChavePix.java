package com.safetransfer.safertransfer.model;



import jakarta.persistence.*;

@Entity
@Table(name = "chave_pix", uniqueConstraints = @UniqueConstraint(name="uk_chave_pix", columnNames = "chave"))
public class ChavePix {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=120)
    private String chave; // email, telefone, CPF, aleatória

    @Column(name="nome_real", nullable=false, length=160)
    private String nomeReal;

    @Column(length=20)
    private String tipo; // opcional: EMAIL/CPF/TELEFONE/ALEATORIA

    public ChavePix() {}
    public ChavePix(String chave, String nomeReal, String tipo) {
        this.chave = chave;
        this.nomeReal = nomeReal;
        this.tipo = tipo;
    }

    public Long getId() { return id; }
    public String getChave() { return chave; }
    public void setChave(String chave) { this.chave = chave; }
    public String getNomeReal() { return nomeReal; }
    public void setNomeReal(String nomeReal) { this.nomeReal = nomeReal; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}
