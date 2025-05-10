package com.futstore.futstore.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.futstore.futstore.modelo.Cliente;
import com.futstore.futstore.modelo.Pedido;
import com.futstore.futstore.modelo.StatusPedido;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

	List<Pedido> findByClienteOrderByDataPedidoDesc(Cliente cliente);

	@Query("SELECT p FROM Pedido p JOIN FETCH p.itens WHERE p.id = :id")
	Pedido findByIdWithItems(@Param("id") Long id);

	Page<Pedido> findAllByOrderByDataPedidoDesc(Pageable pageable);

	Page<Pedido> findByStatusOrderByDataPedidoDesc(StatusPedido status, Pageable pageable);

	@Query("SELECT p FROM Pedido p WHERE " + "(:status IS NULL OR p.status = :status) AND "
			+ "(:nomeCliente IS NULL OR :nomeCliente = '' OR "
			+ "LOWER(p.cliente.nomeCompleto) LIKE LOWER(CONCAT('%', :nomeCliente, '%')) OR "
			+ "LOWER(p.cliente.gmail) LIKE LOWER(CONCAT('%', :nomeCliente, '%'))) " + "ORDER BY p.dataPedido DESC")
	Page<Pedido> findByStatusAndClienteNome(@Param("status") StatusPedido status,
			@Param("nomeCliente") String nomeCliente, Pageable pageable);

}