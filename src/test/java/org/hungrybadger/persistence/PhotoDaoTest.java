package org.hungrybadger.persistence;

import org.hungrybadger.entity.Photo;
import org.hungrybadger.entity.Review;
import org.hungrybadger.util.Database;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link GenericDao} operations on {@link Photo} entities.
 * <p>
 * This test class uses JUnit 5 to verify CRUD functionality and query methods:
 * <ul>
 *     <li>Retrieve a photo by ID</li>
 *     <li>Retrieve all photos</li>
 *     <li>Query photos by property equality</li>
 *     <li>Query photos by property pattern (LIKE)</li>
 *     <li>Insert new photos</li>
 *     <li>Update existing photos</li>
 *     <li>Delete photos</li>
 *     <li>Cascade deletion from associated {@link Review}</li>
 * </ul>
 * Each test resets the database to a known state using {@link Database#runSQL(String)}.
 * </p>
 */
class PhotoDaoTest {

    /**
     * GenericDao instance for Photo entity operations.
     */
    GenericDao<Photo> photoDao;

    /**
     * GenericDao instance for Review entity operations.
     */
    GenericDao<Review> reviewDao;


    /**
     * Runs before each test to reset the database and initialize the DAOs.
     */
    @BeforeEach
    void setUp() {
        Database database = Database.getInstance();
        database.runSQL("cleanDB.sql");

        photoDao = new GenericDao<>(Photo.class);
        reviewDao = new GenericDao<>(Review.class);
    }

    /**
     * Tests retrieving a photo by ID.
     * Asserts that the photo exists and has the expected image path.
     */
    @Test
    void getByIdSuccess() {
        Photo retrievedPhoto = photoDao.getById(1);
        assertNotNull(retrievedPhoto);
        assertEquals("images/sushi_express_rolls.jpg", retrievedPhoto.getImagePath());
    }

    /**
     * Tests updating a photo's image path.
     * Validates that the update persists in the database.
     */
    @Test
    void updateSuccess() {
        Photo photoToUpdate = photoDao.getById(1);
        photoToUpdate.setImagePath("/images/sushi_updated.jpg");
        photoDao.update(photoToUpdate);

        Photo actualPhoto = photoDao.getById(1);
        assertEquals("/images/sushi_updated.jpg", actualPhoto.getImagePath());
    }

    /**
     * Tests inserting a new photo associated with an existing review.
     * Validates that the photo is correctly stored and linked to the review.
     */
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

    /**
     * Tests deleting a photo by ID.
     * Validates that the photo no longer exists after deletion.
     */
    @Test
    void deleteSuccess() {
        Photo photoToDelete = photoDao.getById(2);
        photoDao.delete(photoToDelete);

        assertNull(photoDao.getById(2));
    }

    /**
     * Tests retrieving all photos.
     * Validates the total count matches the expected number of rows in the database.
     */
    @Test
    void getAll() {
        List<Photo> photos = photoDao.getAll();
        assertEquals(4, photos.size());
    }

    /**
     * Tests querying photos by property equality (image path).
     * Validates that the correct photo is returned.
     */
    @Test
    void getByPropertyEqual() {
        List<Photo> photos = photoDao.getByPropertyEqual("imagePath", "images/conrads_wrap.jpg");
        assertEquals(1, photos.size());
        assertEquals(2, photos.get(0).getId());
    }


    /**
     * Tests querying photos by property pattern (LIKE) using image path.
     * Validates that all matching photos are returned.
     */
    @Test
    void getByPropertyLike() {
        List<Photo> photos = photoDao.getByPropertyLike("imagePath", "ians");
        assertEquals(2, photos.size());
    }

    /**
     * Tests that deleting a review cascades to delete all associated photos.
     * Validates that the photos no longer exist after their parent review is deleted.
     */
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