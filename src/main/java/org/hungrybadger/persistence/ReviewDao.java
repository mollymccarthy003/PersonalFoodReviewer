package org.hungrybadger.persistence;

import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hungrybadger.entity.Review;

import java.util.List;

public class ReviewDao {

    private final Logger logger = LogManager.getLogger(this.getClass());
    SessionFactory sessionFactory = SessionFactoryProvider.getSessionFactory();

    /**
     * Get user by id
     */
    public Review getById(int id) {
        Session session = sessionFactory.openSession();
        Review review = session.get(Review.class, id);
        session.close();
        return review;
    }

    /**
     * update review
     * @param review review to be updated
     */
    public void update(Review review) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.merge(review);
        transaction.commit();
        session.close();
    }

    /**
     * insert a new review
     * @param review  Review to be inserted
     */
    public int insert(Review review) {
        int id = 0;
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.persist(review);
        transaction.commit();
        id = review.getId();
        session.close();
        return id;
    }

    /**
     * Delete a review
     * @param review Review to be deleted
     */
    public void delete(Review review) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.delete(review);
        transaction.commit();
        session.close();
    }


    /** Return a list of all reviews
     *
     * @return All reviews
     */
    public List<Review> getAll() {

        Session session = sessionFactory.openSession();

        HibernateCriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Review> query = builder.createQuery(Review.class);
        Root<Review> root = query.from(Review.class);
        List<Review> reviews = session.createSelectionQuery( query ).getResultList();

        logger.debug("The list of users " + reviews);
        session.close();

        return reviews;
    }

    /**
     * Get review by property (exact match)
     * sample usage: getByPropertyEqual("restaurantName", "Sushi Express")
     */
    public List<Review> getByPropertyEqual(String propertyName, String value) {
        Session session = sessionFactory.openSession();

        logger.debug("Searching for user with " + propertyName + " = " + value);

        HibernateCriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Review> query = builder.createQuery(Review.class);
        Root<Review> root = query.from(Review.class);
        query.select(root).where(builder.equal(root.get(propertyName), value));
        List<Review> reviews = session.createSelectionQuery( query ).getResultList();

        session.close();
        return reviews;
    }

    /**
     * Get review by property (like)
     * sample usage: getByPropertyLike("restaurantName", "S")
     */
    public List<Review> getByPropertyLike(String propertyName, String value) {
        Session session = sessionFactory.openSession();

        logger.debug("Searching for user with {} = {}",  propertyName, value);

        HibernateCriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Review> query = builder.createQuery(Review.class);
        Root<Review> root = query.from(Review.class);
        Expression<String> propertyPath = root.get(propertyName);

        query.where(builder.like(propertyPath, "%" + value + "%"));

        List<Review> reviews = session.createQuery( query ).getResultList();
        session.close();
        return reviews;
    }

}
