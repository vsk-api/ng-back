package ru.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.pt.domain.NumberGenerator;

import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface NumberGeneratorRepository extends JpaRepository<NumberGenerator, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select ng from NumberGenerator ng where ng.id = :id")
    Optional<NumberGenerator> findForUpdate(@Param("id") Integer id);

    Optional<NumberGenerator> findByProductCode(String productCode);
}


