package com.microservice.venta.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductoVentaDto {

    private Long Id;

    private String name;

    private Float precioUnitario;

    private Integer cantidad;

    private Float montoTotal;


}
