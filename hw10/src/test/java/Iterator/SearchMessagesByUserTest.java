package Iterator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import Mediator.Message;
import Mediator.User;
import Memento.ChatHistory;

public class SearchMessagesByUserTest {

    User jade = new User("Jade");
    User kyle = new User("Kyle");
    User connor = new User("Connor");
    LocalDateTime timestamp = LocalDateTime.of(2026, 11, 12, 9, 30);
    Message toKyleAndConnor = new Message(jade, List.of(kyle, connor), "Hi Kyle and Connor!", timestamp);
    Message fromKyle = new Message(kyle, List.of(jade), "Hey Jade!", timestamp.plusMinutes(1));
    Message fromConnor = new Message(connor, List.of(jade), "Hello Jade!", timestamp.plusMinutes(2));
    Message toConnor = new Message(jade, List.of(connor), "Connor, are you coming to class?", timestamp.plusMinutes(3));
    ChatHistory chatHistory = new ChatHistory(jade);

    @BeforeEach
    public void addMessages() {
        chatHistory.addMessage(toKyleAndConnor);
        chatHistory.addMessage(fromKyle);
        chatHistory.addMessage(fromConnor);
        chatHistory.addMessage(toConnor);
    }

    private List<Message> collect(SearchMessagesByUser iterator) {
        List<Message> messages = new ArrayList<>();
        while (iterator.hasNext()) {
            messages.add(iterator.next());
        }
        return messages;
    }

    @Test
    public void testSearchMessagesByUserIsIterator() {
        assertInstanceOf(Iterator.class, new SearchMessagesByUser(chatHistory, kyle));
    }

    @Test
    public void testConstructorAndGetUserToSearchWith() {
        assertSame(kyle, new SearchMessagesByUser(chatHistory, kyle).getUserToSearchWith());
    }

    @Test
    public void testIteratesMessagesSentOrReceivedByUser() {
        assertEquals(List.of(toKyleAndConnor, fromKyle), collect(new SearchMessagesByUser(chatHistory, kyle)));
        assertEquals(List.of(toKyleAndConnor, fromConnor, toConnor),
                collect(new SearchMessagesByUser(chatHistory, connor)));
    }

    @Test
    public void testSearchingWithOwnerReturnsAllMessages() {
        assertEquals(List.of(toKyleAndConnor, fromKyle, fromConnor, toConnor),
                collect(new SearchMessagesByUser(chatHistory, jade)));
    }

    @Test
    public void testHasNextDoesNotSkipMessages() {
        SearchMessagesByUser iterator = new SearchMessagesByUser(chatHistory, connor);
        assertTrue(iterator.hasNext());
        assertTrue(iterator.hasNext());
        assertSame(toKyleAndConnor, iterator.next());
    }

    @Test
    public void testNextWithoutHasNext() {
        SearchMessagesByUser iterator = new SearchMessagesByUser(chatHistory, kyle);
        assertSame(toKyleAndConnor, iterator.next());
        assertSame(fromKyle, iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testNextWithNoMoreMessagesThrows() {
        SearchMessagesByUser iterator = new SearchMessagesByUser(chatHistory, kyle);
        collect(iterator);
        Exception exception = assertThrows(NoSuchElementException.class, iterator::next);
        assertEquals("No more messages with Kyle", exception.getMessage());
    }

    @Test
    public void testUserWithNoMessages() {
        assertFalse(new SearchMessagesByUser(chatHistory, new User("Porter")).hasNext());
    }

    @Test
    public void testEmptyChatHistory() {
        assertFalse(new SearchMessagesByUser(new ChatHistory(kyle), jade).hasNext());
    }
}
