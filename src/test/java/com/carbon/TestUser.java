package com.carbon;

import com.carbon.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;

@ExtendWith(MockitoExtension.class)
public class TestUser {

    @Test
    void TestGetUsername() throws IllegalAccessException, NoSuchFieldException {
        User testUser = new User();

        // Java Reflection used
        Field username = testUser.getClass().getDeclaredField("username");
        username.setAccessible(true); // Private field, so make sure we are able to access it
        username.set(testUser, "Davi"); // Set name without using setUsername() method
        Assertions.assertEquals("Davi", testUser.getUsername());
    }

    @Test
    void TestGetPassword() throws NoSuchFieldException, IllegalAccessException {
        User testUser = new User();

        // Java Reflection
        Field points = testUser.getClass().getDeclaredField("password");
        points.setAccessible(true); // Make private field accessible
        points.set(testUser, "hello");
        Assertions.assertEquals("hello", testUser.getPassword());
    }

    @Test
    void TestGetPoints() throws NoSuchFieldException, IllegalAccessException {
        User testUser = new User();

        // Java Reflection
        Field points = testUser.getClass().getDeclaredField("points");
        points.setAccessible(true); // Make private field accessible
        points.set(testUser, 10);
        Assertions.assertEquals(10, testUser.getPoints());
    }

    @Test
    void TestSetUsername() throws NoSuchFieldException, IllegalAccessException {
        User testUser = new User();
        testUser.setUsername("Luca"); // Use method being tested

        // Used Java Reflection to get the value of the field
        Field name = testUser.getClass().getDeclaredField("username");
        name.setAccessible(true); // Make private field accessible
        Assertions.assertEquals("Luca", name.get(testUser));
    }

    @Test
    void TestSetPassword() throws NoSuchFieldException, IllegalAccessException {
        User testUser = new User();
        testUser.setPassword("password123"); // Use method being tested

        // Java Reflection
        Field passwordField = testUser.getClass().getDeclaredField("password");
        passwordField.setAccessible(true); // Make private field accessible
        Assertions.assertEquals("password123", passwordField.get(testUser));
    }

    @Test
    void TestSetPoints() throws NoSuchFieldException, IllegalAccessException {
        User testUser = new User();
        testUser.setPoints(15); // Use method being tested

        // Java Reflection
        Field pointsField = testUser.getClass().getDeclaredField("points");
        pointsField.setAccessible(true); // Make private field accessible
        Assertions.assertEquals(15, pointsField.get(testUser));
    }

    void TestValidUsername() {
        String username1 = "Bob123";
        String username2 = "Bob"; // Minimum number of characters
        String username3 = "Bob12334";

        assertEquals(true, User.isValidUsername(username1));
        assertEquals(true, User.isValidUsername(username2));
        assertEquals(true, User.isValidUsername(username3));

    }

    void TestInvalidUsernameLength() {
        String username1 = "";
        String username2 = "Bo";
        assertEquals(false, User.isValidUsername(username1));
        assertEquals(false, User.isValidUsername(username2));
    }

    void TestInvalidUsernamePattern() {
        String username1 = "hello@£*";
        String username2 = "Test^DSA";
        assertEquals(false, User.isValidUsername(username1));
        assertEquals(false, User.isValidUsername(username2));
    }

}
