package org.hungrybadger.entity;

// Using Jakarta Persistence (jakarta.persistence) for Hibernate 6 compatibility.
import jakarta.persistence.*;

import java.util.List;

/**
 * Represents a user of the application.
 * <p>
 * Each user has a unique Cognito ID, full name, and email address.
 * A user can create multiple {@link Review} entities.
 * </p>
 */
@Entity(name = "User")
@Table(name = "user")
public class User {

    /** Unique identifier for the user, automatically generated. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /** Cognito unique identifier for the user, required and unique. */
    @Column(name = "cognito_sub", nullable = false, unique = true)
    private String cognitoSub;

    /** Full name of the user, required. */
    @Column(name = "full_name", nullable = false)
    private String fullName;

    /** Email address of the user, required and unique. */
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    /**
     * List of reviews created by the user.
     * <p>
     * One user can have many reviews. Cascade operations ensure that
     * deleting a user will also delete all associated reviews.
     * Orphan removal is enabled to remove reviews that are no longer associated.
     * </p>
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;

    /**
     * Default no-argument constructor required by JPA.
     */
    public User() {}

    // Getters and  Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCognitoSub() { return cognitoSub; }
    public void setCognitoSub(String cognitoSub) { this.cognitoSub = cognitoSub; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<Review> getReviews() { return reviews; }
    public void setReviews(List<Review> reviews) { this.reviews = reviews; }
}
