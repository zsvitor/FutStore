package com.futstore.futstore.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.futstore.futstore.modelo.PapelUsuario;
import com.futstore.futstore.modelo.Usuario;
import com.futstore.futstore.repository.UsuarioRepository;
import com.futstore.futstore.service.UsuarioService;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private UsuarioService usuarioService;

	@GetMapping("/novo")
	public String adicionarUsuario(Model model) {
		model.addAttribute("usuario", new Usuario());
		model.addAttribute("papeis", Arrays.asList(PapelUsuario.values()));
		return "/auth/administrador/admin-criar-usuario";
	}

	@PostMapping("/salvar")
	public String salvarUsuario(@Valid Usuario usuario, BindingResult result, RedirectAttributes attributes,
			Model model) {
		if (usuarioRepository.existsByGmail(usuario.getGmail())) {
			result.rejectValue("gmail", "gmail.existente", "Gmail já cadastrado.");
		}
		if (result.hasErrors()) {
			model.addAttribute("papeis", Arrays.asList(PapelUsuario.values()));
			return "/auth/administrador/admin-criar-usuario";
		}
		usuario.setAtivo(true);
		usuarioService.salvar(usuario);
		return "redirect:/usuario/administrador/listar";
	}

	@RequestMapping("/administrador/listar")
	public String listarUsuario(Model model, @RequestParam(required = false) String filtroNome) {
		List<Usuario> usuarios;
		if (filtroNome != null && !filtroNome.isEmpty()) {
			usuarios = usuarioRepository.findByNomeContainingIgnoreCase(filtroNome);
		} else {
			usuarios = usuarioRepository.findAll();
		}
		model.addAttribute("usuarios", usuarios);
		model.addAttribute("filtroNome", filtroNome);
		return "/auth/administrador/admin-listar-usuario";
	}

	@GetMapping("/administrador/trocar-status/{id}")
	public String toggleUserStatus(@PathVariable("id") long id, Model model) {
		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Id inválido: " + id));
		usuario.setAtivo(!usuario.isAtivo());
		usuarioRepository.save(usuario);
		return "redirect:/usuario/administrador/listar";
	}

	@GetMapping("/editar/{id}")
	public String editarUsuario(@PathVariable("id") long id, Model model) {
		Optional<Usuario> usuarioVelho = usuarioRepository.findById(id);
		if (!usuarioVelho.isPresent()) {
			throw new IllegalArgumentException("Usuário inválido:" + id);
		}
		Usuario usuario = usuarioVelho.get();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String emailLogado = auth.getName();
		boolean isEditandoProprioUsuario = usuario.getGmail().equals(emailLogado);

		model.addAttribute("usuario", usuario);
		model.addAttribute("isEditandoProprioUsuario", isEditandoProprioUsuario);
		model.addAttribute("papeis", Arrays.asList(PapelUsuario.values()));
		return "/auth/administrador/admin-alterar-usuario";
	}

	@PostMapping("/editar/{id}")
	public String editarUsuario(@PathVariable("id") long id, @Valid Usuario usuario, BindingResult result, Model model,
			RedirectAttributes attributes) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String emailLogado = auth.getName();

		Usuario usuarioOriginal = usuarioRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Usuário inválido:" + id));

		boolean isEditandoProprioUsuario = usuarioOriginal.getGmail().equals(emailLogado);

		if (isEditandoProprioUsuario && !usuario.getPapel().equals(usuarioOriginal.getPapel())) {
			usuario.setPapel(usuarioOriginal.getPapel());
			attributes.addFlashAttribute("mensagem",
					"Você não pode alterar seu próprio papel. As outras informações foram atualizadas.");
		}

		if (result.hasErrors()) {
			usuario.setId(id);
			model.addAttribute("isEditandoProprioUsuario", isEditandoProprioUsuario);
			model.addAttribute("papeis", Arrays.asList(PapelUsuario.values()));
			return "/auth/administrador/admin-alterar-usuario";
		}

		usuarioService.atualizar(usuario, id);
		if (!attributes.getFlashAttributes().containsKey("mensagem")) {
			attributes.addFlashAttribute("mensagem", "Usuário atualizado com sucesso!");
		}
		return "redirect:/usuario/administrador/listar";
	}

}