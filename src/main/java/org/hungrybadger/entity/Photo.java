package org.hungrybadger.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

@Entity(name = "Photo")
@Table(name = "photo")
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private int id;

    @ManyToOne
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @Column(name = "image_path")
    private String imagePath;

    public Photo() {}

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

    @Override
    public String toString() {
        return "Photo{" +
                "id=" + id +
                ", imagePath='" + imagePath + '\'' +
                ", reviewId=" + (review != null ? review.getId() : null) +
                '}';
    }
}
