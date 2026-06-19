package com.networx.networx.user;

import com.networx.networx.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    List<User> findByEmailContaining(String email);

    boolean existsByEmail(String email);

    @Query("SELECT DISTINCT u FROM User u " +
            "LEFT JOIN u.roles r " +
            "WHERE (:fullName IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :fullName, '%'))) " +
            "AND (:roles IS NULL OR r IN :roles)")
    Page<User> findByFullNameAndRoles(@Param("fullName") String fullName,
                                      @Param("roles") List<Role> roles, Pageable pageable);
}
