package com.microservice.inventario.service;

import com.microservice.inventario.client.AuthenticatedClient;
import com.microservice.inventario.client.AuthenticationTokenResponse;
import com.microservice.inventario.dto.ListProductoDto;
import com.microservice.inventario.dto.ProductoDto;
import com.microservice.inventario.entity.InventarioEntity;
import com.microservice.inventario.entity.ProductoEntity;
import com.microservice.inventario.repository.InventarioRepository;
import com.microservice.inventario.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InventarioAndProductService {


    @Autowired
    private InventarioRepository inventarioRepository;

    @Autowired
    private AuthenticatedClient authenticatedClient;

    @Autowired
    private ProductoRepository productoRepository;


    public String createInventario(String token){
        AuthenticationTokenResponse auth = authenticatedClient.authenticationTokenResponse(token);
        if(auth.getIsValid()){
            InventarioEntity inventarioEntity = InventarioEntity.builder()
                    .userId(auth.getUserId().longValue())
                    .build();
            inventarioRepository.save(inventarioEntity);
        }else{
            throw new RuntimeException();
        }
        return "create Inventario";

    }


    public List<ProductoDto> findAllMyProducts (String authentication){

        String token = authentication.substring(7);
        AuthenticationTokenResponse auth = authenticatedClient.authenticationTokenResponse(token);

        if(auth.getIsValid()){

            InventarioEntity inventarioEntity = inventarioRepository.findByUserId(auth.getUserId().longValue()).get();

            List<ProductoDto> productoDtoList = productoRepository.findByInventarioEntity(inventarioEntity).stream()
                    .map(product -> ProductoDto.builder()
                            .id(product.getId())
                            .name(product.getName())
                            .precioUnitario(product.getPrecioUnitario())
                            .cantidad(product.getCantidad()).build()).toList();

            return productoDtoList;

        }

        throw new RuntimeException();
    }


    public ProductoDto createProduct(String authentication, ProductoDto productoDto){

        String token = authentication.substring(7);
        AuthenticationTokenResponse auth = authenticatedClient.authenticationTokenResponse(token);

        if(auth.getIsValid()){

            InventarioEntity inventarioEntity = inventarioRepository.findByUserId(auth.getUserId().longValue()).get();

            ProductoEntity producto = ProductoEntity.builder()
                    .name(productoDto.getName())
                    .precioUnitario(productoDto.getPrecioUnitario())
                    .cantidad(productoDto.getCantidad())
                    .inventarioEntity(inventarioEntity)
                    .build();

            ProductoEntity producto2 = productoRepository.save(producto);

            return ProductoDto.builder()
                    .id(producto2.getId())
                    .name(producto2.getName())
                    .precioUnitario(producto2.getPrecioUnitario())
                    .cantidad(producto2.getCantidad())
                    .build();

        }

        throw new RuntimeException();

    }

    public String updateProducto(String authentication, ProductoDto productoDto){

        String token = authentication.substring(7);
        AuthenticationTokenResponse auth = authenticatedClient.authenticationTokenResponse(token);

        if(auth.getIsValid()){

            InventarioEntity inventarioEntity = inventarioRepository.findByUserId(auth.getUserId().longValue()).get();
            ProductoEntity producto = productoRepository.findByIdAndInventarioEntityId(productoDto.getId(), inventarioEntity.getId()).get();

            producto.setName(productoDto.getName());
            producto.setPrecioUnitario(productoDto.getPrecioUnitario());
            producto.setCantidad(productoDto.getCantidad());
            productoRepository.save(producto);
        }

        return "Producto Actualizado";

    }

    public String deleteProduct(String authentication, Long productid){

        String token = authentication.substring(7);
        AuthenticationTokenResponse auth = authenticatedClient.authenticationTokenResponse(token);

        if(auth.getIsValid()){

            InventarioEntity inventarioEntity = inventarioRepository.findByUserId(auth.getUserId().longValue()).get();

            ProductoEntity producto = productoRepository.findByIdAndInventarioEntityId(productid, inventarioEntity.getId()).get();

            productoRepository.delete(producto);
        }
        return "Producto Eliminado";
    }

    public ListProductoDto getProducts(ListProductoDto listProductoDto, String token){

        AuthenticationTokenResponse auth = authenticatedClient.authenticationTokenResponse(token);
        List<ProductoEntity> productoEntityList = new ArrayList<>();
        if(auth.getIsValid()){


            InventarioEntity inventarioEntity = inventarioRepository.findByUserId(auth.getUserId().longValue()).get();
            Long inventarioId = inventarioEntity.getId();
            List<ProductoEntity> list = productoRepository.findByInventarioEntity(inventarioEntity);
            List<ProductoDto> productoDtoList = new ArrayList<>();



            for(ProductoEntity productoEntity : list){

                for(ProductoDto productoDTO : listProductoDto.getProductoDtoList()){

                    ///////////////////////////

                    float precioUnitarioProductoEntity = productoEntity.getPrecioUnitario();
                    float precioUnitarioProductoDTO = productoDTO.getPrecioUnitario();

                    if(productoEntity.getId().equals(productoDTO.getId())){
                        if (!productoEntity.getName().equals(productoDTO.getName())  ||
                                precioUnitarioProductoEntity != precioUnitarioProductoDTO) throw new RuntimeException();
                    }

                    ///////////////////////////

                    if(productoDTO.getCantidad() < 1) throw new RuntimeException();
                    if(productoEntity.getId().equals(productoDTO.getId()) && productoEntity.getCantidad() < productoDTO.getCantidad()) throw new RuntimeException();

                    if(productoEntity.getName().equals(productoDTO.getName()) && productoEntity.getCantidad() >= productoDTO.getCantidad() && productoEntity.getId().equals(productoDTO.getId())){

                        productoEntity.setCantidad(productoEntity.getCantidad() - productoDTO.getCantidad());

                      //  productoRepository.save(productoEntity);

                        productoEntityList.add(productoEntity);

                        ProductoDto productoDto = ProductoDto.builder()
                                .id(productoEntity.getId())
                                .name(productoEntity.getName())
                                .precioUnitario(productoEntity.getPrecioUnitario())
                                .cantidad(productoDTO.getCantidad())
                                .build();

                        productoDtoList.add(productoDto);

                    }

                }
            }

            if (productoDtoList.size() == 0) throw new RuntimeException();

            productoRepository.saveAll(productoEntityList);

            listProductoDto.setProductoDtoList(productoDtoList);
            listProductoDto.setInventarioId(inventarioId);

            return listProductoDto;
        }


        return null;

    }

    public ResponseEntity<?> getIdInventario(String token){

        AuthenticationTokenResponse auth = authenticatedClient.authenticationTokenResponse(token);

        if(auth.getIsValid()){

            InventarioEntity inventarioEntity = inventarioRepository.findByUserId(auth.getUserId().longValue()).get();
            Long inventarioId = inventarioEntity.getId();
            return ResponseEntity.ok(inventarioId);

        }
        return new ResponseEntity<>("Acceso denegado", HttpStatus.FORBIDDEN);

    }

    public List<InventarioEntity> findAll(){
        return inventarioRepository.findAll();
    }


}
