package com.futstore.futstore.modelo;

public enum FormaPagamento {
	BOLETO("Boleto"), CARTAO("Cartão de Crédito");

	private final String descricao;

	FormaPagamento(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

}