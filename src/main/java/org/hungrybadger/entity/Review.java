package org.hungrybadger.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

/**
 * A class to represent a review.
 *
 * @author M. McCarthy
 */
public class Review {

    private String restaurantName;
    private String cuisineType;
    private int personalRating;
    private String personalNotes;
    private int id;

    /**
     * Default constructor for Review.
     */
    public Review() {
    }

    /**
     * Instantiates a new Review.
     *
     * @param restaurantName name of the restaurant you are reviewing
     * @param cuisineType type of cuisine
     * @param personalRating personal rating on a 1-5 scale
     * @param personalNotes personal notes about your experience
     */
    public Review(String restaurantName, String cuisineType, int personalRating, String personalNotes) {
        this.restaurantName = restaurantName;
        this.cuisineType = cuisineType;
        this.personalRating = personalRating;
        this.personalNotes = personalNotes;
    }

    /**
     * Gets the restaurant name.
     *
     * @return the restaurant name
     */
    public String getRestaurantName() {
        return restaurantName;
    }

    /**
     * Sets the restaurant name.
     *
     * @param restaurantName the restaurant name to set
     */
    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    /**
     * Gets the cuisine type.
     *
     * @return the cuisine type
     */
    public String getCuisineType() {
        return cuisineType;
    }

    /**
     * Sets the cuisine type.
     *
     * @param cuisineType the cuisine type to set
     */
    public void setCuisineType(String cuisineType) {
        this.cuisineType = cuisineType;
    }

    /**
     * Gets the personal rating.
     *
     * @return the personal rating
     */
    public int getPersonalRating() {
        return personalRating;
    }

    /**
     * Sets the personal rating.
     *
     * @param personalRating the personal rating to set
     */
    public void setPersonalRating(int personalRating) {
        this.personalRating = personalRating;
    }

    /**
     * Gets the personal notes.
     *
     * @return the personal notes
     */
    public String getPersonalNotes() {
        return personalNotes;
    }

    /**
     * Sets the personal notes.
     *
     * @param personalNotes the personal notes to set
     */
    public void setPersonalNotes(String personalNotes) {
        this.personalNotes = personalNotes;
    }

    /**
     * Gets the ID of the review.
     *
     * @return the ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the ID of the review.
     *
     * @param id the ID to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns a string representation of the review.
     *
     * @return a string containing all review details
     */
    @Override
    public String toString() {
        return "Review{" +
                "restaurantName='" + restaurantName + '\'' +
                ", cuisineType='" + cuisineType + '\'' +
                ", personalRating=" + personalRating +
                ", personalNotes='" + personalNotes + '\'' +
                ", id=" + id +
                '}';
    }
}