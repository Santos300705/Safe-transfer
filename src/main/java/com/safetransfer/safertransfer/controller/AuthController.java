package com.safetransfer.safertransfer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // ------------------- REGISTRO -------------------
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Usuario usuario) {

        // Verifica se já existe usuário
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body("Já existe um usuário com esse email.");
        }

        // Salva no banco
        usuarioRepository.save(usuario);

        return ResponseEntity.ok("Usuário registrado com sucesso!");
    }

    // ------------------- LOGIN -------------------
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario req) {

        var usuarioOpt = usuarioRepository.findFirstByEmailIgnoreCase(req.getEmail());

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(401).body("Email não encontrado");
        }

        var usuario = usuarioOpt.get();

        if (!usuario.getSenha().equals(req.getSenha())) {
            return ResponseEntity.status(401).body("Senha incorreta");
        }

        return ResponseEntity.ok("Login autorizado!");
    }
}