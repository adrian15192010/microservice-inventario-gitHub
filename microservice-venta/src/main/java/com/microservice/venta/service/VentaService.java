package com.microservice.venta.service;

import com.microservice.venta.client.InventarioClient;
import com.microservice.venta.dto.ListProductoDto;
import com.microservice.venta.dto.ProductoDto;
import com.microservice.venta.dto.ProductoVentaDto;
import com.microservice.venta.dto.VentaDto;
import com.microservice.venta.entity.ProductoVenta;
import com.microservice.venta.entity.Venta;
import com.microservice.venta.repository.ProductoVentaRepository;
import com.microservice.venta.repository.VentaRepository;
import net.sf.jasperreports.engine.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import java.util.Map;

@Service
public class VentaService {

    @Autowired
    InventarioClient inventarioClient;

    @Autowired
    VentaRepository ventaRepository;

    @Autowired
    ProductoVentaRepository productoVentaRepository;




    public VentaDto create (ListProductoDto listProductoDto, String authentication) throws JRException {

        if(listProductoDto.getProductoDtoList().size() > 3) throw new RuntimeException();

        String token = authentication.substring(7);
        ListProductoDto listProductoDtoVenta = inventarioClient.getProducts(listProductoDto,token);

        Float montaTotalVenta = 0F;

        for(ProductoDto productoDto : listProductoDtoVenta.getProductoDtoList()){

            Float suma = productoDto.getCantidad() * productoDto.getPrecioUnitario();

            montaTotalVenta+= suma;

        }

        Float iva = montaTotalVenta * 0.16F;

        Venta venta = Venta.builder()
                .total(montaTotalVenta)
                .iva(iva)
                .totalConIVA(montaTotalVenta + iva)
                .inventarioId(listProductoDtoVenta.getInventarioId())
                .build();

        Venta venta2 = ventaRepository.save(venta);

        //////////////////////////////////////////////////////////////////////////////////////////////

        Long id = venta2.getId();
        String archivoName = "reporte_"+id+".pdf";

        String ruta = "microservice-venta" + File.separator;

        String destinationPath = ruta + "src" +
                File.separator +
                "main" +
                File.separator +
                "resources" +
                File.separator +
                "static" +
                File.separator +
                archivoName;

        String filePath = "src" +
                File.separator +
                "main" +
                File.separator +
                "resources" +
                File.separator +
                "templates" +
                File.separator +
                "report" + File.separator + "Report.jrxml";

        Map<String, Object> parameters = new HashMap<>();
        int iterador = 1;

        for(ProductoDto productoDto : listProductoDtoVenta.getProductoDtoList()){

            Float montoTotal = productoDto.getCantidad() * productoDto.getPrecioUnitario();

            ProductoVenta productoVenta = ProductoVenta.builder()
                    .name(productoDto.getName())
                    .precioUnitario(productoDto.getPrecioUnitario())
                    .cantidad(productoDto.getCantidad())
                    .montoTotal(montoTotal)
                    .venta(venta2)
                    .build();

            productoVentaRepository.save(productoVenta);

            parameters.put("nombre_"+iterador, productoDto.getName());
            parameters.put("cantidad_"+iterador, productoDto.getCantidad());
            parameters.put("precioUnitario_"+iterador, productoDto.getPrecioUnitario());
            parameters.put("montoTotal_"+iterador, montoTotal);
            iterador++;

        }

        parameters.put("total", montaTotalVenta);
        parameters.put("iva", iva);
        parameters.put("totalConIVA", montaTotalVenta + iva);
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("templates/report/Report.jrxml");
        JasperReport report = JasperCompileManager.compileReport(inputStream);
        JasperPrint print = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());
        try{
           // JasperExportManager.exportReportToPdfFile(print, destinationPath);
            System.out.println("Report Created Succesfully");

        } catch (RuntimeException e) {


        }

        //////////////////////////////////////////////////////////////////////////////////////////////


        return VentaDto.builder()
                .Id(venta2.getId())
                .total(venta2.getTotal())
                .iva(venta2.getIva())
                .totalConIVA(venta2.getTotalConIVA())
                .inventarioId(venta2.getInventarioId())
                .build();


    }

    public VentaDto findVenta(Long ventaid, String authentication){

        String token = authentication.substring(7);
        Long inventarioId = inventarioClient.findInventarioId(token);
        Venta venta = ventaRepository.findById(ventaid).get();

        if(venta.getInventarioId() != inventarioId) throw new RuntimeException();


        return VentaDto.builder()
                .Id(venta.getId())
                .total(venta.getTotal())
                .iva(venta.getIva())
                .totalConIVA(venta.getTotalConIVA())
                .inventarioId(venta.getInventarioId())
                .productoVentaList(
                        venta.getProductoVentaList().stream()
                                .map(productoVenta -> ProductoVentaDto.builder()
                                        .Id(productoVenta.getId())
                                        .name(productoVenta.getName())
                                        .precioUnitario(productoVenta.getPrecioUnitario())
                                        .cantidad(productoVenta.getCantidad())
                                        .montoTotal(productoVenta.getMontoTotal())
                                        .build())
                                .toList()
                ).build();
    }


    public ResponseEntity<FileSystemResource> findFilePdf(Long number, String authentication) throws JRException {

        String token = authentication.substring(7);

        Long inventarioId = inventarioClient.findInventarioId(token);
        Venta venta = ventaRepository.findById(number).get();

        if(venta.getInventarioId() != inventarioId) return ResponseEntity.notFound().build();

        String filename = "reporte_"+venta.getId()+".pdf";
        Path filePath = Paths.get("microservice-venta/src/main/resources/static/").resolve(filename).normalize();
        FileSystemResource imgFile = new FileSystemResource(filePath.toFile());

        if (imgFile.exists() || imgFile.isReadable()) {
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + imgFile.getFilename() + "\"")
                    .body(imgFile);
        }else {

            Long id = number;
            String archivoName = "reporte_"+id+".pdf";
            String ruta = "microservice-venta" + File.separator;

            String destinationPath = ruta + "src" +
                    File.separator +
                    "main" +
                    File.separator +
                    "resources" +
                    File.separator +
                    "static" +
                    File.separator +
                    archivoName;


            Map<String, Object> parameters = new HashMap<>();
            int iterador = 1;
            for (ProductoVenta productoVenta : venta.getProductoVentaList()){

                parameters.put("nombre_"+iterador, productoVenta.getName());
                parameters.put("cantidad_"+iterador, productoVenta.getCantidad());
                parameters.put("precioUnitario_"+iterador, productoVenta.getPrecioUnitario());
                parameters.put("montoTotal_"+iterador, productoVenta.getMontoTotal());
                iterador++;

            }

            parameters.put("total", venta.getTotal());
            parameters.put("iva", venta.getIva());
            parameters.put("totalConIVA", venta.getTotalConIVA());
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("templates/report/Report.jrxml");
            JasperReport report = JasperCompileManager.compileReport(inputStream);
            JasperPrint print = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());
            JasperExportManager.exportReportToPdfFile(print, destinationPath);
            System.out.println("Report Created Succesfully");

        }

        throw new RuntimeException("vuelve a intentarlo");

    }

    public ResponseEntity<?> findAll(String authentication){
        String token = authentication.substring(7);

        try {
            Long inventarioId = inventarioClient.findInventarioId(token);

            return ResponseEntity.ok(ventaRepository.findByInventarioId(inventarioId)) ;

        }catch (Exception exception){

            return new ResponseEntity<>("Acceso denegado", HttpStatus.FORBIDDEN);
        }
    }



}
