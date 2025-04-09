package com.futstore.futstore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.futstore.futstore.modelo.Cliente;
import com.futstore.futstore.modelo.Endereco;
import com.futstore.futstore.repository.ClienteRepository;

@Service
public class ClienteService {

	@Autowired
	private ClienteRepository clienteRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Transactional
	public Cliente salvar(Cliente cliente) {
		cliente.setSenha(passwordEncoder.encode(cliente.getSenha()));
		return clienteRepository.save(cliente);
	}

	public void copiarEndereco(Endereco origem, Endereco destino) {
	    destino.setCep(origem.getCep());
	    destino.setLogradouro(origem.getLogradouro());
	    destino.setNumero(origem.getNumero());
	    destino.setComplemento(origem.getComplemento());
	    destino.setBairro(origem.getBairro());
	    destino.setCidade(origem.getCidade());
	    destino.setUf(origem.getUf());
	}

	public boolean autenticar(String gmail, String senha) {
		return clienteRepository.findByGmail(gmail)
				.map(cliente -> cliente.isAtivo() && passwordEncoder.matches(senha, cliente.getSenha())).orElse(false);
	}

	public boolean validarSenha(String gmail, String senha) {
		return clienteRepository.findByGmail(gmail).map(cliente -> passwordEncoder.matches(senha, cliente.getSenha()))
				.orElse(false);
	}

	public Cliente buscarPorGmail(String gmail) {
		return clienteRepository.findByGmail(gmail).orElse(null);
	}

	public Cliente buscarPorId(Long id) {
		return clienteRepository.findById(id).orElse(null);
	}

	@Transactional
	public Cliente atualizar(Cliente cliente) {
		return clienteRepository.save(cliente);
	}

	@Transactional
	public void atualizarSenha(Cliente cliente, String novaSenha) {
		cliente.setSenha(passwordEncoder.encode(novaSenha));
		clienteRepository.save(cliente);
	}

	@Transactional
	public void adicionarEnderecoEntrega(Cliente cliente, Endereco novoEndereco) {
		if (novoEndereco.isPrincipal()) {
			for (Endereco endereco : cliente.getEnderecosEntrega()) {
				endereco.setPrincipal(false);
			}
		} else if (cliente.getEnderecosEntrega().isEmpty()) {
			novoEndereco.setPrincipal(true);
		}
		cliente.addEnderecoEntrega(novoEndereco);
		clienteRepository.save(cliente);
	}

	@Transactional
	public void definirEnderecoPadrao(Cliente cliente, Long enderecoId) {
		boolean enderecoEncontrado = false;
		for (Endereco endereco : cliente.getEnderecosEntrega()) {
			boolean isPrincipal = endereco.getId().equals(enderecoId);
			endereco.setPrincipal(isPrincipal);
			if (isPrincipal) {
				enderecoEncontrado = true;
			}
		}
		if (enderecoEncontrado) {
			clienteRepository.save(cliente);
		}
	}

}