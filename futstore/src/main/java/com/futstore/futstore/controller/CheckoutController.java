package com.futstore.futstore.controller;

import com.futstore.futstore.modelo.*;
import com.futstore.futstore.service.ClienteService;
import com.futstore.futstore.service.PedidoService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

	@Autowired
	private ClienteService clienteService;

	@Autowired
	private PedidoService pedidoService;

	@GetMapping("/finalizar")
	public String finalizarCompra(HttpSession session, Model model, RedirectAttributes attributes) {
		Cliente clienteLogado = (Cliente) session.getAttribute("clienteLogado");
		if (clienteLogado == null) {
			session.setAttribute("redirectUrl", "/checkout/finalizar");
			return "redirect:/cliente/login";
		}
		Carrinho carrinho = (Carrinho) session.getAttribute("carrinho");
		if (carrinho == null || carrinho.isEmpty()) {
			model.addAttribute("erro", "Seu carrinho está vazio");
			return "redirect:/carrinho";
		}
		if (carrinho.getValorFrete() == null || carrinho.getValorFrete().compareTo(BigDecimal.ZERO) <= 0) {
			attributes.addFlashAttribute("erro", "Por favor, selecione uma opção de frete antes de continuar");
			return "redirect:/carrinho";
		}
		clienteLogado = clienteService.buscarPorId(clienteLogado.getId());
		List<Endereco> enderecosEntrega = clienteLogado.getEnderecosEntrega();
		if (enderecosEntrega == null || enderecosEntrega.isEmpty()) {
			model.addAttribute("erro", "Você precisa cadastrar um endereço de entrega");
			return "redirect:/cliente/perfil#enderecosEntrega";
		}
		model.addAttribute("cliente", clienteLogado);
		model.addAttribute("carrinho", carrinho);
		Endereco enderecoPrincipal = enderecosEntrega.stream().filter(Endereco::isPrincipal).findFirst()
				.orElse(enderecosEntrega.get(0));
		model.addAttribute("enderecoPrincipal", enderecoPrincipal);
		if (!model.containsAttribute("pagamento")) {
			model.addAttribute("pagamento", new Pagamento());
		}
		if (!model.containsAttribute("novoEndereco")) {
			model.addAttribute("novoEndereco", new Endereco());
		}
		model.addAttribute("formasPagamento", FormaPagamento.values());
		model.addAttribute("parcelasDisponiveis", List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12));
		return "cliente/finalizar-compra";
	}

	@PostMapping("/selecionar-endereco")
	public String selecionarEndereco(@RequestParam("enderecoId") Long enderecoId, HttpSession session,
			RedirectAttributes attributes) {
		Cliente clienteLogado = (Cliente) session.getAttribute("clienteLogado");
		if (clienteLogado == null) {
			return "redirect:/cliente/login";
		}
		clienteService.definirEnderecoPadrao(clienteLogado, enderecoId);
		attributes.addFlashAttribute("mensagem", "Endereço de entrega selecionado com sucesso");
		return "redirect:/checkout/finalizar";
	}

	@PostMapping("/adicionar-endereco")
	public String adicionarEndereco(@Valid @ModelAttribute("novoEndereco") Endereco novoEndereco, BindingResult result,
			HttpSession session, RedirectAttributes attributes) {
		Cliente clienteLogado = (Cliente) session.getAttribute("clienteLogado");
		if (clienteLogado == null) {
			return "redirect:/cliente/login";
		}
		if (result.hasErrors()) {
			attributes.addFlashAttribute("org.springframework.validation.BindingResult.novoEndereco", result);
			attributes.addFlashAttribute("novoEndereco", novoEndereco);
			attributes.addFlashAttribute("erro", "Verifique os campos do endereço");
			return "redirect:/checkout/finalizar";
		}
		clienteService.adicionarEnderecoEntrega(clienteLogado, novoEndereco);
		attributes.addFlashAttribute("mensagem", "Novo endereço adicionado com sucesso");
		return "redirect:/checkout/finalizar";
	}

	@PostMapping("/processar-pagamento")
	public String processarPagamento(@Valid @ModelAttribute("pagamento") Pagamento pagamento, BindingResult result,
			@RequestParam(value = "enderecoEntregaId", required = false) Long enderecoEntregaId, HttpSession session,
			RedirectAttributes attributes, Model model) {
		Cliente clienteLogado = (Cliente) session.getAttribute("clienteLogado");
		if (clienteLogado == null) {
			return "redirect:/cliente/login";
		}
		Carrinho carrinho = (Carrinho) session.getAttribute("carrinho");
		if (carrinho == null || carrinho.isEmpty()) {
			attributes.addFlashAttribute("erro", "Seu carrinho está vazio");
			return "redirect:/carrinho";
		}
		if (carrinho.getValorFrete() == null || carrinho.getValorFrete().compareTo(BigDecimal.ZERO) <= 0) {
	        attributes.addFlashAttribute("erro", "Por favor, selecione uma opção de frete antes de continuar");
	        return "redirect:/carrinho";
	    }
		if (enderecoEntregaId == null) {
			attributes.addFlashAttribute("erro", "Selecione um endereço de entrega");
			return "redirect:/checkout/finalizar";
		}
		if (result.hasErrors()) {
			attributes.addFlashAttribute("org.springframework.validation.BindingResult.pagamento", result);
			attributes.addFlashAttribute("pagamento", pagamento);
			return "redirect:/checkout/finalizar";
		}
		if (pagamento.getFormaPagamento() == FormaPagamento.CARTAO && !pagamento.isCartaoValido()) {
			attributes.addFlashAttribute("erro", "Preencha corretamente os dados do cartão");
			attributes.addFlashAttribute("pagamento", pagamento);
			return "redirect:/checkout/finalizar";
		}
		session.setAttribute("pagamento", pagamento);
		return "redirect:/checkout/resumo";
	}

	@GetMapping("/resumo")
	public String resumoPedido(HttpSession session, Model model) {
		Cliente clienteLogado = (Cliente) session.getAttribute("clienteLogado");
		if (clienteLogado == null) {
			return "redirect:/cliente/login";
		}
		Carrinho carrinho = (Carrinho) session.getAttribute("carrinho");
		if (carrinho == null || carrinho.isEmpty()) {
			model.addAttribute("erro", "Seu carrinho está vazio");
			return "redirect:/carrinho";
		}
		Pagamento pagamento = (Pagamento) session.getAttribute("pagamento");
		if (pagamento == null) {
			model.addAttribute("erro", "Informações de pagamento não encontradas");
			return "redirect:/checkout/finalizar";
		}
		clienteLogado = clienteService.buscarPorId(clienteLogado.getId());
		Endereco enderecoEntrega = clienteLogado.getEnderecosEntrega().stream().filter(Endereco::isPrincipal)
				.findFirst().orElse(null);
		if (enderecoEntrega == null) {
			model.addAttribute("erro", "Endereço de entrega não encontrado");
			return "redirect:/checkout/finalizar";
		}
		model.addAttribute("cliente", clienteLogado);
		model.addAttribute("carrinho", carrinho);
		model.addAttribute("pagamento", pagamento);
		model.addAttribute("enderecoEntrega", enderecoEntrega);
		return "cliente/resumo-pedido";
	}

	@PostMapping("/confirmar")
	public String confirmarPedido(HttpSession session, RedirectAttributes attributes) {
		Cliente clienteLogado = (Cliente) session.getAttribute("clienteLogado");
		if (clienteLogado == null) {
			return "redirect:/cliente/login";
		}
		Carrinho carrinho = (Carrinho) session.getAttribute("carrinho");
		Pagamento pagamento = (Pagamento) session.getAttribute("pagamento");
		if (carrinho == null || carrinho.isEmpty() || pagamento == null) {
			attributes.addFlashAttribute("erro", "Não foi possível concluir o pedido");
			return "redirect:/checkout/finalizar";
		}
		try {
			Long pedidoId = pedidoService.finalizarPedido(clienteLogado, carrinho, pagamento);
			session.removeAttribute("carrinho");
			session.removeAttribute("pagamento");
			attributes.addFlashAttribute("mensagem", "Pedido realizado com sucesso!");
			return "redirect:/pedido/detalhe/" + pedidoId;
		} catch (Exception e) {
			attributes.addFlashAttribute("erro", "Erro ao finalizar pedido: " + e.getMessage());
			return "redirect:/checkout/finalizar";
		}
	}

}