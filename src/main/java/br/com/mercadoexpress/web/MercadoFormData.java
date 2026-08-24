package br.com.mercadoexpress.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MercadoFormData {
    private String nome;
    private String tipo;
    private String setor;
    private Double tamanho;
    private Double preco;
}