package com.safetransfer.safertransfer.service;

import java.text.Normalizer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.safetransfer.safertransfer.model.Usuario;
import com.safetransfer.safertransfer.repository.UsuarioRepository;

@Service
public class ValidacaoPixService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Busca nome real a partir da chave Pix (email)
    public String buscarNomePorChave(String chavePix) {
        if (chavePix == null) return null;

        String normalizada = chavePix.trim();

        return usuarioRepository.findFirstByEmailIgnoreCase(normalizada)
                .map(Usuario::getNomeCompleto)
                .orElse(null);
    }

    // Versão obrigatória: se não achar, lança erro de regra
    public String buscarNomePorChaveObrigatorio(String chavePix) {
        String nomeReal = buscarNomePorChave(chavePix);
        if (nomeReal == null) {
            throw new IllegalArgumentException("Nenhum nome encontrado para a chave Pix informada.");
        }
        return nomeReal;
    }

    // Comparação "bonitinha": ignora acento, maiúscula/minúscula e espaços duplicados
    public boolean nomesSaoCompativeis(String nomeInformado, String nomeReal) {
        if (nomeInformado == null || nomeReal == null) return false;

        String n1 = normalizar(nomeInformado);
        String n2 = normalizar(nomeReal);

        return n1.equals(n2);
    }

    private String normalizar(String texto) {
        String lower = texto.toLowerCase().trim();
        String semAcento = Normalizer.normalize(lower, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return semAcento.replaceAll("\\s+", " ");
    }
}
