package com.cc.be.repository;

import com.cc.be.model.ArchivoEvaluador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArchivoEvaluadorRepository extends JpaRepository<ArchivoEvaluador, Long> {
    Optional<ArchivoEvaluador> findByEvaluadorId(Long evaluadorId);
    void deleteByEvaluadorId(Long evaluadorId);
}
