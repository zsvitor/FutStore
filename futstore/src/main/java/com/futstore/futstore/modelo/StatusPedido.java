package com.futstore.futstore.modelo;

public enum StatusPedido {
	AGUARDANDO_PAGAMENTO("Aguardando Pagamento"), PAGAMENTO_APROVADO("Pagamento Aprovado"),
	EM_SEPARACAO("Em Separação"), ENVIADO("Enviado"), ENTREGUE("Entregue"), CANCELADO("Cancelado");

	private final String descricao;

	StatusPedido(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

}