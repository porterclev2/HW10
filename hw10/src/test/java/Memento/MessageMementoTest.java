package Memento;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MessageMementoTest {

    LocalDateTime timestamp = LocalDateTime.of(2026, 11, 12, 9, 30);
    MessageMemento memento = new MessageMemento("Hi Kyle!", timestamp);

    @Test
    public void testConstructorAndGetters() {
        assertEquals("Hi Kyle!", memento.getContent());
        assertEquals(timestamp, memento.getTimestamp());
    }

    @Test
    public void testToString() {
        assertEquals("[2026-11-12 09:30:00] Hi Kyle!", memento.toString());
    }
}
