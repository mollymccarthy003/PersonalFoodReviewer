package org.hungrybadger.entity;

// Using Jakarta Persistence (jakarta.persistence) for Hibernate 6 compatibility.
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

/**
 * Represents a photo associated with a {@link Review} in the application.
 * <p>
 * This entity maps to the "photo" table in the database and stores the
 * image path and the review it belongs to.
 * </p>
 */
@Entity(name = "Photo")
@Table(name = "photo")
public class Photo {

    /** Unique identifier for the photo, automatically generated. */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private int id;

    /** The {@link Review} entity to which this photo belongs. Cannot be null. */
    @ManyToOne
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    /** File path or name of the image associated with this photo. */
    @Column(name = "image_path")
    private String imagePath;

    /**
     * Default no-argument constructor required by JPA.
     */
    public Photo() {}

    /**
     * Constructs a {@code Photo} with the specified image path and associated review.
     *
     * @param imagePath the path or filename of the image
     * @param review    the {@link Review} this photo belongs to
     */
    public Photo(String imagePath, Review review) {
        this.imagePath = imagePath;
        this.review = review;
    }

    // --- Getters and setters ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Review getReview() { return review; }
    public void setReview(Review review) { this.review = review; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    /**
     * Returns a string representation of the photo entity.
     *
     * @return a string containing the photo ID, review, and image path
     */
    @Override
    public String toString() {
        return "Photo{" +
                "id=" + id +
                ", review=" + review +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }

}
