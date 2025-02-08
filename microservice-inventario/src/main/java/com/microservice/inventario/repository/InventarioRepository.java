package com.microservice.inventario.repository;

import com.microservice.inventario.entity.InventarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventarioRepository extends JpaRepository<InventarioEntity, Long> {

    Optional<InventarioEntity> findByUserId(Long userId);


}
