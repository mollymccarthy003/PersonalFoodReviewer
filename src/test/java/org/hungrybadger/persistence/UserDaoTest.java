package org.hungrybadger.persistence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hungrybadger.entity.User;
import org.hungrybadger.util.Database;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link GenericDao} operations on {@link User} entities.
 * <p>
 * This test class uses JUnit 5 and validates CRUD functionality:
 * <ul>
 *     <li>Retrieving a user by ID</li>
 *     <li>Retrieving all users</li>
 *     <li>Querying by property equality (email, cognitoSub)</li>
 *     <li>Inserting new users</li>
 *     <li>Updating existing users</li>
 *     <li>Deleting users</li>
 * </ul>
 * Each test resets the database to a known state using {@link Database#runSQL(String)}.
 * </p>
 */
class UserDaoTest {

    /**
     * Logger for test output.
     */
    private final Logger logger = LogManager.getLogger(this.getClass());

    /**
     * GenericDao instance used for User entity operations.
     */
    GenericDao<User> userDao;

    /**
     * Runs before each test to reset the database and initialize the DAO.
     */
    @BeforeEach
    void setUp() {
        // Reset the database before each test
        Database database = Database.getInstance();
        database.runSQL("cleanDB.sql");

        // Initialize GenericDao for Review
        userDao = new GenericDao<>(User.class);
    }

    /**
     * Tests retrieving a user by a valid ID.
     * Asserts that the returned user has expected full name, email, and Cognito sub.
     */
    @Test
    void getByIdSuccess() {
        User user = userDao.getById(1);

        assertNotNull(user);
        assertEquals("Molly McCarthy", user.getFullName());
        assertEquals("molly@example.com", user.getEmail());
        assertEquals("sub-1111-aaaa-bbbb-ccccdddd0001", user.getCognitoSub());
    }


    /**
     * Tests retrieving all users.
     * Expects exactly 3 users in the test database.
     */
    @Test
    void getAllSuccess() {
        List<User> users = userDao.getAll();

        assertEquals(3, users.size()); // exactly 3 rows in your table
    }

    /**
     * Tests querying users by email property.
     * Verifies the returned user matches expected ID, full name, and Cognito sub.
     */
    @Test
    void getByPropertyEqualEmailSuccess() {
        List<User> users = userDao.getByPropertyEqual("email", "john@example.com");

        assertEquals(1, users.size());
        User user = users.get(0);
        assertEquals(2, user.getId());
        assertEquals("John Doe", user.getFullName());
        assertEquals("sub-2222-bbbb-cccc-ddddeeee0002", user.getCognitoSub());
    }

    /**
     * Tests querying users by Cognito sub property.
     * Verifies the returned user matches expected ID, full name, and email.
     */
    @Test
    void getByPropertyEqualCognitoSuccess() {
        List<User> users = userDao.getByPropertyEqual(
                "cognitoSub",
                "sub-3333-cccc-dddd-eeeeffff0003"
        );

        assertEquals(1, users.size());
        User user = users.get(0);
        assertEquals(3, user.getId());
        assertEquals("Jane Smith", user.getFullName());
        assertEquals("jane@example.com", user.getEmail());
    }

    /**
     * Tests inserting a new user into the database.
     * Validates that the inserted user can be retrieved and has expected properties.
     */
    @Test
    void insertSuccess() {
        User newUser = new User();
        newUser.setCognitoSub("sub-4444-dddd-eeee-ffff11112222");
        newUser.setFullName("New User");
        newUser.setEmail("newuser@example.com");

        int id = userDao.insert(newUser);
        assertNotEquals(0, id);

        User inserted = userDao.getById(id);

        assertEquals("New User", inserted.getFullName());
        assertEquals("newuser@example.com", inserted.getEmail());
    }

    /**
     * Tests updating an existing user's full name.
     * Verifies that the change is persisted in the database.
     */
    @Test
    void updateSuccess() {
        User user = userDao.getById(1);
        user.setFullName("Molly Updated");

        userDao.update(user);

        User updated = userDao.getById(1);
        assertEquals("Molly Updated", updated.getFullName());
    }

    /**
     * Tests deleting a user by ID.
     * Validates that the user no longer exists in the database.
     */
    @Test
    void deleteSuccess() {
        User user = userDao.getById(3);
        assertNotNull(user);

        userDao.delete(user);
        assertNull(userDao.getById(3));
    }
}
