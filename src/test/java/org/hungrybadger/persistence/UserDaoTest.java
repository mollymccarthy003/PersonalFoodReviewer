package org.hungrybadger.persistence;

import org.hungrybadger.entity.User;
import org.hungrybadger.util.Database;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserDaoTest {

    private final Logger logger = LogManager.getLogger(this.getClass());
    GenericDao<User> userDao;

    @BeforeEach
    void setUp() {
        // Reset the database before each test
        Database database = Database.getInstance();
        database.runSQL("cleanDB.sql");

        // Initialize GenericDao for Review
        userDao = new GenericDao<>(User.class);
    }

    @Test
    void getByIdSuccess() {
        User user = userDao.getById(1);

        assertNotNull(user);
        assertEquals("Molly McCarthy", user.getFullName());
        assertEquals("molly@example.com", user.getEmail());
        assertEquals("sub-1111-aaaa-bbbb-ccccdddd0001", user.getCognitoSub());
    }

    @Test
    void getAllSuccess() {
        List<User> users = userDao.getAll();

        assertEquals(3, users.size()); // exactly 3 rows in your table
    }

    @Test
    void getByPropertyEqualEmailSuccess() {
        List<User> users = userDao.getByPropertyEqual("email", "john@example.com");

        assertEquals(1, users.size());
        User user = users.get(0);
        assertEquals(2, user.getId());
        assertEquals("John Doe", user.getFullName());
        assertEquals("sub-2222-bbbb-cccc-ddddeeee0002", user.getCognitoSub());
    }

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

    @Test
    void updateSuccess() {
        User user = userDao.getById(1);
        user.setFullName("Molly Updated");

        userDao.update(user);

        User updated = userDao.getById(1);
        assertEquals("Molly Updated", updated.getFullName());
    }

    @Test
    void deleteSuccess() {
        User user = userDao.getById(3);
        assertNotNull(user);

        userDao.delete(user);
        assertNull(userDao.getById(3));
    }
}
