package com.futstore.futstore.controller;

import com.futstore.futstore.modelo.Carrinho;
import com.futstore.futstore.modelo.ItemCarrinho;
import com.futstore.futstore.modelo.Produto;
import com.futstore.futstore.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

@Controller
@RequestMapping("/carrinho")
public class CarrinhoController {

	@Autowired
	private ProdutoRepository produtoRepository;

	@GetMapping
	public String verCarrinho(Model model, HttpSession session) {
		Carrinho carrinho = getCarrinhoFromSession(session);
		refreshCarrinhoProdutos(carrinho);
		model.addAttribute("carrinho", carrinho);
		return "cliente/carrinho";
	}

	private void refreshCarrinhoProdutos(Carrinho carrinho) {
		for (ItemCarrinho item : new ArrayList<>(carrinho.getItens())) {
			Optional<Produto> produtoRefreshed = produtoRepository.findByIdWithImagens(item.getProduto().getId());
			if (produtoRefreshed.isPresent()) {
				item.setProduto(produtoRefreshed.get());
			}
		}
	}

	@PostMapping("/adicionar")
	public String adicionarAoCarrinho(@RequestParam("produtoId") Long produtoId,
			@RequestParam("quantidade") int quantidade, @RequestParam("acao") String acao, HttpSession session,
			RedirectAttributes attributes) {
		Optional<Produto> produtoOpt = produtoRepository.findByIdWithImagens(produtoId);
		if (!produtoOpt.isPresent()) {
			attributes.addFlashAttribute("erro", "Produto não encontrado");
			return "redirect:/";
		}
		Produto produto = produtoOpt.get();
		if (quantidade > produto.getQuantidadeEstoque()) {
			attributes.addFlashAttribute("erro", "Quantidade solicitada não disponível em estoque");
			return "redirect:/produto/detalhe/" + produtoId;
		}
		Carrinho carrinho = getCarrinhoFromSession(session);
		carrinho.adicionarItem(produto, quantidade);
		session.setAttribute("carrinho", carrinho);
		if ("comprar".equals(acao)) {
			return "redirect:/carrinho";
		} else {
			attributes.addFlashAttribute("mensagem", "Produto adicionado ao carrinho");
			return "redirect:/produto/detalhe/" + produtoId;
		}
	}

	@PostMapping("/atualizar")
	public String atualizarCarrinho(@RequestParam("produtoId") Long produtoId,
			@RequestParam("quantidade") int quantidade, HttpSession session) {
		Carrinho carrinho = getCarrinhoFromSession(session);
		carrinho.atualizarQuantidade(produtoId, quantidade);
		return "redirect:/carrinho";
	}

	@GetMapping("/remover/{produtoId}")
	public String removerItem(@PathVariable("produtoId") Long produtoId, HttpSession session) {
		Carrinho carrinho = getCarrinhoFromSession(session);
		carrinho.removerItem(produtoId);
		return "redirect:/carrinho";
	}

	@PostMapping("/aplicar-frete")
	public String aplicarFrete(@RequestParam("frete") BigDecimal valorFrete, HttpSession session) {
		Carrinho carrinho = getCarrinhoFromSession(session);
		carrinho.setValorFrete(valorFrete);
		return "redirect:/carrinho";
	}

	private Carrinho getCarrinhoFromSession(HttpSession session) {
		Carrinho carrinho = (Carrinho) session.getAttribute("carrinho");
		if (carrinho == null) {
			carrinho = new Carrinho();
			session.setAttribute("carrinho", carrinho);
		}
		return carrinho;
	}

}