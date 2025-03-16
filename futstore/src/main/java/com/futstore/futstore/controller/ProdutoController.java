package com.futstore.futstore.controller;

import com.futstore.futstore.modelo.Produto;
import com.futstore.futstore.modelo.ProdutoImagem;
import com.futstore.futstore.repository.ProdutoRepository;
import com.futstore.futstore.repository.ProdutoImagemRepository;
import com.futstore.futstore.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Optional;
import java.util.List;

@Controller
@RequestMapping("/produto")
public class ProdutoController {

	@Autowired
	private ProdutoRepository produtoRepository;

	@Autowired
	private ProdutoImagemRepository produtoImagemRepository;

	@Autowired
	private ProdutoService produtoService;

	@GetMapping("/novo")
	public String adicionarProduto(Model model) {
		model.addAttribute("produto", new Produto());
		return "/administrativo/auth/administrador/admin-criar-produto";
	}

	@PostMapping("/salvar")
	public String salvarProduto(@Valid Produto produto, BindingResult result,
			@RequestParam(value = "files", required = false) MultipartFile[] imagens,
			@RequestParam(value = "imagemPrincipal", required = false, defaultValue = "0") int imagemPrincipal,
			RedirectAttributes attributes, Model model) {
		if (result.hasErrors()) {
			return "/administrativo/auth/administrador/admin-criar-produto";
		}
		Produto produtoSalvo = produtoService.salvar(produto);
		if (imagens != null && imagens.length > 0) {
			for (int i = 0; i < imagens.length; i++) {
				MultipartFile imagem = imagens[i];
				if (imagem != null && !imagem.isEmpty()) {
					boolean principal = (i == imagemPrincipal);
					produtoService.salvarImagem(produtoSalvo.getId(), imagem, principal);
				}
			}
		}
		attributes.addFlashAttribute("mensagem", "Produto cadastrado com sucesso!");
		return "redirect:/produto/administrador/listar";
	}

	@GetMapping("/administrador/listar")
	public String listarProdutosAdmin(@RequestParam(required = false, defaultValue = "0") int page,
			@RequestParam(required = false) String filtroNome, Model model) {
		Page<Produto> produtosPage = produtoService.listarProdutos(page, filtroNome);
		model.addAttribute("produtos", produtosPage.getContent());
		model.addAttribute("paginaAtual", page);
		model.addAttribute("totalPaginas", produtosPage.getTotalPages());
		model.addAttribute("filtroNome", filtroNome);
		return "/administrativo/auth/administrador/admin-listar-produto";
	}

	@GetMapping("/administrador/trocar-status/{id}")
	public String toggleProdutoStatus(@PathVariable("id") Long id, RedirectAttributes attributes) {
		produtoService.toggleStatus(id);
		return "redirect:/produto/administrador/listar";
	}

	@GetMapping("/editar/{id}")
	public String editarProduto(@PathVariable("id") Long id, Model model) {
		Optional<Produto> produtoOptional = produtoRepository.findById(id);
		if (!produtoOptional.isPresent()) {
			throw new IllegalArgumentException("Produto inválido: " + id);
		}
		Produto produto = produtoOptional.get();
		List<ProdutoImagem> imagens = produtoImagemRepository.findByProdutoOrderById(produto);
		model.addAttribute("produto", produto);
		model.addAttribute("imagens", imagens);
		return "/administrativo/auth/administrador/admin-alterar-produto";
	}

	@PostMapping("/editar/{id}")
	public String editarProduto(@PathVariable("id") Long id, @Valid Produto produto, BindingResult result,
			@RequestParam(value = "files", required = false) MultipartFile[] imagens,
			@RequestParam(value = "imagemPrincipal", required = false, defaultValue = "-1") int imagemPrincipal,
			Model model, RedirectAttributes attributes) {
		if (result.hasErrors()) {
			produto.setId(id);
			return "/administrativo/auth/administrador/admin-alterar-produto";
		}
		produtoService.atualizar(produto, id);
		if (imagens != null && imagens.length > 0) {
			for (int i = 0; i < imagens.length; i++) {
				MultipartFile imagem = imagens[i];
				if (imagem != null && !imagem.isEmpty()) {
					boolean principal = (i == imagemPrincipal);
					produtoService.salvarImagem(id, imagem, principal);
				}
			}
		}
		attributes.addFlashAttribute("mensagem", "Produto atualizado com sucesso!");
		return "redirect:/produto/administrador/listar";
	}

