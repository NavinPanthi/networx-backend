package com.networx.networx.jobs;

import com.networx.networx.enums.Level;
import com.networx.networx.enums.Payment;
import com.networx.networx.enums.Type;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, Long> {

    // Find all jobs filtered by conditions
    @Query("""
        SELECT DISTINCT i FROM Job i
        WHERE (:levels IS NULL OR i.level IN :levels)
        AND (:types IS NULL OR i.type IN :types)
        AND (:payments IS NULL OR i.payment IN :payments)
        AND (
            :search IS NULL
            OR LOWER(i.title) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(i.description) LIKE LOWER(CONCAT('%', :search, '%'))
        )
    """)
    Page<Job> findByFilters(
            @Param("levels") List<Level> levels,
            @Param("types") List<Type> types,
            @Param("payments") List<Payment> payments,
            @Param("search") String search,
            Pageable pageable
    );

    // Find all jobs by company
    List<Job> findByUserId(Long companyId);

    // Find jobs by company with filters
    @Query("""
        SELECT DISTINCT i FROM Job i
        WHERE i.user.id = :companyId
        AND (:levels IS NULL OR i.level IN :levels)
        AND (:types IS NULL OR i.type IN :types)
        AND (:payments IS NULL OR i.payment IN :payments)
        AND (
            :search IS NULL
            OR LOWER(i.title) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(i.description) LIKE LOWER(CONCAT('%', :search, '%'))
        )
    """)
    Page<Job> findByCompanyAndFilters(
            @Param("companyId") Long companyId,
            @Param("levels") List<Level> levels,
            @Param("types") List<Type> types,
            @Param("payments") List<Payment> payments,
            @Param("search") String search,
            Pageable pageable
    );

    // Find internship by id and company
    Optional<Job> findByIdAndUserId(Long jobId, Long companyId);
}
