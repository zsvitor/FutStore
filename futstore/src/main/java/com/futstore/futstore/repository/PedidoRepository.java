package com.futstore.futstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.futstore.futstore.modelo.Cliente;
import com.futstore.futstore.modelo.Pedido;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

	List<Pedido> findByClienteOrderByDataPedidoDesc(Cliente cliente);

	@Query("SELECT p FROM Pedido p JOIN FETCH p.itens WHERE p.id = :id")
	Pedido findByIdWithItems(@Param("id") Long id);

}