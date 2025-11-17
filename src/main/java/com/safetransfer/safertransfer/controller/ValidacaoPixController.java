package com.safetransfer.safertransfer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.safetransfer.safertransfer.dto.ValidacaoPixRequest;
import com.safetransfer.safertransfer.dto.ValidacaoPixResponse;
import com.safetransfer.safertransfer.service.ValidacaoPixService;



@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ValidacaoPixController {

    @Autowired
    private ValidacaoPixService validacaoPixService;

    @PostMapping("/validar-pix")
    public ResponseEntity<ValidacaoPixResponse> validar(@Valid @RequestBody ValidacaoPixRequest req) {

        String chavePix = req.getChavePix();
        String nomeInformado = req.getNomeInformado();

        if (chavePix == null || chavePix.isBlank()) {
            return ResponseEntity.badRequest().body(
                    new ValidacaoPixResponse("ERRO", "Chave Pix é obrigatória.", null)
            );
        }

        if (nomeInformado == null || nomeInformado.isBlank()) {
            return ResponseEntity.badRequest().body(
                    new ValidacaoPixResponse("ERRO", "Nome informado é obrigatório.", null)
            );
        }

        try {
            // 1) Busca o nome real no banco a partir da chave Pix
            String nomeReal = validacaoPixService.buscarNomePorChaveObrigatorio(chavePix);

            // 2) Compara nome informado x nome real
            boolean ok = validacaoPixService.nomesSaoCompativeis(nomeInformado, nomeReal);

            // 3) Monta resposta
            ValidacaoPixResponse resp = new ValidacaoPixResponse(
                    ok ? "VÁLIDO" : "DIVERGENTE",
                    ok ? "Nome corresponde ao titular da chave Pix."
                       : "Nome NÃO corresponde ao titular da chave Pix.",
                    nomeReal
            );

            return ResponseEntity.ok(resp);

        } catch (IllegalArgumentException e) {
            // chave Pix não encontrada → 400 com mensagem
            ValidacaoPixResponse erro = new ValidacaoPixResponse(
                    "ERRO",
                    e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
        }
    }
}
