package com.linkedme.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Entity representing the relationship between users and roles in the system.
 *
 * <p>This class maps to the "user_role" table in the database and defines the
 * many-to-many relationship between users and roles. Each user can have multiple roles,
 * and each role can be assigned to multiple users.</p>
 *
 * <p>The annotations in this class manage the database mapping, relationship handling,
 * and boilerplate code generation.</p>
 */
@Table(name = "user_role") // Maps this class to the "user_role" table in the database
@Entity(name = "UserRole") // Marks this class as a JPA entity with the name "UserRole"
@FieldDefaults(level = AccessLevel.PRIVATE) // Sets all fields to private by default
@AllArgsConstructor // Generates a constructor with all fields as parameters
@NoArgsConstructor // Generates a no-argument constructor
@Builder // Enables the builder pattern for creating instances
@Getter // Generates getter methods for all fields
@Setter // Generates setter methods for all fields
public class UserRole {

    /**
     * Unique identifier for the user-role relationship.
     *
     * <p>Annotations:</p>
     * <ul>
     *     <li>@Id: Marks this field as the primary key of the entity.</li>
     *     <li>@GeneratedValue: Specifies that the value will be generated automatically.</li>
     *     <li>@Column: Maps this field to the "id_user_role" column in the database.</li>
     * </ul>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user_role")
    Long id;

    /**
     * The user associated with this user-role relationship.
     *
     * <p>Annotations:</p>
     * <ul>
     *     <li>@ManyToOne: Defines a many-to-one relationship with the {@link User} entity.</li>
     *     <li>@JoinColumn: Specifies the foreign key column in the "user_role" table referencing the "user" table.</li>
     *     <li>fetch = FetchType.EAGER: Indicates that the user will be loaded immediately with this relationship.</li>
     *     <li>nullable = false: Ensures that this column cannot be null.</li>
     * </ul>
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_user", nullable = false, referencedColumnName = "id_user")
    User user;

    /**
     * The role associated with this user-role relationship.
     *
     * <p>Annotations:</p>
     * <ul>
     *     <li>@ManyToOne: Defines a many-to-one relationship with the {@link Role} entity.</li>
     *     <li>@JoinColumn: Specifies the foreign key column in the "user_role" table referencing the "role" table.</li>
     *     <li>fetch = FetchType.EAGER: Indicates that the role will be loaded immediately with this relationship.</li>
     *     <li>nullable = false: Ensures that this column cannot be null.</li>
     * </ul>
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_role", nullable = false, referencedColumnName = "id_role")
    Role role;
}
