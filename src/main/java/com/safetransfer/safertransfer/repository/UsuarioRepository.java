package com.safetransfer.safertransfer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.safetransfer.safertransfer.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // estamos usando email como chave Pix
    Optional<Usuario> findFirstByEmailIgnoreCase(String email);
}
