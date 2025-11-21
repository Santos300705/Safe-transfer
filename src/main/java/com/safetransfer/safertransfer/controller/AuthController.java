package com.safetransfer.safertransfer.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.safetransfer.safertransfer.dto.LoginRequest;
import com.safetransfer.safertransfer.dto.RegistroRequest;
import com.safetransfer.safertransfer.model.Usuario;
import com.safetransfer.safertransfer.repository.UsuarioRepository;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = { "https://front-lqki.onrender.com" })
public class AuthController {

    private final UsuarioRepository usuarioRepository;

    public AuthController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // ========= REGISTRO =========
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registrar(@RequestBody RegistroRequest req) {

        // validações simples
        if (req.getEmail() == null || req.getEmail().isBlank()
                || req.getSenha() == null || req.getSenha().isBlank()
                || req.getNomeCompleto() == null || req.getNomeCompleto().isBlank()) {

            Map<String, Object> erro = new LinkedHashMap<>();
            erro.put("status", "ERRO");
            erro.put("mensagem", "Nome, e-mail e senha são obrigatórios.");
            return ResponseEntity.badRequest().body(erro);
        }

        // verifica se já existe usuário com esse e-mail
        var existente = usuarioRepository.findFirstByEmailIgnoreCase(req.getEmail());
        if (existente.isPresent()) {
            Map<String, Object> erro = new LinkedHashMap<>();
            erro.put("status", "ERRO");
            erro.put("mensagem", "Já existe usuário cadastrado com esse e-mail.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
        }

        // cria usuário
        Usuario novo = new Usuario(
                req.getNomeCompleto(),
                req.getEmail(),
                req.getSenha() // sem hash mesmo, só pro projeto
        );
        Usuario salvo = usuarioRepository.save(novo);

        // monta resposta JSON organizada
        Map<String, Object> usuarioResumo = new LinkedHashMap<>();
        usuarioResumo.put("id", salvo.getId());
        usuarioResumo.put("nome", salvo.getNomeCompleto());
        usuarioResumo.put("email", salvo.getEmail());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", "SUCESSO");
        body.put("mensagem", "Usuário registrado com sucesso!");
        body.put("usuario", usuarioResumo);

        // 201 Created (bonitinho pra REST)
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    // ========= LOGIN =========
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest req) {

        if (req.getEmail() == null || req.getEmail().isBlank()
                || req.getSenha() == null || req.getSenha().isBlank()) {

            Map<String, Object> erro = new LinkedHashMap<>();
            erro.put("status", "ERRO");
            erro.put("mensagem", "E-mail e senha são obrigatórios.");
            return ResponseEntity.badRequest().body(erro);
        }

        var usuarioOpt = usuarioRepository.findFirstByEmailIgnoreCase(req.getEmail());
        if (usuarioOpt.isEmpty()) {
            Map<String, Object> erro = new LinkedHashMap<>();
            erro.put("status", "ERRO");
            erro.put("mensagem", "E-mail ou senha inválidos.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(erro);
        }

        var usuario = usuarioOpt.get();

        // aqui só compara senha em texto puro mesmo
        if (!usuario.getSenha().equals(req.getSenha())) {
            Map<String, Object> erro = new LinkedHashMap<>();
            erro.put("status", "ERRO");
            erro.put("mensagem", "E-mail ou senha inválidos.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(erro);
        }

        // JSON de sucesso
        Map<String, Object> usuarioResumo = new LinkedHashMap<>();
        usuarioResumo.put("id", usuario.getId());
        usuarioResumo.put("nome", usuario.getNomeCompleto());
        usuarioResumo.put("email", usuario.getEmail());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", "SUCESSO");
        body.put("mensagem", "Login realizado com sucesso.");
    
        return ResponseEntity.ok(body);
    }
}