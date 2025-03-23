package com.futstore.futstore.controller;

import com.futstore.futstore.modelo.Produto;
import com.futstore.futstore.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

	@Autowired
	private ProdutoService produtoService;

	@GetMapping({ "/", "/home" })
	public String home(@RequestParam(required = false, defaultValue = "0") int page,
			@RequestParam(required = false) String filtroNome, Model model) {
		Page<Produto> produtosPage = produtoService.listarProdutos(page, filtroNome);
		model.addAttribute("produtos", produtosPage.getContent());
		model.addAttribute("paginaAtual", page);
		model.addAttribute("totalPaginas", produtosPage.getTotalPages());
		model.addAttribute("filtroNome", filtroNome);
		return "cliente/home";
	}

}