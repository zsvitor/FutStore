package com.futstore.futstore.service;

import com.futstore.futstore.modelo.Produto;
import com.futstore.futstore.modelo.ProdutoImagem;
import com.futstore.futstore.repository.ProdutoRepository;
import com.futstore.futstore.repository.ProdutoImagemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

@Service
public class ProdutoService {

	@Value("${app.upload.dir:${user.home}/uploads}")
	private String uploadDir;

	@Autowired
	private ProdutoRepository produtoRepository;

	@Autowired
	private ProdutoImagemRepository produtoImagemRepository;

	public Produto salvar(Produto produto) {
		return produtoRepository.save(produto);
	}

	public Page<Produto> listarProdutos(int pagina, String filtroNome) {
		PageRequest pageRequest = PageRequest.of(pagina, 10);
		if (filtroNome != null && !filtroNome.isEmpty()) {
			return produtoRepository.findByNomeContainingIgnoreCaseOrderByIdDesc(filtroNome, pageRequest);
		}
		return produtoRepository.findAllByOrderByIdDesc(pageRequest);
	}

	public Produto atualizar(Produto produto, Long id) {
		Produto produtoBanco = produtoRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Produto inválido: " + id));
		produtoBanco.setNome(produto.getNome());
		produtoBanco.setDescricao(produto.getDescricao());
		produtoBanco.setPreco(produto.getPreco());
		produtoBanco.setQuantidadeEstoque(produto.getQuantidadeEstoque());
		produtoBanco.setAvaliacao(produto.getAvaliacao());
		return produtoRepository.save(produtoBanco);
	}

	public void toggleStatus(Long id) {
		Produto produto = produtoRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Produto inválido: " + id));
		produto.setAtivo(!produto.isAtivo());
		produtoRepository.save(produto);
	}

	public ProdutoImagem salvarImagem(Long produtoId, MultipartFile imagem, boolean principal) {
		if (!imagem.isEmpty()) {
			Produto produto = produtoRepository.findById(produtoId)
					.orElseThrow(() -> new IllegalArgumentException("Produto inválido: " + produtoId));
			String extensao = getExtensao(imagem.getOriginalFilename());
			String nomeUnico = UUID.randomUUID().toString() + extensao;
			String caminhoRelativo = nomeUnico;
			if (principal) {
				for (ProdutoImagem img : produto.getImagens()) {
					if (img.isPrincipal()) {
						img.setPrincipal(false);
						produtoImagemRepository.save(img);
					}
				}
			}
			ProdutoImagem produtoImagem = new ProdutoImagem();
			produtoImagem.setCaminho(caminhoRelativo);
			produtoImagem.setPrincipal(principal);
			produtoImagem.setProduto(produto);
			if (principal) {
				produto.setImagem(caminhoRelativo);
				produtoRepository.save(produto);
			}
			try {
				File diretorio = new File(uploadDir);
				if (!diretorio.exists()) {
					diretorio.mkdirs();
				}
				File arquivoDestino = new File(diretorio, nomeUnico);
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(arquivoDestino));
				stream.write(imagem.getBytes());
				stream.close();
				return produtoImagemRepository.save(produtoImagem);
			} catch (IOException e) {
				throw new RuntimeException("Erro ao salvar imagem: " + e.getMessage());
			}
		}
		return null;
	}

	private String getExtensao(String nomeArquivo) {
		if (nomeArquivo != null && nomeArquivo.contains(".")) {
			return nomeArquivo.substring(nomeArquivo.lastIndexOf("."));
		}
		return ".jpg";
	}

	public void definirImagemPrincipal(Long produtoId, Long imagemId) {
		Produto produto = produtoRepository.findById(produtoId)
				.orElseThrow(() -> new IllegalArgumentException("Produto inválido: " + produtoId));
		for (ProdutoImagem img : produto.getImagens()) {
			img.setPrincipal(false);
			produtoImagemRepository.save(img);
		}
		ProdutoImagem imagemPrincipal = produtoImagemRepository.findById(imagemId)
				.orElseThrow(() -> new IllegalArgumentException("Imagem inválida: " + imagemId));
		imagemPrincipal.setPrincipal(true);
		produtoImagemRepository.save(imagemPrincipal);
		produto.setImagem(imagemPrincipal.getCaminho());
		produtoRepository.save(produto);
	}

	public void removerImagem(Long imagemId) {
		ProdutoImagem imagem = produtoImagemRepository.findById(imagemId)
				.orElseThrow(() -> new IllegalArgumentException("Imagem inválida: " + imagemId));
		boolean eraPrincipal = imagem.isPrincipal();
		Produto produto = imagem.getProduto();
		File arquivo = new File(uploadDir, imagem.getCaminho());
		if (arquivo.exists()) {
			arquivo.delete();
		}
		produtoImagemRepository.delete(imagem);
		if (eraPrincipal && !produto.getImagens().isEmpty()) {
			ProdutoImagem novaPrincipal = produto.getImagens().get(0);
			novaPrincipal.setPrincipal(true);
			produtoImagemRepository.save(novaPrincipal);
			produto.setImagem(novaPrincipal.getCaminho());
			produtoRepository.save(produto);
		}
	}

}