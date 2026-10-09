
package com.ferreteria.ferreteria.controller;

import com.ferreteria.ferreteria.model.Producto;
import com.ferreteria.ferreteria.repository.ProductoRepository;
import com.ferreteria.ferreteria.repository.VentaRepository;
import com.ferreteria.ferreteria.service.VentaService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class VentaController {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final VentaService ventaService;

    public VentaController(
            VentaRepository ventaRepository,
            ProductoRepository productoRepository,
            VentaService ventaService) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
        this.ventaService = ventaService;
    }

    @GetMapping("/ventas")
    public String listarVentas(Model model) {

        var ventas = ventaRepository.findAllByOrderByFechaDesc();

        BigDecimal totalRecaudado = ventas.stream()
                .map(venta -> venta.getTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("ventas", ventas);
        model.addAttribute("totalVentas", ventas.size());
        model.addAttribute("totalRecaudado", totalRecaudado);

        return "ventas";
    }

    @GetMapping("/ventas/nueva")
    public String mostrarFormulario(Model model) {

        List<Producto> productos = productoRepository.findAll()
                .stream()
                .filter(producto ->
                        producto.getExistencias() != null
                        && producto.getExistencias() > 0)
                .toList();

        model.addAttribute("productos", productos);

        return "venta-form";
    }

    @PostMapping("/ventas/guardar")
    public String guardarVenta(
            @RequestParam Long productoId,
            @RequestParam Integer cantidad,
            RedirectAttributes redirectAttributes) {

        try {
            ventaService.registrarVenta(productoId, cantidad);

            redirectAttributes.addFlashAttribute(
                    "mensaje",
                    "Venta registrada correctamente. Inventario actualizado.");

            return "redirect:/ventas";

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "error", e.getMessage());

            return "redirect:/ventas/nueva";
        }
    }

    @GetMapping("/ventas/{id}/factura")
public String mostrarFactura(
        @PathVariable Long id,
        Model model,
        RedirectAttributes redirectAttributes) {

    return ventaRepository.findWithProductoById(id)
            .map(venta -> {
                model.addAttribute("venta", venta);
                return "factura";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute(
                        "error",
                        "No se encontró la venta solicitada.");

                return "redirect:/ventas";
            });
    }
}
