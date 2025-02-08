package com.microservice.venta.controller;

import com.microservice.venta.dto.ListProductoDto;
import com.microservice.venta.dto.VentaDto;
import com.microservice.venta.service.VentaService;
import net.sf.jasperreports.engine.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/venta")
public class VentaController {


@Autowired
VentaService ventaService;


@PostMapping("/create")
public ResponseEntity<VentaDto> create(@RequestBody ListProductoDto listProductoDto, @RequestHeader(HttpHeaders.AUTHORIZATION) final String authentication) throws JRException {
    return ResponseEntity.ok(ventaService.create(listProductoDto, authentication));
}

@GetMapping("/{number}")
public ResponseEntity<?> findVenta(@PathVariable Long number, @RequestHeader(HttpHeaders.AUTHORIZATION) final String authentication){
    return ResponseEntity.ok(ventaService.findVenta(number, authentication));
}

@GetMapping("/pdf/{number}")
public ResponseEntity<FileSystemResource> findFilePdf(@PathVariable Long number, @RequestHeader(HttpHeaders.AUTHORIZATION) final String authentication){
    return ventaService.findFilePdf(number, authentication);
}

@GetMapping("/all")
public ResponseEntity<?> findAll(@RequestHeader(HttpHeaders.AUTHORIZATION) final String authentication){
    return ventaService.findAll(authentication);
}


}
