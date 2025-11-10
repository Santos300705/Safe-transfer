package com.safetransfer.safertransfer.repository;




import com.safetransfer.safertransfer.model.ChavePix;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChavePixRepository extends JpaRepository<ChavePix, Long> {
    Optional<ChavePix> findByChaveIgnoreCase(String chave);
}