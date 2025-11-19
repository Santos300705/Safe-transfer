package com.safetransfer.safertransfer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.safetransfer.safertransfer.dto.ValidacaoPixRequest;
import com.safetransfer.safertransfer.dto.ValidacaoPixResponse;
import com.safetransfer.safertransfer.repository.UsuarioRepository;

@RestController
@RequestMapping("/api")
// pode deixar o CrossOrigin aqui ou tirar e usar só a GlobalCorsConfig
@CrossOrigin(origins = "https://front-lqki.onrender.com")
public class ValidacaoPixController {

    private final UsuarioRepository usuarioRepository;

    public ValidacaoPixController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // *** NOVO: responde ao preflight (OPTIONS) ***
    @RequestMapping(value = "/validar-pix", method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> validarPixOptions() {
        // só precisa responder 200 OK
        return ResponseEntity.ok().build();
    }

    // POST normal da validação
    @PostMapping("/validar-pix")
    public ResponseEntity<ValidacaoPixResponse> validar(@Valid @RequestBody ValidacaoPixRequest req) {

        String chavePix = req.getChavePix();
        String nomeInformado = req.getNomeInformado();

        if (chavePix == null || chavePix.isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .body(new ValidacaoPixResponse(
                            "ERRO",
                            null,
                            "A chave Pix é obrigatória."
                    ));
        }

        // aqui eu assumo que a chave Pix é o e-mail cadastrado
        var usuarioOpt = usuarioRepository.findFirstByEmailIgnoreCase(chavePix);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.ok(
                    new ValidacaoPixResponse(
                            "NAO_ENCONTRADO",
                            null,
                            "Nenhum nome encontrado para a chave Pix informada."
                    )
            );
        }

        var usuario = usuarioOpt.get();
        String nomeReal = usuario.getNomeCompleto();

        String status;
        String mensagem;

        if (nomeInformado == null || nomeInformado.isBlank()) {
            status = "ENCONTRADO";
            mensagem = "Chave localizada. Nome real exibido abaixo.";
        } else if (nomeInformado.trim().equalsIgnoreCase(nomeReal.trim())) {
            status = "VALIDO";
            mensagem = "Nome informado confere com o nome real da chave Pix.";
        } else {
            status = "DIVERGENTE";
            mensagem = "Atenção: o nome informado é diferente do nome real da chave Pix.";
        }

        return ResponseEntity.ok(
                new ValidacaoPixResponse(status, nomeReal, mensagem)
        );
    }
}