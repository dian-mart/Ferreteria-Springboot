
package com.ferreteria.ferreteria.service;

import com.ferreteria.ferreteria.model.Producto;
import com.ferreteria.ferreteria.model.Venta;
import com.ferreteria.ferreteria.repository.ProductoRepository;
import com.ferreteria.ferreteria.repository.VentaRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class VentaService {

    private final ProductoRepository productoRepository;
    private final VentaRepository ventaRepository;

    public VentaService(
            ProductoRepository productoRepository,
            VentaRepository ventaRepository) {
        this.productoRepository = productoRepository;
        this.ventaRepository = ventaRepository;
    }

    @Transactional
    public Venta registrarVenta(Long productoId, Integer cantidad) {

        if (productoId == null) {
            throw new IllegalArgumentException(
                    "Debes seleccionar un producto.");
        }

        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero.");
        }

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "El producto seleccionado no existe."));

        if (producto.getExistencias() == null
                || producto.getExistencias() < cantidad) {
            throw new IllegalArgumentException(
                    "No hay suficientes existencias para esta venta.");
        }

        if (producto.getPrecio() == null
                || producto.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "El producto no tiene un precio válido.");
        }

        BigDecimal precioUnitario = producto.getPrecio();

        BigDecimal total = precioUnitario.multiply(
                BigDecimal.valueOf(cantidad));

        producto.setExistencias(
                producto.getExistencias() - cantidad);

        productoRepository.save(producto);

        Venta venta = new Venta();
        venta.setProducto(producto);
        venta.setCantidad(cantidad);
        venta.setPrecioUnitario(precioUnitario);
        venta.setTotal(total);
        venta.setFecha(LocalDateTime.now());

        return ventaRepository.save(venta);
    }
}
