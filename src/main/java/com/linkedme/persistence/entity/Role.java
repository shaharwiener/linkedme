package com.linkedme.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Entity representing a role in the system.
 *
 * <p>This class is used to define the roles that can be assigned to users in the system,
 * such as "ROLE_USER" and "ROLE_ADMIN". Each role has a unique name and an identifier.</p>
 *
 * <p>The annotations used in this class provide various functionalities like
 * defining it as a database entity, setting access levels, and generating boilerplate code.</p>
 */
@Table(name = "role") // Maps this class to the "role" table in the database
@Entity(name = "Role") // Marks this class as a JPA entity with the name "Role"
@FieldDefaults(level = AccessLevel.PRIVATE) // Sets all fields to private by default
@AllArgsConstructor // Generates a constructor with all fields as parameters
@NoArgsConstructor // Generates a no-argument constructor
@Builder // Enables the builder pattern for creating instances
@Getter // Generates getter methods for all fields
@Setter // Generates setter methods for all fields
public class Role {

    /**
     * Constant for the "USER" role name.
     */
    public static final String ROLE_USER = "ROLE_USER";

    /**
     * Constant for the "ADMIN" role name.
     */
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    /**
     * Unique identifier for the role.
     *
     * <p>Annotations:</p>
     * <ul>
     *     <li>@Id: Marks this field as the primary key of the entity.</li>
     *     <li>@GeneratedValue: Specifies that the value will be generated automatically.</li>
     *     <li>@Column: Maps this field to the "id_role" column in the database.</li>
     * </ul>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_role")
    Long id;

    /**
     * Name of the role.
     *
     * <p>Annotations:</p>
     * <ul>
     *     <li>@Column: Maps this field to the "des_name" column in the database.</li>
     *     <li>nullable = false: Ensures this column cannot be null.</li>
     *     <li>unique = true: Ensures this column has unique values.</li>
     * </ul>
     */
    @Column(name = "des_name", nullable = false, unique = true)
    String name;
}