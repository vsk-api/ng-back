package ru.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.pt.domain.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByIdAndIsDeletedFalse(Long id);

    Optional<Product> findByCodeAndIsDeletedFalse(String code);

    @Query("select p from Product p where p.isDeleted = false order by p.code")
    List<Product> listActive();

    @Query("select p.id, p.lob, p.code, p.name, p.prodVersionNo, p.devVersionNo from Product p where p.isDeleted = false order by p.code")
    List<Object[]> listActiveSummaries();
}

