package com.safetransfer.safertransfer.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
@CrossOrigin(origins = "*")
public class ValidacaoPixController {

    @Autowired
    private ValidacaoPixService validacaoPixService;

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // GET /api/pix/{chavePix} - opcional
    @GetMapping("/pix/{chavePix}")
    public Map<String, String> buscarNomePorChave(@PathVariable String chavePix) {
        try {
            String nomeReal = validacaoPixService.buscarNomePorChaveObrigatorio(chavePix);
            return Map.of("chavePix", chavePix, "nomeReal", nomeReal);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    // POST /api/validar-chave - SÓ chave → nomeReal
    @PostMapping("/validar-chave")
    public ValidacaoPixResponse validarChave(@Valid @RequestBody ValidacaoPixRequest req) {
        String chavePix = req.getChavePix();

        if (chavePix == null || chavePix.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "chavePix é obrigatória");
        }

        try {
            String nomeReal = validacaoPixService.buscarNomePorChaveObrigatorio(chavePix);

            return new ValidacaoPixResponse(
                    "OK",
                    "Nome obtido com sucesso.",
                    nomeReal
            );
        } catch (IllegalArgumentException e) {
            // aqui convertemos a IllegalArgumentException em 400, não 500
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    // POST /api/validar-pix - fluxo completo (se quiser manter)
    @PostMapping("/validar-pix")
    public ValidacaoPixResponse validar(@Valid @RequestBody ValidacaoPixRequest req) {

        String chavePix = req.getChavePix();

        if (chavePix == null || chavePix.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chave Pix obrigatória");
        }

        String nomeReal = validacaoPixService.buscarNomePorChaveObrigatorio(chavePix);

        boolean ok = validacaoPixService.nomesSaoCompativeis(req.getNomeInformado(), nomeReal);

        Usuario usuario = null;
        if (req.getUsuarioId() != null) {
            usuario = usuarioRepository.findById(req.getUsuarioId())
                    .orElseThrow(() -> new IllegalArgumentException("usuarioId não encontrado"));
        }

        Transacao t = new Transacao();
        t.setChavePix(chavePix);
        t.setNomeInformado(req.getNomeInformado());
        t.setNomeReal(nomeReal);
        t.setStatusValidacao(ok ? "VÁLIDO" : "DIVERGENTE");
        t.setUsuario(usuario);
        transacaoRepository.save(t);

        return new ValidacaoPixResponse(
                ok ? "VÁLIDO" : "DIVERGENTE",
                ok ? "✅ Nome validado com sucesso."
                   : "⚠️ Nome divergente! A conta vinculada a essa chave Pix não corresponde ao nome informado.",
                nomeReal
        );
    }
}
