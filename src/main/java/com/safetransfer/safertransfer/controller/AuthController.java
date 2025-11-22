package com.safetransfer.safertransfer.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.safetransfer.safertransfer.model.Usuario;
import com.safetransfer.safertransfer.repository.UsuarioRepository;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = { "https://front-lqki.onrender.com" })
public class AuthController {

    private final UsuarioRepository usuarioRepository;

    public AuthController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // ====== CADASTRO ======
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {

        String nomeCompleto = body.get("nomeCompleto");
        String email = body.get("email");
        String telefone = body.get("telefone"); // se não tiver campo no entity, pode ignorar
        String senha = body.get("senha");

        if (nomeCompleto == null || nomeCompleto.isBlank()
                || email == null || email.isBlank()
                || senha == null || senha.isBlank()) {

            Map<String, String> erro = new HashMap<>();
            erro.put("mensagem", "Nome, e-mail e senha são obrigatórios.");
            return ResponseEntity.badRequest().body(erro);
        }

        // já existe e-mail?
        Optional<Usuario> existente = usuarioRepository.findFirstByEmailIgnoreCase(email);
        if (existente.isPresent()) {
            Map<String, String> erro = new HashMap<>();
            erro.put("mensagem", "Já existe um usuário cadastrado com esse e-mail.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
        }

        // cria usuário novo
        Usuario novo = new Usuario();
        novo.setNomeCompleto(nomeCompleto);
        novo.setEmail(email);
        // se o entity tiver campo telefone, descomenta:
        // novo.setTelefone(telefone);
        novo.setSenha(senha);

        usuarioRepository.save(novo);

        Map<String, Object> resp = new HashMap<>();
        resp.put("mensagem", "Usuário registrado com sucesso.");
        resp.put("email", email);

        // 201 Created
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    // ====== LOGIN ======
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {

        String email = body.get("email");
        String senha = body.get("senha");

        if (email == null || email.isBlank()
                || senha == null || senha.isBlank()) {

            Map<String, String> erro = new HashMap<>();
            erro.put("mensagem", "E-mail e senha são obrigatórios.");
            return ResponseEntity.badRequest().body(erro);
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findFirstByEmailIgnoreCase(email);
        if (usuarioOpt.isEmpty()) {
            Map<String, String> erro = new HashMap<>();
            erro.put("mensagem", "Usuário não encontrado.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(erro);
        }

        Usuario usuario = usuarioOpt.get();
        if (!senha.equals(usuario.getSenha())) {
            Map<String, String> erro = new HashMap<>();
            erro.put("mensagem", "Credenciais inválidas.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(erro);
        }

        Map<String, Object> resp = new HashMap<>();
        resp.put("mensagem", "Login realizado com sucesso.");
        resp.put("nome", usuario.getNomeCompleto());
        resp.put("email", usuario.getEmail());

        return ResponseEntity.ok(resp);
    }
}