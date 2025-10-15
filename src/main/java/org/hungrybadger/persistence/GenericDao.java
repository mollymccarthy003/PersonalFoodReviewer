package org.hungrybadger.persistence;

import jakarta.persistence.criteria.CriteriaBuilder;
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

/**
 * A generic DAO for CRUD operations on any entity type.
 *
 * @param <T> The entity type
 */
public class GenericDao<T> {
    private Class<T> type;

    private final Logger logger = LogManager.getLogger(this.getClass());
    SessionFactory sessionFactory = SessionFactoryProvider.getSessionFactory();

    /**
     * Instantiates a new GenericDao for the given entity type.
     *
     * @param type the entity class type
     */
    public GenericDao(Class<T> type) {
        this.type=type;
    }
    /**
     * Get entity by id
     */
    public <T>T getById(int id) {
        Session session = getSession();
        T entity = (T)session.get(type, id);
        session.close();
        return entity;
    }

    /**
     * Update an existing entity.
     *
     * @param entity the entity to update
     */
    public void update(T entity) {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        session.merge(entity);
        transaction.commit();
        session.close();
    }

    /**
     * Insert a new entity.
     *
     * @param entity the entity to insert
     * @return the generated ID
     */
    public int insert(T entity) {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        session.persist(entity);
        transaction.commit();

        // Use reflection to call getId() if available
        int id = 0;
        try {
            var method = entity.getClass().getMethod("getId");
            id = (int) method.invoke(entity);
        } catch (Exception e) {
            logger.warn("Could not retrieve ID for entity of type {}", type.getSimpleName());
        }

        session.close();
        return id;
    }

    /**
     * Delete an entity
     * @param entity entity to be deleted
     */
    public void delete(T entity) {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        session.delete(entity);
        transaction.commit();
        session.close();
    }


    /** Return a list of all entities
     *
     * @return All entities
     */
    public List<T> getAll() {

        Session session = getSession();

        CriteriaBuilder builder = session.getCriteriaBuilder();

        CriteriaQuery<T> criteriaQuery = builder.createQuery(type);
        Root<T> root = criteriaQuery.from(type);
        List <T> list = session.createQuery(criteriaQuery).getResultList();
        session.close();
        return list;
    }

    /**
     * Get entities by property (exact match).
     * Example: getByPropertyEqual("restaurantName", "Sushi Express")
     *
     * @param propertyName property name
     * @param value        property value
     * @return list of matching entities
     */
    public List<T> getByPropertyEqual(String propertyName, String value) {
        Session session = getSession();
        logger.debug("Searching for {} with {} = {}", type.getSimpleName(), propertyName, value);

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(type);
        Root<T> root = query.from(type);
        query.select(root).where(builder.equal(root.get(propertyName), value));

        List<T> results = session.createQuery(query).getResultList();
        session.close();
        return results;
    }

    /**
     * Get entities by property (LIKE search).
     * Example: getByPropertyLike("restaurantName", "S")
     *
     * @param propertyName property name
     * @param value        partial value
     * @return list of matching entities
     */
    public List<T> getByPropertyLike(String propertyName, String value) {
        Session session = sessionFactory.openSession();
        logger.debug("Searching for {} where {} LIKE %{}%", type.getSimpleName(), propertyName, value);

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(type);
        Root<T> root = query.from(type);
        Expression<String> propertyPath = root.get(propertyName);

        query.select(root).where(builder.like(propertyPath, "%" + value + "%"));

        List<T> results = session.createQuery(query).getResultList();
        session.close();
        return results;
    }

    /**
     * Helper method to get a new Hibernate session.
     */
    private Session getSession() {
        return SessionFactoryProvider.getSessionFactory().openSession();
    }


}
