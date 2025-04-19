package com.futstore.futstore.modelo;

import java.io.Serializable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Pagamento implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull(message = "A forma de pagamento deve ser selecionada")
	@Enumerated(EnumType.STRING)
	private FormaPagamento formaPagamento;

	@Pattern(regexp = "^\\d{16}$", message = "Número do cartão deve ter 16 dígitos")
	private String numeroCartao;

	@Size(min = 3, message = "O nome no cartão deve ter no mínimo 3 caracteres")
	private String nomeCartao;

	@Pattern(regexp = "^\\d{3,4}$", message = "CVV deve ter 3 ou 4 dígitos")
	private String codigoVerificador;

	@Pattern(regexp = "^(0[1-9]|1[0-2])/[0-9]{2}$", message = "Validade deve estar no formato MM/AA")
	private String validade;

	private Integer parcelas;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public FormaPagamento getFormaPagamento() {
		return formaPagamento;
	}

	public void setFormaPagamento(FormaPagamento formaPagamento) {
		this.formaPagamento = formaPagamento;
	}

	public String getNumeroCartao() {
		return numeroCartao;
	}

	public void setNumeroCartao(String numeroCartao) {
		this.numeroCartao = numeroCartao;
	}

	public String getNomeCartao() {
		return nomeCartao;
	}

	public void setNomeCartao(String nomeCartao) {
		this.nomeCartao = nomeCartao;
	}

	public String getCodigoVerificador() {
		return codigoVerificador;
	}

	public void setCodigoVerificador(String codigoVerificador) {
		this.codigoVerificador = codigoVerificador;
	}

	public String getValidade() {
		return validade;
	}

	public void setValidade(String validade) {
		this.validade = validade;
	}

	public Integer getParcelas() {
		return parcelas;
	}

	public void setParcelas(Integer parcelas) {
		this.parcelas = parcelas;
	}

	public boolean isCartaoValido() {
		if (formaPagamento != FormaPagamento.CARTAO) {
			return true;
		}
		return numeroCartao != null && !numeroCartao.isEmpty() && nomeCartao != null && !nomeCartao.isEmpty()
				&& codigoVerificador != null && !codigoVerificador.isEmpty() && validade != null && !validade.isEmpty()
				&& parcelas != null && parcelas > 0;
	}

}