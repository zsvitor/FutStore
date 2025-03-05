package com.futstore.futstore.repository;

import com.futstore.futstore.modelo.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    Page<Produto> findByNomeContainingIgnoreCaseOrderByIdDesc(String nome, Pageable pageable);
    Page<Produto> findAllByOrderByIdDesc(Pageable pageable);
}