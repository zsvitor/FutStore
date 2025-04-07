package com.futstore.futstore.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.futstore.futstore.modelo.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    boolean existsByCpf(String cpf);
    boolean existsByGmail(String gmail);
    Optional<Cliente> findByGmail(String gmail);
}