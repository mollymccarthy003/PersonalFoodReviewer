package org.hungrybadger.entity;

// Using Jakarta Persistence (jakarta.persistence) for Hibernate 6 compatibility.
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;

/**
 * Represents a restaurant review made by a {@link User}.
 * <p>
 * Each review contains details about the restaurant, cuisine, personal rating,
 * and personal notes. It is associated with one {@link User} and can have
 * multiple {@link Photo} entities representing images for the review.
 * </p>
 */
@Entity(name = "Review")
@Table(name = "review")
public class Review {

    /** Unique identifier for the review, automatically generated. */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private int id;

    /** Name of the restaurant being reviewed. */
    @Column(name = "restaurant_name")
    private String restaurantName;


    /** Type of cuisine for the restaurant. */
    @Column(name = "cuisine_type")
    private String cuisineType;


    /** Personal rating given by the user (e.g., 1–5). */
    @Column(name = "personal_rating")
    private int personalRating;

    /** Optional personal notes written by the user about the review. */
    @Column(name = "personal_notes")
    private String personalNotes;

    /**
     * The {@link User} who created this review.
     * <p>
     * This is a many-to-one relationship; a user can have many reviews.
     * The {@code user_id} column is a foreign key to the {@link User} table.
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * The list of {@link Photo} objects associated with this review.
     * <p>
     * One review can have many photos. Cascade operations ensure that
     * deleting a review will also delete its photos. Orphan removal
     * is enabled to remove photos that are no longer associated.
     * </p>
     */
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true,  fetch = FetchType.EAGER)
    private List<Photo> photos;

    /**
     * Default no-argument constructor required by JPA.
     */
    public Review() {}

    /**
     * Constructs a {@code Review} with all main attributes.
     *
     * @param restaurantName the restaurant's name
     * @param cuisineType the type of cuisine
     * @param personalRating the rating given by the user
     * @param personalNotes personal notes for the review
     * @param user the {@link User} who created the review
     */
    public Review(String restaurantName, String cuisineType, int personalRating, String personalNotes, User user) {
        this.restaurantName = restaurantName;
        this.cuisineType = cuisineType;
        this.personalRating = personalRating;
        this.personalNotes = personalNotes;
        this.user = user;
    }

    // --- Getters and setters ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getRestaurantName() { return restaurantName; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }

    public String getCuisineType() { return cuisineType; }
    public void setCuisineType(String cuisineType) { this.cuisineType = cuisineType; }

    public int getPersonalRating() { return personalRating; }
    public void setPersonalRating(int personalRating) { this.personalRating = personalRating; }

    public String getPersonalNotes() { return personalNotes; }
    public void setPersonalNotes(String personalNotes) { this.personalNotes = personalNotes; }

    public List<Photo> getPhotos() { return photos; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    // Helper methods to manage bidirectional relationship

    /**
     * Adds a photo to this review and sets the review reference in the photo.
     *
     * @param photo the {@link Photo} to add
     */
    public void addPhoto(Photo photo) {
        photos.add(photo);
        photo.setReview(this);
    }

    /**
     * Removes a photo from this review and clears the review reference in the photo.
     *
     * @param photo the {@link Photo} to remove
     */
    public void removePhoto(Photo photo) {
        photos.remove(photo);
        photo.setReview(null);
    }

    /**
     * Returns a string representation of the review entity.
     *
     * @return a string containing review ID, restaurant, cuisine, rating, notes, user, and photo count
     */
    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", restaurantName='" + restaurantName + '\'' +
                ", cuisineType='" + cuisineType + '\'' +
                ", personalRating=" + personalRating +
                ", personalNotes='" + personalNotes + '\'' +
                ", user=" + (user != null ? user.getFullName() : "null") +
                ", photoCount=" + photos.size() +
                '}';
    }
}