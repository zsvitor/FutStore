package com.futstore.futstore.controller;

import com.futstore.futstore.modelo.*;
import com.futstore.futstore.repository.PedidoRepository;
import com.futstore.futstore.service.ClienteService;
import com.futstore.futstore.service.PedidoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/pedido")
public class PedidoController {

	@Autowired
	private PedidoRepository pedidoRepository;

	@Autowired
	private PedidoService pedidoService;

	@Autowired
	private ClienteService clienteService;

	@GetMapping("/meus-pedidos")
	public String meusPedidos(HttpSession session, Model model) {
		Cliente clienteLogado = (Cliente) session.getAttribute("clienteLogado");
		if (clienteLogado == null) {
			return "redirect:/cliente/login";
		}
		List<Pedido> pedidos = pedidoRepository.findByClienteOrderByDataPedidoDesc(clienteLogado);
		model.addAttribute("pedidos", pedidos);
		return "cliente/meus-pedidos";
	}

	@GetMapping("/detalhe/{id}")
	public String detalhePedido(@PathVariable("id") Long id, HttpSession session, Model model) {
		Cliente clienteLogado = (Cliente) session.getAttribute("clienteLogado");
		if (clienteLogado == null) {
			return "redirect:/cliente/login";
		}

		Pedido pedido = pedidoRepository.findByIdWithItems(id);
		if (pedido == null || !pedido.getCliente().getId().equals(clienteLogado.getId())) {
			model.addAttribute("erro", "Pedido não encontrado");
			return "redirect:/pedido/meus-pedidos";
		}

		model.addAttribute("pedido", pedido);
		return "cliente/detalhe-pedido";
	}

	@PostMapping("/finalizar")
	public String finalizarPedido(@RequestParam(value = "enderecoEntregaId", required = false) Long enderecoEntregaId,
			@RequestParam(value = "formaPagamento", required = false) FormaPagamento formaPagamento,
			@ModelAttribute("novoEndereco") Endereco novoEndereco,
			@RequestParam(value = "definirComoPadrao", required = false) Boolean definirComoPadrao,
			@RequestParam(value = "cartao.numero", required = false) String numeroCartao,
			@RequestParam(value = "cartao.nome", required = false) String nomeCartao,
			@RequestParam(value = "cartao.validade", required = false) String validadeCartao,
			@RequestParam(value = "cartao.cvv", required = false) String cvvCartao,
			@RequestParam(value = "cartao.parcelas", required = false) Integer parcelasCartao, HttpSession session,
			RedirectAttributes attributes) {
		Cliente clienteLogado = (Cliente) session.getAttribute("clienteLogado");
		if (clienteLogado == null) {
			return "redirect:/cliente/login";
		}
		Carrinho carrinho = (Carrinho) session.getAttribute("carrinho");
		if (carrinho == null || carrinho.isEmpty()) {
			attributes.addFlashAttribute("erro", "Seu carrinho está vazio");
			return "redirect:/carrinho";
		}
		clienteLogado = clienteService.buscarPorId(clienteLogado.getId());
		Endereco enderecoEntrega = null;
		if (enderecoEntregaId != null) {
			enderecoEntrega = clienteLogado.getEnderecosEntrega().stream()
					.filter(e -> e.getId().equals(enderecoEntregaId)).findFirst().orElse(null);
		} else if (novoEndereco != null && novoEndereco.getCep() != null && !novoEndereco.getCep().isEmpty()) {
			novoEndereco.setPrincipal(definirComoPadrao != null && definirComoPadrao);
			enderecoEntrega = clienteService.adicionarEnderecoEntrega(clienteLogado, novoEndereco);
		}
		if (enderecoEntrega == null) {
			attributes.addFlashAttribute("erro", "Selecione ou adicione um endereço de entrega");
			return "redirect:/checkout/finalizar";
		}
		if (formaPagamento == null) {
			attributes.addFlashAttribute("erro", "Selecione uma forma de pagamento");
			return "redirect:/checkout/finalizar";
		}
		Pagamento pagamento = new Pagamento();
		pagamento.setFormaPagamento(formaPagamento);
		if (formaPagamento == FormaPagamento.CARTAO) {
			pagamento.setNumeroCartao(numeroCartao);
			pagamento.setNomeCartao(nomeCartao);
			pagamento.setValidade(validadeCartao);
			pagamento.setCodigoVerificador(cvvCartao);
			pagamento.setParcelas(parcelasCartao != null ? parcelasCartao : 1);
		}
		try {
			Long pedidoId = pedidoService.finalizarPedido(clienteLogado, carrinho, pagamento);
			session.removeAttribute("carrinho");
			attributes.addFlashAttribute("mensagem",
					"Pedido #" + pedidoId + " realizado com sucesso! Status: Aguardando Pagamento");
			return "redirect:/pedido/detalhe/" + pedidoId;
		} catch (Exception e) {
			attributes.addFlashAttribute("erro", "Erro ao finalizar pedido: " + e.getMessage());
			return "redirect:/checkout/finalizar";
		}
	}

}