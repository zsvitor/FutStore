package com.futstore.futstore.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.Collection;

@Controller
public class LoginAdministrativoController {
    
    @GetMapping("/administrativo/login")
    public String login() {
        return "administrativo/login";
    }
    
    @GetMapping("/administrativo/login-success")
    public String loginSuccess(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();   
        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals("ROLE_ADMINISTRADOR")) {
                return "redirect:/administrativo/auth/administrador/admin-index";
            } else if (authority.getAuthority().equals("ROLE_ESTOQUISTA")) {
                return "redirect:/administrativo/auth/estoquista/estoq-index";
            }
        }    
        return "redirect:/administrativo/login";
    }
    
    @GetMapping("/administrativo/auth/administrador/admin-index")
    public String adminIndex() {
        if (!hasRole("ROLE_ADMINISTRADOR")) {
            return "redirect:/administrativo/auth/acesso-negado";
        }
        return "/administrativo/auth/administrador/admin-index";
    }
    
    @GetMapping("/administrativo/auth/estoquista/estoq-index")
    public String estoquistaIndex() {
        if (!hasRole("ROLE_ESTOQUISTA")) {
            return "redirect:/administrativo/auth/acesso-negado";
        }
        return "/administrativo/auth/estoquista/estoq-index";
    }
    
    @GetMapping("/acesso-negado")
    public String acessoNegado(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userRole = ""; 
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals("ROLE_ADMINISTRADOR")) {
                userRole = "Administrador";
                break;
            } else if (authority.getAuthority().equals("ROLE_ESTOQUISTA")) {
                userRole = "Estoquista";
                break;
            }
        }       
        model.addAttribute("papel", userRole);
        model.addAttribute("usuario", auth.getName());
        return "/administrativo/auth/acesso-negado";
    }

    private boolean hasRole(String role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            return auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(role));
        }
        return false;
    }
    
}