package com.safetransfer.safertransfer.repository;


import com.safetransfer.safertransfer.model.Transacao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {}