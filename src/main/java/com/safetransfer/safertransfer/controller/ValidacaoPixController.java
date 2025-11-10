package com.safetransfer.safertransfer.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.safetransfer.safertransfer.dto.ValidacaoPixRequest;
import com.safetransfer.safertransfer.dto.ValidacaoPixResponse;
import com.safetransfer.safertransfer.model.Transacao;
import com.safetransfer.safertransfer.model.Usuario;
import com.safetransfer.safertransfer.repository.TransacaoRepository;
import com.safetransfer.safertransfer.repository.UsuarioRepository;
import com.safetransfer.safertransfer.service.ValidacaoPixService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // permite o acesso do front-end
public class ValidacaoPixController {

    @Autowired
    private ValidacaoPixService validacaoPixService;

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // ============================================
    // NOVO ENDPOINT: Buscar nome real pela chave Pix
    // ============================================
    @GetMapping("/pix/{chavePix}")
    public Map<String, String> buscarNomePorChave(@PathVariable String chavePix) {
        try {
            String nomeReal = validacaoPixService.buscarNomePorChaveObrigatorio(chavePix);
            return Map.of("chavePix", chavePix, "nomeReal", nomeReal);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    // ============================================
    // ENDPOINT PRINCIPAL: Validar transação Pix
    // ============================================
    @PostMapping("/validar-pix")
    public ValidacaoPixResponse validar(@Valid @RequestBody ValidacaoPixRequest req) {
        // Valor > 0 (defesa extra, além do Bean Validation)
        if (req.getValor() == null || req.getValor() <= 0) {
            throw new IllegalArgumentException("valor deve ser maior que zero");
        }

        // Busca nome real pela chave
        String nomeReal = validacaoPixService.buscarNomePorChaveObrigatorio(req.getChavePix());
        boolean ok = validacaoPixService.nomesSaoCompativeis(req.getNomeInformado(), nomeReal);

        // Carrega o usuário só se veio ID
        Usuario usuario = null;
        if (req.getUsuarioId() != null) {
            usuario = usuarioRepository.findById(req.getUsuarioId())
                    .orElseThrow(() -> new IllegalArgumentException("usuarioId não encontrado"));
        }

        // Persiste transação
        Transacao t = new Transacao();
        t.setChavePix(req.getChavePix());
        t.setNomeInformado(req.getNomeInformado());
        t.setNomeReal(nomeReal);
        t.setValor(req.getValor());
        t.setStatusValidacao(ok ? "VÁLIDO" : "DIVERGENTE");
        t.setUsuario(usuario); // pode ser null
        transacaoRepository.save(t);

        return new ValidacaoPixResponse(
                ok ? "VÁLIDO" : "DIVERGENTE",
                ok ? "✅ Nome validado com sucesso."
                   : "⚠️ Nome divergente! A conta vinculada a essa chave Pix não corresponde ao nome informado.",
                nomeReal
        );
    }
}