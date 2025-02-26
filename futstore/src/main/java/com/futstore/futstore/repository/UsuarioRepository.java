package com.futstore.futstore.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.futstore.futstore.modelo.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
    boolean existsByCpf(String cpf);
    boolean existsByGmail(String gmail);
    Optional<Usuario> findByGmail(String gmail);
    List<Usuario> findByNomeContainingIgnoreCase(String nome);
}