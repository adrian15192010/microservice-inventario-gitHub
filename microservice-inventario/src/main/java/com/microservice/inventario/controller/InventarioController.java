package com.microservice.inventario.controller;

import com.microservice.inventario.dto.ListProductoDto;
import com.microservice.inventario.dto.ProductoDto;
import com.microservice.inventario.service.InventarioAndProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    @Autowired
    private InventarioAndProductService inventarioService;

    @PostMapping("/create/{token}")
    public ResponseEntity<String> createInventario(@PathVariable String token){ //crear inevntario
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inventarioService.createInventario(token));
    }

    @GetMapping("/all")
    public ResponseEntity<?> findAll(){return ResponseEntity.ok(inventarioService.findAll());}

    @GetMapping("/product/all")
    public ResponseEntity<?> findAllMyProducts(@RequestHeader(HttpHeaders.AUTHORIZATION) final String authentication){
       return ResponseEntity.ok(inventarioService.findAllMyProducts(authentication));
    }

    @PostMapping("/product/create")
    public ResponseEntity<?> createProduct(@RequestHeader(HttpHeaders.AUTHORIZATION) final String authentication, @RequestBody ProductoDto productoDto){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inventarioService.createProduct(authentication, productoDto));
    }

    @PutMapping("/product/update")
    public ResponseEntity<String> updateProduct(@RequestHeader(HttpHeaders.AUTHORIZATION) final String authentication, @RequestBody ProductoDto productoDto){
        return ResponseEntity.ok(inventarioService.updateProducto(authentication, productoDto));
    }

    @DeleteMapping("/product/delete/{productid}")
    public ResponseEntity<String> deleteProduct(@RequestHeader(HttpHeaders.AUTHORIZATION) final String authentication,@PathVariable Long productid){
        return ResponseEntity.ok(inventarioService.deleteProduct(authentication, productid));
    }

    @PostMapping("/product/venta/{token}")
    public ResponseEntity<ListProductoDto> getProducts(@RequestBody ListProductoDto listProductoDto,@PathVariable String token){
        return ResponseEntity.ok(inventarioService.getProducts(listProductoDto, token));
    }

    @GetMapping("/get/id/{token}")
    public ResponseEntity<?> getIdInventario(@PathVariable String token){
        return inventarioService.getIdInventario(token);
    }

}
