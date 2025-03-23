package com.futstore.futstore.modelo;

import java.io.Serializable;
import java.math.BigDecimal;

public class ItemCarrinho implements Serializable {

	private static final long serialVersionUID = 1L;

	private Produto produto;
	private int quantidade;

	public ItemCarrinho(Produto produto, int quantidade) {
		this.produto = produto;
		this.quantidade = quantidade;
	}

	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	public int getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(int quantidade) {
		this.quantidade = quantidade;
	}

	public BigDecimal getSubtotal() {
		return produto.getPreco().multiply(BigDecimal.valueOf(quantidade));
	}

}