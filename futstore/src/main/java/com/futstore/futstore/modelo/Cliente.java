package com.futstore.futstore.modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
public class Cliente {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Pattern(regexp = "^\\S+\\s+\\S+.*$", message = "O nome deve conter pelo menos duas palavras")
	@Size(min = 3, message = "O nome deve ter no mínimo 3 caracteres")
	private String nomeCompleto;

	@CPF(message = "CPF inválido")
	@NotEmpty(message = "O CPF deve ser informado")
	private String cpf;

	@NotNull(message = "A data de nascimento deve ser informada")
	@Past(message = "A data de nascimento deve ser no passado")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate dataNascimento;

	@NotNull(message = "O gênero deve ser informado")
	@Enumerated(EnumType.STRING)
	private Genero genero;

	@Email(message = "Email inválido")
	@NotEmpty(message = "O email deve ser informado")
	private String gmail;

	@NotEmpty(message = "A senha deve ser informada")
	@Size(min = 3, message = "A senha deve ter no mínimo 3 caracteres")
	private String senha;

	@OneToOne(cascade = CascadeType.ALL)
	@NotNull(message = "O endereço de faturamento deve ser informado")
	@Valid
	private Endereco enderecoFaturamento;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Endereco> enderecosEntrega = new ArrayList<>();

	private boolean ativo = true;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNomeCompleto() {
		return nomeCompleto;
	}

	public void setNomeCompleto(String nomeCompleto) {
		this.nomeCompleto = nomeCompleto;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public LocalDate getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(LocalDate dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public Genero getGenero() {
		return genero;
	}

	public void setGenero(Genero genero) {
		this.genero = genero;
	}

	public String getGmail() {
		return gmail;
	}

	public void setGmail(String gmail) {
		this.gmail = gmail;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public Endereco getEnderecoFaturamento() {
		return enderecoFaturamento;
	}

	public void setEnderecoFaturamento(Endereco enderecoFaturamento) {
		this.enderecoFaturamento = enderecoFaturamento;
	}

	public List<Endereco> getEnderecosEntrega() {
		return enderecosEntrega;
	}

	public void setEnderecosEntrega(List<Endereco> enderecosEntrega) {
		this.enderecosEntrega = enderecosEntrega;
	}

	public void addEnderecoEntrega(Endereco endereco) {
		enderecosEntrega.add(endereco);
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

}