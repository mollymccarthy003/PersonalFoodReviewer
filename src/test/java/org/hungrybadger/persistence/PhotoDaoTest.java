package org.hungrybadger.persistence;

import org.hungrybadger.entity.Photo;
import org.hungrybadger.entity.Review;
import org.hungrybadger.util.Database;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PhotoDaoTest {

    GenericDao<Photo> photoDao;
    GenericDao<Review> reviewDao;

    @BeforeEach
    void setUp() {
        // Reset the database before each test
        Database database = Database.getInstance();
        database.runSQL("cleanDB.sql");

        // Initialize GenericDao for Photo and Review
        photoDao = new GenericDao<>(Photo.class);
        reviewDao = new GenericDao<>(Review.class);
    }

    @Test
    void getByIdSuccess() {
        Photo retrievedPhoto = photoDao.getById(1);
        assertNotNull(retrievedPhoto);
        assertEquals("images/sushi_express_rolls.jpg", retrievedPhoto.getImagePath());
    }

    @Test
    void updateSuccess() {
        Photo photoToUpdate = photoDao.getById(1);
        photoToUpdate.setImagePath("/images/sushi_updated.jpg");
        photoDao.update(photoToUpdate);

        Photo actualPhoto = photoDao.getById(1);
        assertEquals("/images/sushi_updated.jpg", actualPhoto.getImagePath());
    }

    @Test
    void insertSuccess() {
        Review review = reviewDao.getById(1);
        assertNotNull(review, "Review must exist to associate with Photo");

        Photo photoToInsert = new Photo("images/new_photo.jpg", review);
        int insertedId = photoDao.insert(photoToInsert);
        assertNotEquals(0, insertedId);

        Photo insertedPhoto = photoDao.getById(insertedId);
        assertEquals("images/new_photo.jpg", insertedPhoto.getImagePath());
        assertEquals(review.getId(), insertedPhoto.getReview().getId());
    }

    @Test
    void deleteSuccess() {
        Photo photoToDelete = photoDao.getById(2);
        photoDao.delete(photoToDelete);

        assertNull(photoDao.getById(2));
    }

    @Test
    void getAll() {
        List<Photo> photos = photoDao.getAll();
        assertEquals(4, photos.size());
    }

    @Test
    void getByPropertyEqual() {
        List<Photo> photos = photoDao.getByPropertyEqual("imagePath", "images/conrads_wrap.jpg");
        assertEquals(1, photos.size());
        assertEquals(2, photos.get(0).getId());
    }

    @Test
    void getByPropertyLike() {
        List<Photo> photos = photoDao.getByPropertyLike("imagePath", "ians");
        assertEquals(2, photos.size());
    }

    @Test
    void deleteReviewCascadesToPhotos() {
        // Fetch review with photos
        Review review = reviewDao.getById(3);
        List<Photo> photos = review.getPhotos();
        assertTrue(photos.size() >= 2, "Review should have at least 2 photos for this test");

        int photoId1 = photos.get(0).getId();
        int photoId2 = photos.get(1).getId();

        // Delete the review
        reviewDao.delete(review);

        // Review should be gone
        assertNull(reviewDao.getById(3));

        // Associated photos should also be gone (cascade delete)
        assertNull(photoDao.getById(photoId1));
        assertNull(photoDao.getById(photoId2));
    }
}