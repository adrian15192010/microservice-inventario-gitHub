package com.microservice.venta.dto;

import com.microservice.venta.entity.ProductoVenta;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VentaDto {

    private Long Id;

    private Float total;

    private Float iva;

    private Float totalConIVA;

    private Long inventarioId;

    private List<ProductoVentaDto> productoVentaList;

}
