package org.hungrybadger.entity;

// Using Jakarta Persistence (jakarta.persistence) for Hibernate 6 compatibility.
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;

@Entity(name = "Review")
@Table(name = "review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private int id;

    @Column(name = "restaurant_name")
    private String restaurantName;

    @Column(name = "cuisine_type")
    private String cuisineType;

    @Column(name = "personal_rating")
    private int personalRating;

    @Column(name = "personal_notes")
    private String personalNotes;

    /**
     * Each review belongs to one user.
     * The user_id column will be the foreign key.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Bidirectional mapping: one review can have many photos.
     * Deleting a review cascades to its photos.
     */
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true,  fetch = FetchType.EAGER)
    private List<Photo> photos;

    public Review() {}

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
    public void addPhoto(Photo photo) {
        photos.add(photo);
        photo.setReview(this);
    }

    public void removePhoto(Photo photo) {
        photos.remove(photo);
        photo.setReview(null);
    }

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