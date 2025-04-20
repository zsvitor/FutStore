package com.futstore.futstore.controller;

import com.futstore.futstore.modelo.Cliente;
import com.futstore.futstore.modelo.Pedido;
import com.futstore.futstore.repository.PedidoRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

@Controller
@RequestMapping("/pedido")
public class PedidoController {

	@Autowired
	private PedidoRepository pedidoRepository;

	@GetMapping("/meus-pedidos")
	public String meusPedidos(HttpSession session, Model model) {
		Cliente clienteLogado = (Cliente) session.getAttribute("clienteLogado");
		if (clienteLogado == null) {
			return "redirect:/cliente/login";
		}
		List<Pedido> pedidos = pedidoRepository.findByClienteOrderByDataPedidoDesc(clienteLogado);
		model.addAttribute("pedidos", pedidos);
		return "cliente/meus-pedidos";
	}

}