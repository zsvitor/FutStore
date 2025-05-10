package com.futstore.futstore.modelo;

public enum StatusPedido {
	AGUARDANDO_PAGAMENTO("Aguardando Pagamento"), PAGAMENTO_REJEITADO("Pagamento Rejeitado"), PAGO("Pago"),
	AGUARDANDO_RETIRADA("Aguardando Retirada"), EM_TRANSITO("Em Trânsito"), ENTREGUE("Entregue");

	private final String descricao;

	StatusPedido(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

}