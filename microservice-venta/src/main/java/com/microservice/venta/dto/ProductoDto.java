package com.microservice.venta.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductoDto {

    private Long id;

    private String name;

    private Float precioUnitario;

    private Integer cantidad;

}
