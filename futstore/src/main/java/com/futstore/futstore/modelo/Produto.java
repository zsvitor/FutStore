package com.futstore.futstore.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Produto implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotEmpty(message = "O nome do produto é obrigatório")
	@Size(max = 200, message = "O nome do produto deve ter no máximo 200 caracteres")
	private String nome;

	@NotNull(message = "A avaliação do produto é obrigatória")
	@DecimalMin(value = "0.5", message = "A avaliação mínima é 0.5")
	@DecimalMax(value = "5.0", message = "A avaliação máxima é 5.0")
	private Double avaliacao;

	@NotEmpty(message = "A descrição do produto é obrigatória")
	@Size(max = 2000, message = "A descrição deve ter no máximo 2000 caracteres")
	private String descricao;

	@NotNull(message = "O preço do produto é obrigatório")
	@DecimalMin(value = "0.01", message = "O preço deve ser maior que zero")
	private BigDecimal preco;

	@NotNull(message = "A quantidade em estoque é obrigatória")
	@Min(value = 0, message = "A quantidade em estoque não pode ser negativa")
	private Integer quantidadeEstoque;

	private boolean ativo = true;

	private String imagem;

	@OneToMany(mappedBy = "produto", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ProdutoImagem> imagens = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Double getAvaliacao() {
		return avaliacao;
	}

	public void setAvaliacao(Double avaliacao) {
		this.avaliacao = avaliacao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public BigDecimal getPreco() {
		return preco;
	}

	public void setPreco(BigDecimal preco) {
		this.preco = preco;
	}

	public Integer getQuantidadeEstoque() {
		return quantidadeEstoque;
	}

	public void setQuantidadeEstoque(Integer quantidadeEstoque) {
		this.quantidadeEstoque = quantidadeEstoque;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public String getImagem() {
		if (!imagens.isEmpty()) {
			for (ProdutoImagem img : imagens) {
				if (img.isPrincipal()) {
					return img.getCaminho();
				}
			}
			return imagens.get(0).getCaminho();
		}
		return imagem;
	}

	public void setImagem(String imagem) {
		this.imagem = imagem;
	}

	public List<ProdutoImagem> getImagens() {
		return imagens;
	}

	public void setImagens(List<ProdutoImagem> imagens) {
		this.imagens = imagens;
	}

	public void adicionarImagem(ProdutoImagem imagem) {
		imagens.add(imagem);
		imagem.setProduto(this);
	}

	public void removerImagem(ProdutoImagem imagem) {
		imagens.remove(imagem);
		imagem.setProduto(null);
	}

	public ProdutoImagem getImagemPrincipal() {
		for (ProdutoImagem img : imagens) {
			if (img.isPrincipal()) {
				return img;
			}
		}
		return imagens.isEmpty() ? null : imagens.get(0);
	}
	
}