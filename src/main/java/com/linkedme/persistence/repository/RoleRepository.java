package com.linkedme.persistence.repository;

import com.linkedme.persistence.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing {@link Role} entities.
 *
 * <p>This interface extends {@link JpaRepository}, which provides basic CRUD (Create, Read, Update, Delete)
 * operations and query execution for the {@link Role} entity. It includes custom query methods
 * to handle additional requirements.</p>
 *
 * <p>Annotations:</p>
 * <ul>
 *     <li>@Repository: Marks this interface as a Spring Data Repository, enabling exception translation
 *     and automatic implementation by Spring Data JPA.</li>
 * </ul>
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Finds a role by its name.
     *
     * <p>This method leverages Spring Data JPA's derived query capabilities to find a role
     * with the specified name.</p>
     *
     * @param name the name of the role to find.
     * @return an {@link Optional} containing the role if found, or empty if no role with the specified name exists.
     */
    Optional<Role> findByName(String name);

    /**
     * Finds roles by their names using a custom JPQL query.
     *
     * <p>This method uses the @Query annotation to define a JPQL query that retrieves all roles
     * whose names match the provided list.</p>
     *
     * @param names a list of role names to search for.
     * @return a list of {@link Role} entities whose names match the provided list.
     */
    @Query("SELECT r FROM Role r WHERE r.name IN :names")
    List<Role> findByNames(List<String> names);
}