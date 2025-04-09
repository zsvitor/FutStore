package com.futstore.futstore.controller;

import java.time.LocalDate;
import java.util.Arrays;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.futstore.futstore.modelo.Cliente;
import com.futstore.futstore.modelo.Endereco;
import com.futstore.futstore.modelo.Genero;
import com.futstore.futstore.repository.ClienteRepository;
import com.futstore.futstore.service.ClienteService;

@Controller
@RequestMapping("/cliente")
public class ClienteController {

	@Autowired
	private ClienteService clienteService;

	@Autowired
	private ClienteRepository clienteRepository;

	@GetMapping("/cadastro")
	public String mostrarFormularioCadastro(Model model) {
		Cliente cliente = new Cliente();
		cliente.setEnderecoFaturamento(new Endereco());
		model.addAttribute("cliente", cliente);
		model.addAttribute("generos", Arrays.asList(Genero.values()));
		return "cliente/cadastro";
	}

	@PostMapping("/cadastro")
	public String cadastrarCliente(@Valid @ModelAttribute("cliente") Cliente cliente, BindingResult result, Model model,
			RedirectAttributes redirectAttributes,
			@RequestParam(value = "copiarEnderecoFaturamento", required = false) boolean copiarEnderecoFaturamento,
			@RequestParam(value = "confirmarSenha", required = true) String confirmarSenha) {
		if (clienteRepository.existsByCpf(cliente.getCpf())) {
			result.rejectValue("cpf", "cpf.existente", "CPF já cadastrado.");
		}
		if (clienteRepository.existsByGmail(cliente.getGmail())) {
			result.rejectValue("gmail", "gmail.existente", "Email já cadastrado.");
		}
		if (!cliente.getSenha().equals(confirmarSenha)) {
			result.reject("senha.naoConfere", "As senhas não correspondem.");
		}
		if (result.hasErrors()) {
			model.addAttribute("generos", Arrays.asList(Genero.values()));
			return "cliente/cadastro";
		}
		if (copiarEnderecoFaturamento) {
			Endereco enderecoEntrega = new Endereco();
			clienteService.copiarEndereco(cliente.getEnderecoFaturamento(), enderecoEntrega);
			enderecoEntrega.setDescricao("Principal");
			enderecoEntrega.setPrincipal(true);
			cliente.addEnderecoEntrega(enderecoEntrega);
		}
		clienteService.salvar(cliente);
		redirectAttributes.addFlashAttribute("mensagemSucesso",
				"Cadastro realizado com sucesso! Faça login para continuar.");
		return "redirect:/cliente/login";
	}

	@GetMapping("/login")
	public String mostrarLoginForm(@RequestParam(required = false) String error, Model model, HttpSession session) {
		if (session.getAttribute("clienteLogado") != null) {
			return "redirect:/";
		}
		if (error != null) {
			model.addAttribute("loginError", "Email ou senha inválidos");
		}
		return "cliente/login";
	}

	@PostMapping("/autenticar")
	public String autenticarCliente(@RequestParam String gmail, @RequestParam String senha, HttpSession session,
			RedirectAttributes redirectAttributes) {
		if (clienteService.autenticar(gmail, senha)) {
			Cliente cliente = clienteService.buscarPorGmail(gmail);
			session.setAttribute("clienteLogado", cliente);
			String redirectUrl = (String) session.getAttribute("redirectUrl");
			if (redirectUrl != null) {
				session.removeAttribute("redirectUrl");
				return "redirect:" + redirectUrl;
			}
			return "redirect:/";
		} else {
			redirectAttributes.addFlashAttribute("loginError", "Email ou senha inválidos");
			return "redirect:/cliente/login?error=true";
		}
	}

