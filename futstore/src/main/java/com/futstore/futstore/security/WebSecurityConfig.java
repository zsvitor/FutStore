package com.futstore.futstore.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	@Autowired
	private UsuarioDetailsService usuarioDetailsService;

	@Autowired
	private ClienteLoginFilter clienteLoginFilter;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(usuarioDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

	@Bean
	public FilterRegistrationBean<ClienteLoginFilter> clienteLoginFilterRegistration() {
		FilterRegistrationBean<ClienteLoginFilter> registration = new FilterRegistrationBean<>();
		registration.setFilter(clienteLoginFilter);
		registration.addUrlPatterns("/cliente/*", "/carrinho/*", "/checkout/*", "/pedido/meus-pedidos",
				"/pedido/detalhe/*", "/pedido/finalizar", "/produto/detalhe/*", "/home", "/");
		registration.setName("clienteLoginFilter");
		registration.setOrder(1);
		return registration;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(authorize -> authorize
				.requestMatchers("/", "/home", "/cliente/**", "/css/**", "/js/**", "/bootstrap-5.1.3-dist/**",
						"/jquery-3.6.0-dist/**", "/fragments/**", "/uploads/**", "/imagens/**", "/produto/detalhe/**",
						"/carrinho/**", "/carrinho", "/carrinho/adicionar", "/carrinho/atualizar",
						"/carrinho/remover/**", "/checkout/**", "/pedido/meus-pedidos", "/pedido/detalhe/**",
						"/pedido/finalizar")
				.permitAll()
				.requestMatchers("/administrativo/login", "/usuario/novo", "/usuario/salvar", "/acesso-negado")
				.permitAll().requestMatchers("/pedido/estoquista/**").hasRole("ESTOQUISTA")
				.requestMatchers("/auth/administrador/**").hasRole("ADMINISTRADOR")
				.requestMatchers("/auth/estoquista/**").hasRole("ESTOQUISTA").requestMatchers("/estoque/**")
				.hasAnyRole("ESTOQUISTA", "ADMINISTRADOR").anyRequest().authenticated())
				.formLogin(form -> form.loginPage("/administrativo/login")
						.loginProcessingUrl("/administrativo/login-process")
						.defaultSuccessUrl("/administrativo/login-success", true)
						.failureUrl("/administrativo/login?error=true").permitAll())
				.logout(logout -> logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
						.logoutSuccessUrl("/").invalidateHttpSession(true).clearAuthentication(true).permitAll())
				.exceptionHandling(ex -> ex.accessDeniedPage("/acesso-negado")).csrf(csrf -> csrf.disable());
		return http.build();
	}

}