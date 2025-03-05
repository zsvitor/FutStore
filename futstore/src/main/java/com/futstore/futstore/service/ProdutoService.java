package com.futstore.futstore.service;

import com.futstore.futstore.modelo.Produto;
import com.futstore.futstore.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    public Produto salvar(Produto produto) {
        return produtoRepository.save(produto);
    }

    public Page<Produto> listarProdutos(int pagina, String filtroNome) {
        PageRequest pageRequest = PageRequest.of(pagina, 10);      
        if (filtroNome != null && !filtroNome.isEmpty()) {
            return produtoRepository.findByNomeContainingIgnoreCaseOrderByIdDesc(filtroNome, pageRequest);
        }        
        return produtoRepository.findAllByOrderByIdDesc(pageRequest);
    }

    public Produto atualizar(Produto produto, Long id) {
        Produto produtoBanco = produtoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Produto inválido: " + id));       
        produtoBanco.setNome(produto.getNome());
        produtoBanco.setDescricao(produto.getDescricao());
        produtoBanco.setPreco(produto.getPreco());
        produtoBanco.setQuantidadeEstoque(produto.getQuantidadeEstoque());
        produtoBanco.setAvaliacao(produto.getAvaliacao());        
        return produtoRepository.save(produtoBanco);
    }

    public void toggleStatus(Long id) {
        Produto produto = produtoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Produto inválido: " + id));        
        produto.setAtivo(!produto.isAtivo());
        produtoRepository.save(produto);
    }
    
}