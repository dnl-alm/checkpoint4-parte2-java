package br.com.mercadoexpress.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CadastroForm {
    private String username;
    private String senha;
    private String confirmarSenha;
}