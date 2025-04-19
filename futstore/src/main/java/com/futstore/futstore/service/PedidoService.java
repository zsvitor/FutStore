package com.futstore.futstore.service;

import com.futstore.futstore.modelo.*;
import com.futstore.futstore.repository.PagamentoRepository;
import com.futstore.futstore.repository.PedidoRepository;
import com.futstore.futstore.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoService {

	@Autowired
	private PedidoRepository pedidoRepository;

	@Autowired
	private PagamentoRepository pagamentoRepository;

	@Autowired
	private ProdutoRepository produtoRepository;

	@Transactional
	public Long finalizarPedido(Cliente cliente, Carrinho carrinho, Pagamento pagamento) {
		if (carrinho == null || carrinho.isEmpty()) {
			throw new RuntimeException("Carrinho vazio");
		}
		verificarEstoqueProdutos(carrinho);
		Pagamento pagamentoSalvo = pagamentoRepository.save(pagamento);
		Pedido pedido = new Pedido();
		pedido.setCliente(cliente);
		pedido.setDataPedido(LocalDateTime.now());
		pedido.setStatus(StatusPedido.AGUARDANDO_PAGAMENTO);
		pedido.setValorTotal(carrinho.getTotal());
		pedido.setValorFrete(carrinho.getValorFrete());
		pedido.setPagamento(pagamentoSalvo);
		Endereco enderecoEntrega = cliente.getEnderecosEntrega().stream().filter(Endereco::isPrincipal).findFirst()
				.orElseThrow(() -> new RuntimeException("Endereço de entrega não encontrado"));
		pedido.setEnderecoEntrega(enderecoEntrega);
		List<ItemPedido> itensPedido = new ArrayList<>();
		for (ItemCarrinho itemCarrinho : carrinho.getItens()) {
			ItemPedido itemPedido = new ItemPedido();
			itemPedido.setPedido(pedido);
			itemPedido.setProduto(itemCarrinho.getProduto());
			itemPedido.setQuantidade(itemCarrinho.getQuantidade());
			itemPedido.setPrecoUnitario(itemCarrinho.getProduto().getPreco());
			itemPedido.setSubtotal(itemCarrinho.getSubtotal());
			itensPedido.add(itemPedido);
			atualizarEstoque(itemCarrinho.getProduto().getId(), itemCarrinho.getQuantidade());
		}
		pedido.setItens(itensPedido);
		Pedido pedidoSalvo = pedidoRepository.save(pedido);
		return pedidoSalvo.getId();
	}

	private void verificarEstoqueProdutos(Carrinho carrinho) {
		for (ItemCarrinho item : carrinho.getItens()) {
			Produto produto = produtoRepository.findById(item.getProduto().getId())
					.orElseThrow(() -> new RuntimeException("Produto não encontrado: " + item.getProduto().getId()));
			if (produto.getQuantidadeEstoque() < item.getQuantidade()) {
				throw new RuntimeException("Produto " + produto.getNome() + " não possui estoque suficiente");
			}
		}
	}

	@Transactional
	private void atualizarEstoque(Long produtoId, int quantidade) {
		Produto produto = produtoRepository.findById(produtoId)
				.orElseThrow(() -> new RuntimeException("Produto não encontrado"));
		produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - quantidade);
		produtoRepository.save(produto);
	}

}