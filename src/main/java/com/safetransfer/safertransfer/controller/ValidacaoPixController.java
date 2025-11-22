package com.safetransfer.safertransfer.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.safetransfer.safertransfer.dto.ValidacaoPixRequest;
import com.safetransfer.safertransfer.dto.ValidacaoPixResponse;
import com.safetransfer.safertransfer.repository.UsuarioRepository;



@RestController
@RequestMapping("/api")
@CrossOrigin(origins = { "https://front-lqki.onrender.com" })
public class ValidacaoPixController {

    // --------------------------------------------------------------------
    // CLASSE INTERNA DE LOG (NÃO PRECISA DE OUTRO ARQUIVO)
    // --------------------------------------------------------------------
    public static class PixLogResponse {
        private Long id;
        private String chavePix;
        private String nomeInformado;
        private String status;
        private LocalDateTime dataHora;

        public PixLogResponse() {
        }

        public PixLogResponse(
                Long id,
                String chavePix,
                String nomeInformado,
                String status,
                LocalDateTime dataHora) {
            this.id = id;
            this.chavePix = chavePix;
            this.nomeInformado = nomeInformado;
            this.status = status;
            this.dataHora = dataHora;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getChavePix() {
            return chavePix;
        }

        public void setChavePix(String chavePix) {
            this.chavePix = chavePix;
        }

        public String getNomeInformado() {
            return nomeInformado;
        }

        public void setNomeInformado(String nomeInformado) {
            this.nomeInformado = nomeInformado;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public LocalDateTime getDataHora() {
            return dataHora;
        }

        public void setDataHora(LocalDateTime dataHora) {
            this.dataHora = dataHora;
        }
    }

    // --------------------------------------------------------------------
    // CAMPOS DO CONTROLLER
    // --------------------------------------------------------------------
    private final UsuarioRepository usuarioRepository;

    // "banco" em memória para os logs de validação
    private final List<PixLogResponse> logs = new CopyOnWriteArrayList<>();
    private final AtomicLong logSequence = new AtomicLong(1L);

    public ValidacaoPixController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // --------------------------------------------------------------------
    // ENDPOINT QUE O FRONT USA: /api/validar-pix
    // --------------------------------------------------------------------
    @PostMapping("/validar-pix")
    public ResponseEntity<ValidacaoPixResponse> validar(@Valid @RequestBody ValidacaoPixRequest req) {

        String chavePix = req.getChavePix();
        String nomeInformado = req.getNomeInformado();

        if (chavePix == null || chavePix.isBlank()) {
            registrarLog(chavePix, nomeInformado, "ERRO");

            return ResponseEntity
                    .badRequest()
                    .body(new ValidacaoPixResponse(
                            "ERRO",
                            null,
                            "A chave Pix é obrigatória."
                    ));
        }

        var usuarioOpt = usuarioRepository.findFirstByEmailIgnoreCase(chavePix);

        if (usuarioOpt.isEmpty()) {
            registrarLog(chavePix, nomeInformado, "NAO_ENCONTRADO");

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

        registrarLog(chavePix, nomeInformado, status);

        return ResponseEntity.ok(
                new ValidacaoPixResponse(status, nomeReal, mensagem)
        );
    }

    // --------------------------------------------------------------------
    // ALIAS PARA O POSTMAN: /api/pix/verify  (mesma lógica do validar-pix)
    // --------------------------------------------------------------------
    @PostMapping("/pix/verify")
    public ResponseEntity<ValidacaoPixResponse> verificarPix(@Valid @RequestBody ValidacaoPixRequest req) {
        return validar(req);
    }

    // --------------------------------------------------------------------
    // LISTAR TODOS OS LOGS: GET /api/pix/logs
    // --------------------------------------------------------------------
    @GetMapping("/pix/logs")
    public ResponseEntity<List<PixLogResponse>> listarLogs() {
        return ResponseEntity.ok(logs);
    }

    // --------------------------------------------------------------------
    // BUSCAR LOG POR ID: GET /api/pix/logs/{id}
    // --------------------------------------------------------------------
    @GetMapping("/pix/logs/{id}")
    public ResponseEntity<PixLogResponse> buscarLogPorId(@PathVariable Long id) {
        Optional<PixLogResponse> encontrado = logs.stream()
                .filter(log -> log.getId().equals(id))
                .findFirst();

        return encontrado
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // --------------------------------------------------------------------
    // MÉTODO PRIVADO PARA REGISTRAR LOG EM MEMÓRIA
    // --------------------------------------------------------------------
    private void registrarLog(String chavePix, String nomeInformado, String status) {
        Long id = logSequence.getAndIncrement();
        PixLogResponse log = new PixLogResponse(
                id,
                chavePix,
                nomeInformado,
                status,
                LocalDateTime.now()
        );
        logs.add(log);
    }
}