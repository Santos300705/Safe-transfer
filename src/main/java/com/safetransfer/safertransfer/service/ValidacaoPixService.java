package com.safetransfer.safertransfer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.safetransfer.safertransfer.model.Usuario;
import com.safetransfer.safertransfer.repository.UsuarioRepository;

import java.text.Normalizer;

@Service
public class ValidacaoPixService {

    private static final double LIMIAR = 0.85;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Busca nome real a partir do email (que estamos usando como chave Pix)
    public String buscarNomePorChave(String chavePix) {
        if (chavePix == null) return null;

        String normalizada = chavePix.trim();

        return usuarioRepository.findByChavePix(normalizada)
                .map(Usuario::getNomeCompleto)
                .orElse(null);
    }

    public String buscarNomePorChaveObrigatorio(String chavePix) {
        String nomeReal = buscarNomePorChave(chavePix);
        if (nomeReal == null) {
            throw new IllegalArgumentException("Nenhum nome encontrado para a chave Pix informada.");
        }
        return nomeReal;
    }

    public boolean nomesSaoIguais(String nomeInformado, String nomeReal) {
        if (nomeInformado == null || nomeReal == null) return false;

        String n1 = normalizar(nomeInformado);
        String n2 = normalizar(nomeReal);

        double similaridade = calcularSimilaridade(n1, n2);
        return similaridade >= LIMIAR;
    }

    public boolean nomesSaoCompativeis(String nomeInformado, String nomeReal) {
        return nomesSaoIguais(nomeInformado, nomeReal);
    }

    private String normalizar(String texto) {
        String lower = texto.toLowerCase().trim();
        String semAcento = Normalizer.normalize(lower, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return semAcento.replaceAll("[^a-z0-9\\s]", "").replaceAll("\\s+", " ").trim();
    }

    public double calcularSimilaridade(String s1, String s2) {
        int dist = levenshtein(s1, s2);
        int maior = Math.max(s1.length(), s2.length());
        if (maior == 0) return 1.0;
        return 1.0 - ((double) dist / maior);
    }

    private int levenshtein(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++)
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) dp[i][j] = j;
                else if (j == 0) dp[i][j] = i;
                else {
                    int custo = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                    dp[i][j] = Math.min(
                            Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                            dp[i - 1][j - 1] + custo
                    );
                }
            }
        return dp[s1.length()][s2.length()];
    }
}
