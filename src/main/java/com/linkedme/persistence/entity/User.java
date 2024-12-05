package com.linkedme.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * Entity representing a user in the system.
 *
 * <p>This class maps to the "user" table in the database and defines the structure of user data.
 * Each user has an ID, name, email, and a list of roles.</p>
 *
 * <p>The annotations used in this class simplify the integration with the database
 * and automatically generate boilerplate code like getters, setters, and constructors.</p>
 */
@Table(name = "user") // Maps this class to the "user" table in the database
@Entity(name = "User") // Marks this class as a JPA entity with the name "User"
@Builder // Enables the builder pattern for creating instances
@AllArgsConstructor // Generates a constructor with all fields as parameters
@NoArgsConstructor // Generates a no-argument constructor
@FieldDefaults(level = PRIVATE) // Sets all fields to private by default
@Getter // Generates getter methods for all fields
@Setter // Generates setter methods for all fields
public class User {

    /**
     * Unique identifier for the user.
     *
     * <p>Annotations:</p>
     * <ul>
     *     <li>@Id: Marks this field as the primary key of the entity.</li>
     *     <li>@GeneratedValue: Specifies that the value will be generated automatically.</li>
     *     <li>@Column: Maps this field to the "id_user" column in the database.</li>
     * </ul>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    Long id;

    /**
     * Name of the user.
     *
     * <p>Annotations:</p>
     * <ul>
     *     <li>@Column: Maps this field to the "des_name" column in the database.</li>
     *     <li>nullable = false: Ensures this column cannot be null.</li>
     * </ul>
     */
    @Column(name = "des_name", nullable = false)
    String name;

    /**
     * Email of the user.
     *
     * <p>Annotations:</p>
     * <ul>
     *     <li>@Column: Maps this field to the "des_email" column in the database.</li>
     *     <li>nullable = false: Ensures this column cannot be null.</li>
     * </ul>
     */
    @Column(name = "des_email", nullable = false)
    String email;

    /**
     * List of roles assigned to the user.
     *
     * <p>Annotations:</p>
     * <ul>
     *     <li>@OneToMany: Defines a one-to-many relationship between users and roles.</li>
     *     <li>fetch = FetchType.EAGER: Indicates that roles will be loaded immediately with the user (not recommended for large data sets; use LAZY instead).</li>
     *     <li>cascade = CascadeType.ALL: Any operation on the user (e.g., save, delete) will also be applied to their roles.</li>
     * </ul>
     */
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    List<UserRole> roles;
}
