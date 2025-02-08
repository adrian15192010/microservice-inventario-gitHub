package com.microservice.authservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "msvc-inventario", url = "localhost:8040")
public interface InventarioClient {

    @PostMapping("/api/inventario/create/{token}")
    String createInventario(@PathVariable String token);

}
