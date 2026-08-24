package br.com.mercadoexpress.web;

import br.com.mercadoexpress.dto.request.MercadoRequest;
import br.com.mercadoexpress.dto.response.MercadoResponse;
import br.com.mercadoexpress.service.MercadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mercados")
public class MercadoViewController {

    private final MercadoService mercadoService;

    @GetMapping
    public String listar(Model model) {
        var pagina = mercadoService.listarTudo(PageRequest.of(0, 50));
        model.addAttribute("mercados", pagina.getContent());
        return "mercado/list";
    }

    @GetMapping("/novo")
    public String formNovo(Model model) {
        model.addAttribute("mercadoForm", new MercadoFormData());
        model.addAttribute("modoEdicao", false);
        return "mercado/form";
    }

    @PostMapping
    public String criar(@ModelAttribute MercadoFormData mercadoForm) {
        mercadoService.criar(toRequest(mercadoForm));
        return "redirect:/mercados";
    }

    @GetMapping("/{id}/editar")
    public String formEditar(@PathVariable Long id, Model model) {
        MercadoResponse mercado = mercadoService.pesquisarPorId(id);
        var form = new MercadoFormData(mercado.nome(), mercado.tipo(), mercado.setor(),
                mercado.tamanho(), mercado.preco());
        model.addAttribute("mercadoForm", form);
        model.addAttribute("modoEdicao", true);
        model.addAttribute("id", id);
        return "mercado/form";
    }

    @PostMapping("/{id}/atualizar")
    public String atualizar(@PathVariable Long id, @ModelAttribute MercadoFormData mercadoForm) {
        mercadoService.atualizar(id, toRequest(mercadoForm));
        return "redirect:/mercados";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id) {
        mercadoService.deletar(id);
        return "redirect:/mercados";
    }

    private MercadoRequest toRequest(MercadoFormData form) {
        return new MercadoRequest(form.getNome(), form.getTipo(), form.getSetor(),
                form.getTamanho(), form.getPreco());
    }
}