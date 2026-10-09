package com.ferreteria.ferreteria.controller;

import com.ferreteria.ferreteria.model.Producto;
import com.ferreteria.ferreteria.repository.ProductoRepository;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProductoController {

    private final ProductoRepository productoRepository;

    public ProductoController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @GetMapping("/productos")
    public String listarProductos(Model model) {
        model.addAttribute("productos", productoRepository.findAll());
        return "productos";
    }

    @GetMapping("/productos/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("producto", new Producto());
        return "producto-form";
    }

    @PostMapping("/productos/guardar")
    public String guardarProducto(
            @Valid @ModelAttribute("producto") Producto producto,
            BindingResult resultado,
            RedirectAttributes redirectAttributes) {

        if (resultado.hasErrors()) {
            return "producto-form";
        }

        productoRepository.save(producto);
        redirectAttributes.addFlashAttribute(
                "mensaje", "Producto guardado correctamente.");

        return "redirect:/productos";
    }

    @GetMapping("/productos/editar/{id}")
    public String editarProducto(
            @PathVariable Long id, Model model,
            RedirectAttributes redirectAttributes) {

        return productoRepository.findById(id)
                .map(producto -> {
                    model.addAttribute("producto", producto);
                    return "producto-form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute(
                            "error", "El producto no existe.");
                    return "redirect:/productos";
                });
    }

    @PostMapping("/productos/eliminar/{id}")
    public String eliminarProducto(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        if (productoRepository.existsById(id)) {
            productoRepository.deleteById(id);
            redirectAttributes.addFlashAttribute(
                    "mensaje", "Producto eliminado correctamente.");
        } else {
            redirectAttributes.addFlashAttribute(
                    "error", "El producto no existe.");
        }

        return "redirect:/productos";
    }
}
