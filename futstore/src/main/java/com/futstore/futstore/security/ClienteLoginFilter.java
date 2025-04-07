package com.futstore.futstore.security;

import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import com.futstore.futstore.modelo.Cliente;

@Component
public class ClienteLoginFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		HttpSession session = httpRequest.getSession(false);
		String requestURI = httpRequest.getRequestURI();
		if (requestURI.startsWith("/administrativo") || requestURI.startsWith("/usuario/administrador")
				|| requestURI.startsWith("/auth/administrador") || requestURI.startsWith("/auth/estoquista")
				|| requestURI.contains("login-process")) {
			chain.doFilter(request, response);
			return;
		}
		boolean isPublicResource = requestURI.startsWith("/css/") || requestURI.startsWith("/js/")
				|| requestURI.startsWith("/imagens/") || requestURI.startsWith("/bootstrap-5.1.3-dist/")
				|| requestURI.startsWith("/jquery-3.6.0-dist/") || requestURI.equals("/")
				|| requestURI.startsWith("/cliente/login") || requestURI.startsWith("/cliente/cadastro")
				|| requestURI.startsWith("/cliente/autenticar") || requestURI.startsWith("/produto/detalhe/")
				|| requestURI.startsWith("/carrinho/");
		boolean isClientResource = requestURI.startsWith("/pedido/") || requestURI.startsWith("/cliente/perfil")
				|| requestURI.startsWith("/cliente/endereco") || requestURI.startsWith("/checkout/");
		Cliente clienteLogado = null;
		boolean isLoggedIn = false;
		if (session != null) {
			clienteLogado = (Cliente) session.getAttribute("clienteLogado");
			isLoggedIn = (clienteLogado != null);
		}
		if (isClientResource && !isLoggedIn) {
			session = httpRequest.getSession(true);
			session.setAttribute("redirectUrl", requestURI);
			httpResponse.sendRedirect(httpRequest.getContextPath() + "/cliente/login");
			return;
		}
		chain.doFilter(request, response);
	}

}