	@GetMapping("/logout")
	public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
		session.removeAttribute("clienteLogado");
		redirectAttributes.addFlashAttribute("mensagemSucesso", "Logout realizado com sucesso!");
		return "redirect:/";
	}

	@GetMapping("/perfil")
	public String mostrarPerfil(Model model, HttpSession session) {
		Cliente cliente = (Cliente) session.getAttribute("clienteLogado");
		if (cliente == null) {
			session.setAttribute("redirectUrl", "/cliente/perfil");
			return "redirect:/cliente/login";
		}
		cliente = clienteService.buscarPorId(cliente.getId());
		session.setAttribute("clienteLogado", cliente);
		model.addAttribute("cliente", cliente);
		model.addAttribute("novoEndereco", new Endereco());
		model.addAttribute("generos", Arrays.asList(Genero.values()));
		return "cliente/perfil";
	}

	@PostMapping("/atualizar-dados")
	public String atualizarDadosPessoais(@RequestParam("nomeCompleto") String nomeCompleto,
			@RequestParam("dataNascimento") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataNascimento,
			@RequestParam("genero") Genero genero, HttpSession session, RedirectAttributes redirectAttributes) {
		Cliente clienteLogado = (Cliente) session.getAttribute("clienteLogado");
		if (clienteLogado != null) {
			Cliente cliente = clienteService.buscarPorId(clienteLogado.getId());
			if (cliente != null) {
				cliente.setNomeCompleto(nomeCompleto);
				cliente.setDataNascimento(dataNascimento);
				cliente.setGenero(genero);
				clienteRepository.save(cliente);
				clienteLogado.setNomeCompleto(nomeCompleto);
				clienteLogado.setDataNascimento(dataNascimento);
				clienteLogado.setGenero(genero);
				redirectAttributes.addFlashAttribute("mensagemSucesso", "Dados pessoais atualizados com sucesso!");
			} else {
				redirectAttributes.addFlashAttribute("mensagemErro", "Cliente não encontrado.");
			}
		} else {
			return "redirect:/cliente/login";
		}
		return "redirect:/cliente/perfil";
	}

	@PostMapping("/atualizar-senha")
	public String atualizarSenha(HttpSession session, @RequestParam String senhaAtual, @RequestParam String novaSenha,
			@RequestParam String confirmarNovaSenha, RedirectAttributes redirectAttributes) {
		Cliente clienteLogado = (Cliente) session.getAttribute("clienteLogado");
		if (!clienteService.validarSenha(clienteLogado.getGmail(), senhaAtual)) {
			redirectAttributes.addFlashAttribute("mensagemErro", "A senha atual está incorreta.");
			return "redirect:/cliente/perfil#alterarSenha";
		}
		if (!novaSenha.equals(confirmarNovaSenha)) {
			redirectAttributes.addFlashAttribute("mensagemErro", "A nova senha e a confirmação não correspondem.");
			return "redirect:/cliente/perfil#alterarSenha";
		}
		clienteService.atualizarSenha(clienteLogado, novaSenha);
		redirectAttributes.addFlashAttribute("mensagemSucesso", "Senha atualizada com sucesso!");
		return "redirect:/cliente/perfil#alterarSenha";
	}

	@PostMapping("/adicionar-endereco")
	public String adicionarEndereco(@Valid @ModelAttribute("novoEndereco") Endereco novoEndereco, BindingResult result,
			@RequestParam(value = "definirComoPadrao", required = false) boolean definirComoPadrao, HttpSession session,
			Model model, RedirectAttributes redirectAttributes) {
		Cliente clienteLogado = (Cliente) session.getAttribute("clienteLogado");
		if (result.hasErrors()) {
			model.addAttribute("cliente", clienteLogado);
			model.addAttribute("generos", Arrays.asList(Genero.values()));
			return "cliente/perfil";
		}
		if (novoEndereco.getDescricao() == null || novoEndereco.getDescricao().trim().isEmpty()) {
			novoEndereco.setDescricao("Endereço " + (clienteLogado.getEnderecosEntrega().size() + 1));
		}
		novoEndereco.setPrincipal(definirComoPadrao);
		clienteService.adicionarEnderecoEntrega(clienteLogado, novoEndereco);
		clienteLogado = clienteService.buscarPorId(clienteLogado.getId());
		session.setAttribute("clienteLogado", clienteLogado);
		redirectAttributes.addFlashAttribute("mensagemSucesso", "Endereço adicionado com sucesso!");
		return "redirect:/cliente/perfil#enderecosEntrega";
	}

	@PostMapping("/definir-endereco-padrao")
	public String definirEnderecoPadrao(@RequestParam Long enderecoId, HttpSession session,
			RedirectAttributes redirectAttributes) {
		Cliente clienteLogado = (Cliente) session.getAttribute("clienteLogado");
		clienteService.definirEnderecoPadrao(clienteLogado, enderecoId);
		clienteLogado = clienteService.buscarPorId(clienteLogado.getId());
		session.setAttribute("clienteLogado", clienteLogado);
		redirectAttributes.addFlashAttribute("mensagemSucesso", "Endereço padrão atualizado com sucesso!");
		return "redirect:/cliente/perfil#enderecosEntrega";
	}

}