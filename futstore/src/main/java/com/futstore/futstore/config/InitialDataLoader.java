package com.futstore.futstore.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.futstore.futstore.modelo.PapelUsuario;
import com.futstore.futstore.modelo.Usuario;
import com.futstore.futstore.repository.UsuarioRepository;

@Component
public class InitialDataLoader implements CommandLineRunner {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public void run(String... args) throws Exception {
		if (usuarioRepository.count() == 0) {
			Usuario admin = new Usuario();
			admin.setNome("Administrador");
			admin.setGmail("admin@gmail.com");
			admin.setSenha(passwordEncoder.encode("12345"));
			admin.setCpf("600.796.770-02");
			admin.setPapel(PapelUsuario.ADMINISTRADOR);
			admin.setAtivo(true);
			usuarioRepository.save(admin);
			System.out.println("Usuário administrador inicial criado!");
		}
	}

}