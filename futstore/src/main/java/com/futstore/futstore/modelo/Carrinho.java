package com.futstore.futstore.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Carrinho implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<ItemCarrinho> itens = new ArrayList<>();

	private BigDecimal valorFrete = BigDecimal.ZERO;

	public List<ItemCarrinho> getItens() {
		return itens;
	}

	public void adicionarItem(Produto produto, int quantidade) {
		Optional<ItemCarrinho> itemExistente = itens.stream()
				.filter(item -> item.getProduto().getId().equals(produto.getId())).findFirst();
		if (itemExistente.isPresent()) {
			itemExistente.get().setQuantidade(itemExistente.get().getQuantidade() + quantidade);
		} else {
			itens.add(new ItemCarrinho(produto, quantidade));
		}
	}

	public void atualizarQuantidade(Long produtoId, int quantidade) {
		itens.stream().filter(item -> item.getProduto().getId().equals(produtoId)).findFirst().ifPresent(item -> {
			if (quantidade <= 0) {
				removerItem(produtoId);
			} else {
				item.setQuantidade(quantidade);
			}
		});
	}

	public void removerItem(Long produtoId) {
		itens.removeIf(item -> item.getProduto().getId().equals(produtoId));
	}

	public void limpar() {
		itens.clear();
		valorFrete = BigDecimal.ZERO;
	}

	public BigDecimal getSubtotal() {
		return itens.stream().map(ItemCarrinho::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	public BigDecimal getValorFrete() {
		return valorFrete;
	}

	public void setValorFrete(BigDecimal valorFrete) {
		this.valorFrete = valorFrete;
	}

	public BigDecimal getTotal() {
		return getSubtotal().add(valorFrete);
	}

	public int getQuantidadeTotal() {
		return itens.stream().mapToInt(ItemCarrinho::getQuantidade).sum();
	}

	public boolean isEmpty() {
		return itens.isEmpty();
	}

}