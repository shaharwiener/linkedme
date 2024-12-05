package com.linkedme.persistence.repository;

import com.linkedme.persistence.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String nome);

    @Query("SELECT r FROM Role r WHERE r.name IN :names")
    List<Role> findByNames(List<String> names);
}
