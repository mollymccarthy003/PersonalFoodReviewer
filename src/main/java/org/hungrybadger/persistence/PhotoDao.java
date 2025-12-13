package org.hungrybadger.persistence;

import org.hibernate.Session;
import org.hungrybadger.entity.Photo;
import org.hungrybadger.entity.Review;

import java.util.List;

public class PhotoDao extends GenericDao<Photo> {

    public PhotoDao() {
        super(Photo.class);
    }

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