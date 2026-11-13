package Mediator;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTest {

    User jade = new User("Jade");
    User kyle = new User("Kyle");
    User connor = new User("Connor");

    @Test
    public void testConstructorAndGetName() {
        assertEquals("Jade", jade.getName());
        assertEquals("Porter", new User("Porter").getName());
    }

    @Test
    public void testToString() {
        assertEquals("Kyle", kyle.toString());
    }
}
