package org.hungrybadger.persistence;

import org.hungrybadger.entity.Review;
import org.hungrybadger.util.Database;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReviewDaoTest {

    ReviewDao reviewDao;

    @BeforeEach
    void setUp() {
        Database database = Database.getInstance();
        database.runSQL("cleanDB.sql");
    }

    @Test
    void getByIdSuccess() {
        reviewDao = new ReviewDao();
        Review retrievedReview;
        retrievedReview = reviewDao.getById(1);
        assertNotNull(retrievedReview);
        assertEquals("Sushi Express", retrievedReview.getRestaurantName());
    }

    @Test
    void updateSuccess() {
        reviewDao = new ReviewDao();
        Review reviewToUpdate = reviewDao.getById(1);
        reviewToUpdate.setRestaurantName("Conrads Wraps");
        reviewDao.update(reviewToUpdate);

        //retrieve user and check that the name change worked
        Review actualReview = reviewDao.getById(1);
        assertEquals("Conrads Wraps", actualReview.getRestaurantName());

    }

    @Test
    void insertSuccess() {
        reviewDao = new ReviewDao();
        Review reviewToInsert = new Review("Madison Chocolate Company", "French", 4,"Worked here for 2 years, gluten free!");
        int insertedUserId = reviewDao.insert(reviewToInsert);
        assertNotEquals(0, insertedUserId);
        Review insertedReview = reviewDao.getById(insertedUserId);
        assertEquals("Madison Chocolate Company", insertedReview.getRestaurantName());


    }

    @Test
    void deleteSuccess() {
        reviewDao = new ReviewDao();
        reviewDao.delete(reviewDao.getById(2));
        assertNull(reviewDao.getById(2));
    }

    @Test
    void getAll() {
        reviewDao = new ReviewDao();
        List<Review> reviews = reviewDao.getAll();
        assertEquals(reviews.size(), 3);
    }

    @Test
    void getByPropertyEqual() {
        reviewDao = new ReviewDao();
        List<Review> reviews = reviewDao.getByPropertyLike("restaurantName", "Sushi Express");
        assertEquals(1, reviews.size());
        assertEquals(1, reviews.get(0).getId());
    }

    @Test
    void getByPropertyLike() {
        reviewDao = new ReviewDao();
        List<Review> reviews = reviewDao.getByPropertyLike("restaurantName", "S");
        assertEquals(3, reviews.size());
    }
}