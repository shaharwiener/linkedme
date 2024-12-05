package com.linkedme.persistence.repository;

import com.linkedme.persistence.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing {@link User} entities.
 *
 * <p>This interface extends {@link JpaRepository}, which provides basic CRUD (Create, Read, Update, Delete)
 * operations and query execution for the {@link User} entity. It includes custom query methods
 * to handle specific user-related requirements.</p>
 *
 * <p>Annotations:</p>
 * <ul>
 *     <li>@Repository: Marks this interface as a Spring Data Repository, enabling exception translation
 *     and automatic implementation by Spring Data JPA.</li>
 * </ul>
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their email address.
     *
     * <p>This method leverages Spring Data JPA's derived query capabilities to find a user
     * with the specified email address.</p>
     *
     * @param email the email address of the user to find.
     * @return an {@link Optional} containing the user if found, or empty if no user with the specified email exists.
     */
    Optional<User> findByEmail(String email);
}