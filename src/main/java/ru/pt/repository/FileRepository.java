package ru.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.pt.domain.FileEntity;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<FileEntity, Long> {

    @Query("select f from FileEntity f where f.id = :id and f.deleted = false")
    Optional<FileEntity> findActiveById(@Param("id") Long id);

    @Query("select f from FileEntity f where f.productCode = :productCode and f.fileType = :fileType and f.deleted = false")
    FileEntity findActiveByProductandFileType(@Param("productCode") String productCode, @Param("fileType") String fileType);

    @Query("select f.id, f.fileType, f.fileDesc, f.productCode from FileEntity f where f.deleted = false and (:productCode is null or f.productCode = :productCode) order by f.id")
    List<Object[]> listSummaries(@Param("productCode") String productCode);
}


