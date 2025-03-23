package com.codeflow.toolflow.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.codeflow.toolflow.persistence.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
