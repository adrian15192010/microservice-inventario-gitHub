package com.microservice.inventario.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class InventarioEntity {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 @Column(unique = true)
 private Long userId;


 @OneToMany(mappedBy = "inventarioEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
 List<ProductoEntity> productoEntity;



}
