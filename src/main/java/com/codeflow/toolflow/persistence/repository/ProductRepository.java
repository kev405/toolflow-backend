package com.codeflow.toolflow.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.codeflow.toolflow.persistence.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
