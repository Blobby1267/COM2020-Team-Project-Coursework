import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Field;

public class TestUser {
    @Test
    void TestGetUsername(){
        Main.User testUser = new Main.User("Davi","10");
        Assertions.assertEquals("Davi", testUser.getUsername());
    }

    @Test
    void TestGetPoints(){
        Main.User testUser = new Main.User("Davi","10");
        Assertions.assertEquals(10, testUser.getPoints());
    }

    @Test
    void TestSetUsername() throws NoSuchFieldException, IllegalAccessException{
        Main.User testUser = new Main.User("Davi","10");
        testUser.setUsername("Luca"); //Use method being tested

        //Used Java Reflection to get the value of the field
        Field name = testUser.getClass().getDeclaredField("username");
        name.setAccessible(true); //Makes sure I can access the private variable
        Assertions.assertEquals("Luca", name.get(testUser));
    }

    

}
