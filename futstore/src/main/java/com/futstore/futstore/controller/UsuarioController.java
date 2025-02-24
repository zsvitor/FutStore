package com.futstore.futstore.controller;

import java.util.Arrays;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.futstore.futstore.modelo.PapelUsuario;
import com.futstore.futstore.modelo.Usuario;
import com.futstore.futstore.repository.UsuarioRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@GetMapping("/novo")
	public String adicionarUsuario(Model model) {
		model.addAttribute("usuario", new Usuario());
		model.addAttribute("papeis", Arrays.asList(PapelUsuario.values()));
		return "/publica-criar-usuario";
	}
	
	@PostMapping("/salvar")
	public String salvarUsuario(@Valid Usuario usuario, BindingResult result, RedirectAttributes attributes, Model model) {
	    if (usuarioRepository.existsByCpf(usuario.getCpf())) {
	        result.rejectValue("cpf", "cpf.existente", "Este CPF já está cadastrado.");
	    }
	    if (usuarioRepository.existsByGmail(usuario.getGmail())) {
	        result.rejectValue("gmail", "gmail.existente", "Este Gmail já está cadastrado.");
	    }
	    if (result.hasErrors()) {
	    	model.addAttribute("papeis", Arrays.asList(PapelUsuario.values()));
	        return "/publica-criar-usuario";
	    }
	    usuario.setAtivo(true);
	    usuarioRepository.save(usuario);
	    attributes.addFlashAttribute("mensagem", "Usuário salvo com sucesso!");
	    return "redirect:/usuario/novo";
	}
	
	@RequestMapping("/administrador/listar")
	public String listarUsuario(Model model) {
		model.addAttribute("usuarios", usuarioRepository.findAll());		
		return "/auth/administrador/admin-listar-usuario";		
	}
	
	@GetMapping("/administrador/trocar-status/{id}")
	public String toggleUserStatus(@PathVariable("id") long id, Model model) {
	    Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Id inválido: " + id));
	    usuario.setAtivo(!usuario.isAtivo()); //
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
	    model.addAttribute("usuario", usuario);
	    model.addAttribute("papeis", Arrays.asList(PapelUsuario.values()));
	    return "/auth/administrador/admin-alterar-usuario";
	}

	@PostMapping("/editar/{id}")
	public String editarUsuario(@PathVariable("id") long id, @Valid Usuario usuario, BindingResult result, Model model) {
	    if (result.hasErrors()) {
	        usuario.setId(id);
	        model.addAttribute("papeis", Arrays.asList(PapelUsuario.values()));
	        return "/auth/administrador/admin-alterar-usuario";
	    }
	    Optional<Usuario> usuarioExistente = usuarioRepository.findById(id);
	    if (!usuarioExistente.isPresent()) {
	        throw new IllegalArgumentException("Usuário inválido: " + id);
	    }
	    Usuario usuarioBanco = usuarioExistente.get();
	    usuarioBanco.setNome(usuario.getNome());
	    usuarioBanco.setCpf(usuario.getCpf());
	    usuarioBanco.setSenha(usuario.getSenha());
	    usuarioBanco.setPapel(usuario.getPapel());
	    usuarioRepository.save(usuarioBanco);
	    return "redirect:/usuario/administrador/listar";
	}
	
}
