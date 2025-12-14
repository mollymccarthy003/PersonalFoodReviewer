package org.hungrybadger.persistence;

import org.hibernate.Session;
import org.hungrybadger.entity.Photo;
import org.hungrybadger.entity.Review;

import java.util.List;

/**
 * Data Access Object (DAO) for the {@link Photo} entity.
 * <p>
 * Extends the {@link GenericDao} to provide basic CRUD operations for photos.
 * Additional methods specific to {@link Photo} retrieval can be added here.
 * </p>
 */
public class PhotoDao extends GenericDao<Photo> {

    /**
     * Constructs a {@link PhotoDao} instance for performing database operations
     * on the {@link Photo} entity.
     */
    public PhotoDao() {
        super(Photo.class);
    }

    /**
     * Retrieves all photos associated with a specific review.
     * <p>
     * This method performs a JOIN FETCH on the review and the user to eagerly
     * load related entities, preventing lazy loading issues when accessing them later.
     * </p>
     *
     * @param reviewId the ID of the {@link Review} whose photos should be retrieved
     * @return a list of {@link Photo} entities associated with the given review ID
     */
    public List<Photo> getByReview(int reviewId) {
        List<Photo> photos;
        try (Session session = getSession()) { // try-with-resources auto closes
            photos = session.createQuery(
                            "SELECT p FROM Photo p JOIN FETCH p.review r JOIN FETCH r.user WHERE r.id = :id", Photo.class)
                    .setParameter("id", reviewId)
                    .getResultList();
        }
        return photos;
    }
}