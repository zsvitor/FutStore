package com.futstore.futstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.futstore.futstore.modelo.Pagamento;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

}