package ru.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.pt.domain.Lob;

import java.util.List;
import java.util.Optional;

public interface LobRepository extends JpaRepository<Lob, Long> {

    Optional<Lob> findByCodeAndIsDeletedFalse(String code);

    @Query("select l from Lob l where l.id = :id and l.isDeleted = false")
    Optional<Lob> findActiveById(@Param("id") Long id);

    @Query("select l.id as id, l.code as code, l.name as name from Lob l where l.isDeleted = false order by l.code")
    List<Object[]> listActiveSummaries();
}


