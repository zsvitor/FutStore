package com.futstore.futstore.repository;

import com.futstore.futstore.modelo.Produto;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    Page<Produto> findByNomeContainingIgnoreCaseOrderByIdDesc(String nome, Pageable pageable);
    Page<Produto> findAllByOrderByIdDesc(Pageable pageable);
    
    @Query("SELECT p FROM Produto p LEFT JOIN FETCH p.imagens WHERE p.id = :id")
    Optional<Produto> findByIdWithImagens(@Param("id") Long id);
}