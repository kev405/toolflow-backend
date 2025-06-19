package com.codeflow.toolflow.persistence.headquarter.respository;

import com.codeflow.toolflow.persistence.headquarter.entity.Headquarter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HeadquarterRepository extends JpaRepository<Headquarter, Long> {
    Page<Headquarter> findAllByStatusTrue(Pageable pageable);

    Optional<Headquarter> findByMainTrue();
}
