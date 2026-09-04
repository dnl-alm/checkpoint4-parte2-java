package br.com.mercadoexpress.web;

import br.com.mercadoexpress.domain.usuario.Usuario;
import br.com.mercadoexpress.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AutenticacaoController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String telaLogin() {
        return "auth/login";
    }

    @GetMapping("/cadastro")
    public String formCadastro(Model model) {
        model.addAttribute("cadastroForm", new CadastroForm());
        return "auth/cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastrar(@ModelAttribute CadastroForm cadastroForm, Model model) {
        if (!cadastroForm.getSenha().equals(cadastroForm.getConfirmarSenha())) {
            model.addAttribute("erro", "As senhas não coincidem.");
            return "auth/cadastro";
        }
        if (usuarioRepository.findByUsername(cadastroForm.getUsername()).isPresent()) {
            model.addAttribute("erro", "Esse usuário já existe.");
            return "auth/cadastro";
        }

        var usuario = Usuario.builder()
                .username(cadastroForm.getUsername())
                .senha(passwordEncoder.encode(cadastroForm.getSenha()))
                .build();
        usuarioRepository.save(usuario);

        return "redirect:/login?cadastroOk";
    }
}