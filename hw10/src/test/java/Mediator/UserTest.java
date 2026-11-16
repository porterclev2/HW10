package Mediator;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    public void testGetChatHistory() {
        assertSame(jade, jade.getChatHistory().getOwner());
        assertTrue(jade.getChatHistory().getMessages().isEmpty());
        assertSame(jade.getChatHistory(), jade.getChatHistory());
    }
}
