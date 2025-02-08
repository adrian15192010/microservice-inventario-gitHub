package com.microservice.inventario.repository;

import com.microservice.inventario.entity.InventarioEntity;
import com.microservice.inventario.entity.ProductoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<ProductoEntity,Long> {

    List<ProductoEntity> findByInventarioEntity(InventarioEntity inventario);

    Optional<ProductoEntity> findByIdAndInventarioEntityId(Long productoEntityId, Long InventarioEntityId);


}
