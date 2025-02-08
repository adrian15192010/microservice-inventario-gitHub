package com.microservice.venta.client;

import com.microservice.venta.dto.ListProductoDto;
import com.microservice.venta.dto.ProductoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "msvc-inventario", url = "localhost:8040")
public interface InventarioClient {


    @PostMapping("/api/inventario/product/venta/{token}")
    ListProductoDto getProducts(@RequestBody ListProductoDto dto, @PathVariable String token);

    @GetMapping("/api/inventario/get/id/{token}")
    Long findInventarioId(@PathVariable String token);


}
