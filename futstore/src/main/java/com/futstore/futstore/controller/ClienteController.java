package com.futstore.futstore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ClienteController {

	@GetMapping({ "/", "/home" })
	public String home() {
		return "cliente/home";
	}

}