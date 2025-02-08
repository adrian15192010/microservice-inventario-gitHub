package com.microservice.venta.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ListProductoDto {

    Long inventarioId;

    List<ProductoDto> productoDtoList;


}
