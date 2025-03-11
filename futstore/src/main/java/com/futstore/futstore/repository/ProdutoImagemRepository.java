package com.futstore.futstore.repository;

import com.futstore.futstore.modelo.ProdutoImagem;
import com.futstore.futstore.modelo.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProdutoImagemRepository extends JpaRepository<ProdutoImagem, Long> {
	List<ProdutoImagem> findByProdutoOrderById(Produto produto);
	ProdutoImagem findByProdutoAndPrincipal(Produto produto, boolean principal);
}