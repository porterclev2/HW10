package Mediator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MessageTest {

    User jade = new User("Jade");
    User kyle = new User("Kyle");
    User connor = new User("Connor");
    LocalDateTime timestamp = LocalDateTime.of(2026, 11, 12, 9, 30);
    Message message = new Message(jade, List.of(kyle, connor), "Hi Kyle and Connor!", timestamp);

    @Test
    public void testConstructorAndGetters() {
        assertSame(jade, message.getSender());
        assertEquals(List.of(kyle, connor), message.getRecipients());
        assertEquals("Hi Kyle and Connor!", message.getContent());
        assertEquals(timestamp, message.getTimestamp());
    }

    @Test
    public void testConstructorWithoutTimestampUsesCurrentTime() {
        LocalDateTime before = LocalDateTime.now();
        Message now = new Message(kyle, List.of(jade), "Hey Jade!");
        LocalDateTime after = LocalDateTime.now();
        assertFalse(now.getTimestamp().isBefore(before));
        assertFalse(now.getTimestamp().isAfter(after));
        assertSame(kyle, now.getSender());
        assertEquals(List.of(jade), now.getRecipients());
        assertEquals("Hey Jade!", now.getContent());
    }

    @Test
    public void testConstructorCopiesRecipients() {
        List<User> recipients = new ArrayList<>(List.of(kyle));
        Message copied = new Message(jade, recipients, "Hi Kyle!", timestamp);
        recipients.add(connor);
        assertEquals(List.of(kyle), copied.getRecipients());
    }

    @Test
    public void testGetRecipientsIsUnmodifiable() {
        assertThrows(UnsupportedOperationException.class, () -> message.getRecipients().add(jade));
    }

    @Test
    public void testConstructorWithoutRecipientsThrows() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Message(jade, List.of(), "Hello?", timestamp);
        });
        assertEquals("A message needs at least one recipient", exception.getMessage());
    }

    @Test
    public void testFormatTimestamp() {
        assertEquals("2026-11-12 09:30:00", Message.formatTimestamp(timestamp));
        assertEquals("2026-01-05 17:04:09", Message.formatTimestamp(LocalDateTime.of(2026, 1, 5, 17, 4, 9)));
    }

    @Test
    public void testToString() {
        assertEquals("[2026-11-12 09:30:00] Jade -> Kyle, Connor: Hi Kyle and Connor!", message.toString());
        assertEquals("[2026-11-12 09:30:00] Kyle -> Jade: Hey Jade!",
                new Message(kyle, List.of(jade), "Hey Jade!", timestamp).toString());
    }
}
