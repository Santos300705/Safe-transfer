package com.safetransfer.safertransfer.service;

import java.text.Normalizer;

import org.springframework.stereotype.Service;

import com.safetransfer.safertransfer.model.ChavePix;
import com.safetransfer.safertransfer.repository.ChavePixRepository;

@Service
public class ValidacaoPixService {

   
    private final ChavePixRepository chavePixRepository;

    public ValidacaoPixService(ChavePixRepository chavePixRepository) {
        this.chavePixRepository = chavePixRepository;
    }

    /** >>> NOME EXATO que o controller usa <<< */
    public String buscarNomePorChaveObrigatorio(String chavePix) {
        if (chavePix == null || chavePix.isBlank()) {
            throw new IllegalArgumentException("chavePix inválida");
        }
        ChavePix c = chavePixRepository.findByChaveIgnoreCase(chavePix.trim())
                .orElseThrow(() -> new IllegalArgumentException("Chave Pix não encontrada"));
        return c.getNomeReal();
    }

    /** >>> NOME EXATO que o controller usa <<< */
    public boolean nomesSaoCompativeis(String nomeInformado, String nomeReal) {
    if (nomeInformado == null || nomeReal == null) return false;

    String a = normalize(nomeInformado); // ex.: "gabriel silva"
    String b = normalize(nomeReal);      // ex.: "gabriel silva"

    // se forem idênticos após normalizar: válido
    if (a.equals(b)) return true;

    String[] ta = a.split(" ");
    String[] tb = b.split(" ");

    // 1) primeiro nome precisa bater forte
    String fa = ta[0];
    String fb = tb[0];
    if (similaridade(fa, fb) < 0.97) return false;

    // 2) se ambos tiverem sobrenome, ele também precisa bater forte
    if (ta.length > 1 && tb.length > 1) {
        String la = ta[ta.length - 1];
        String lb = tb[tb.length - 1];
        if (similaridade(la, lb) < 0.97) return false;
    }

    // 3) similaridade geral ainda precisa ser razoável
    return similaridade(a, b) >= 0.93;
}
    // ---- helpers ----
    private String normalize(String s) {
        String lower = s.toLowerCase().trim();
        String noAccent = Normalizer.normalize(lower, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return noAccent.replaceAll("[^a-z0-9\\s]", " ").replaceAll("\\s+", " ").trim();
    }

    private double similaridade(String s1, String s2) {
        int d = lev(s1, s2);
        int m = Math.max(s1.length(), s2.length());
        return (m == 0) ? 1.0 : 1.0 - ((double) d / m);
    }

    private int lev(String a, String b) {
        int n = a.length(), m = b.length();
        int[][] dp = new int[n + 1][m + 1];
        for (int i = 0; i <= n; i++) dp[i][0] = i;
        for (int j = 0; j <= m; j++) dp[0][j] = j;
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int cost = (a.charAt(i - 1) == b.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
            }
        }
        return dp[n][m];
    }
}