	@PostMapping("/upload-imagens/{id}")
	public String uploadImagens(@PathVariable("id") Long id, @RequestParam("files") MultipartFile[] imagens,
			@RequestParam(value = "imagemPrincipal", required = false, defaultValue = "0") int imagemPrincipal,
			RedirectAttributes attributes) {
		if (imagens.length == 0 || imagens[0].isEmpty()) {
			attributes.addFlashAttribute("erro", "Selecione pelo menos uma imagem para upload");
			return "redirect:/produto/editar/" + id;
		}
		try {
			for (int i = 0; i < imagens.length; i++) {
				if (!imagens[i].isEmpty()) {
					boolean principal = (i == imagemPrincipal);
					produtoService.salvarImagem(id, imagens[i], principal);
				}
			}
			attributes.addFlashAttribute("mensagem", "Imagens carregadas com sucesso");
		} catch (Exception e) {
			attributes.addFlashAttribute("erro", "Erro ao carregar imagens: " + e.getMessage());
		}
		return "redirect:/produto/editar/" + id;
	}

	@GetMapping("/definir-imagem-principal/{produtoId}/{imagemId}")
	public String definirImagemPrincipal(@PathVariable("produtoId") Long produtoId,
			@PathVariable("imagemId") Long imagemId, RedirectAttributes attributes) {
		try {
			produtoService.definirImagemPrincipal(produtoId, imagemId);
			attributes.addFlashAttribute("mensagem", "Imagem principal definida com sucesso");
		} catch (Exception e) {
			attributes.addFlashAttribute("erro", "Erro ao definir imagem principal: " + e.getMessage());
		}
		return "redirect:/produto/editar/" + produtoId;
	}

	@GetMapping("/remover-imagem/{produtoId}/{imagemId}")
	public String removerImagem(@PathVariable("produtoId") Long produtoId, @PathVariable("imagemId") Long imagemId,
			RedirectAttributes attributes) {
		try {
			produtoService.removerImagem(imagemId);
			attributes.addFlashAttribute("mensagem", "Imagem removida com sucesso");
		} catch (Exception e) {
			attributes.addFlashAttribute("erro", "Erro ao remover imagem: " + e.getMessage());
		}
		return "redirect:/produto/editar/" + produtoId;
	}

	@GetMapping("/visualizar/{id}")
	public String visualizarProduto(@PathVariable("id") Long id, Model model) {
		Optional<Produto> produtoOptional = produtoRepository.findById(id);
		if (!produtoOptional.isPresent()) {
			throw new IllegalArgumentException("Produto inválido: " + id);
		}
		model.addAttribute("produto", produtoOptional.get());
		return "/administrativo/auth/administrador/admin-visualizar-produto";
	}

	@GetMapping("/estoquista/listar")
	public String listarProdutosEstoq(@RequestParam(required = false, defaultValue = "0") int page,
			@RequestParam(required = false) String filtroNome, Model model) {
		Page<Produto> produtosPage = produtoService.listarProdutos(page, filtroNome);
		model.addAttribute("produtos", produtosPage.getContent());
		model.addAttribute("paginaAtual", page);
		model.addAttribute("totalPaginas", produtosPage.getTotalPages());
		model.addAttribute("filtroNome", filtroNome);
		return "/administrativo/auth/estoquista/estoq-listar-produto";
	}

	@GetMapping("/estoquista/editar-estoque/{id}")
	public String editarEstoque(@PathVariable("id") Long id, Model model) {
		Optional<Produto> produtoOptional = produtoRepository.findById(id);
		if (!produtoOptional.isPresent()) {
			throw new IllegalArgumentException("Produto inválido: " + id);
		}
		model.addAttribute("produto", produtoOptional.get());
		return "/administrativo/auth/estoquista/estoq-alterar-produto";
	}

	@PostMapping("/estoquista/atualizar-estoque/{id}")
	public String atualizarEstoque(@PathVariable("id") Long id, @Valid Produto produto, BindingResult result,
			RedirectAttributes attributes) {
		if (result.hasErrors()) {
			produto.setId(id);
			return "/administrativo/auth/estoquista/estoq-alterar-produto";
		}
		Optional<Produto> produtoAtual = produtoRepository.findById(id);
		if (produtoAtual.isPresent()) {
			Produto produtoExistente = produtoAtual.get();
			produtoExistente.setQuantidadeEstoque(produto.getQuantidadeEstoque());
			produtoRepository.save(produtoExistente);
		}
		return "redirect:/produto/estoquista/listar";
	}
	
}