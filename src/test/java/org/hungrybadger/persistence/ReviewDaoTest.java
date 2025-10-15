package org.hungrybadger.persistence;

import org.hungrybadger.entity.Review;
import org.hungrybadger.util.Database;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReviewDaoTest {

    GenericDao<Review> genericDao;

    @BeforeEach
    void setUp() {
        // Reset the database before each test
        Database database = Database.getInstance();
        database.runSQL("cleanDB.sql");

        // Initialize GenericDao for Review
        genericDao = new GenericDao<>(Review.class);
    }

    @Test
    void getByIdSuccess() {
        Review retrievedReview = genericDao.getById(1);
        assertNotNull(retrievedReview);
        assertEquals("Sushi Express", retrievedReview.getRestaurantName());
    }

    @Test
    void updateSuccess() {
        Review reviewToUpdate = genericDao.getById(1);
        reviewToUpdate.setRestaurantName("Conrads Wraps");
        genericDao.update(reviewToUpdate);

        // Retrieve and check that the name change worked
        Review actualReview = genericDao.getById(1);
        assertEquals("Conrads Wraps", actualReview.getRestaurantName());
    }

    @Test
    void insertSuccess() {
        Review reviewToInsert = new Review(
                "Madison Chocolate Company",
                "French",
                4,
                "Worked here for 2 years, gluten free!"
        );
        int insertedId = genericDao.insert(reviewToInsert);
        assertNotEquals(0, insertedId);

        Review insertedReview = genericDao.getById(insertedId);
        assertEquals("Madison Chocolate Company", insertedReview.getRestaurantName());
    }

    @Test
    void deleteSuccess() {
        Review reviewToDelete = genericDao.getById(2);
        genericDao.delete(reviewToDelete);

        // Deleting and trying to fetch again should return null
        assertNull(genericDao.getById(2));
    }

    @Test
    void getAll() {
        List<Review> reviews = genericDao.getAll();
        assertEquals(3, reviews.size());
    }

    @Test
    void getByPropertyEqual() {
        List<Review> reviews = genericDao.getByPropertyEqual("restaurantName", "Sushi Express");
        assertEquals(1, reviews.size());
        assertEquals(1, reviews.get(0).getId());
    }

    @Test
    void getByPropertyLike() {
        List<Review> reviews = genericDao.getByPropertyLike("restaurantName", "S");
        assertEquals(3, reviews.size());
    }
}