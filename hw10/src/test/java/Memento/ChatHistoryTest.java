package Memento;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import Iterator.IterableByUser;
import Iterator.SearchMessagesByUser;
import Mediator.Message;
import Mediator.User;

public class ChatHistoryTest {

    User jade = new User("Jade");
    User kyle = new User("Kyle");
    User connor = new User("Connor");
    LocalDateTime timestamp = LocalDateTime.of(2026, 11, 12, 9, 30);
    Message sent = new Message(jade, List.of(kyle, connor), "Hi Kyle and Connor!", timestamp);
    Message received = new Message(kyle, List.of(jade), "Hey Jade!", timestamp.plusMinutes(1));
    ChatHistory chatHistory = new ChatHistory(jade);

    @Test
    public void testConstructorAndGetOwner() {
        assertSame(jade, chatHistory.getOwner());
        assertTrue(chatHistory.getMessages().isEmpty());
    }

    @Test
    public void testAddMessage() {
        chatHistory.addMessage(sent);
        chatHistory.addMessage(received);
        assertEquals(List.of(sent, received), chatHistory.getMessages());
    }

    @Test
    public void testGetMessagesIsUnmodifiable() {
        assertThrows(UnsupportedOperationException.class, () -> chatHistory.getMessages().add(sent));
    }

    @Test
    public void testGetLastMessageSent() {
        assertNull(chatHistory.getLastMessageSent());
        chatHistory.addMessage(sent);
        chatHistory.addMessage(received);
        assertSame(sent, chatHistory.getLastMessageSent());
        Message sentLater = new Message(jade, List.of(kyle), "See you soon", timestamp.plusMinutes(2));
        chatHistory.addMessage(sentLater);
        assertSame(sentLater, chatHistory.getLastMessageSent());
    }

    @Test
    public void testGetLastMessageSentWithOnlyReceivedMessages() {
        chatHistory.addMessage(received);
        assertNull(chatHistory.getLastMessageSent());
    }

    @Test
    public void testRemoveMessage() {
        chatHistory.addMessage(sent);
        chatHistory.addMessage(received);
        assertTrue(chatHistory.removeMessage(jade, sent.createMemento()));
        assertEquals(List.of(received), chatHistory.getMessages());
        assertFalse(chatHistory.removeMessage(jade, sent.createMemento()));
    }

    @Test
    public void testRemoveMessageRequiresMatchingSender() {
        chatHistory.addMessage(received);
        assertFalse(chatHistory.removeMessage(jade, received.createMemento()));
        assertEquals(List.of(received), chatHistory.getMessages());
    }

    @Test
    public void testRemoveMessageRemovesMostRecentMatch() {
        Message duplicate = new Message(jade, List.of(kyle, connor), "Hi Kyle and Connor!", timestamp);
        chatHistory.addMessage(sent);
        chatHistory.addMessage(duplicate);
        assertTrue(chatHistory.removeMessage(jade, duplicate.createMemento()));
        assertEquals(1, chatHistory.getMessages().size());
        assertSame(sent, chatHistory.getMessages().get(0));
    }

    @Test
    public void testToString() {
        chatHistory.addMessage(sent);
        chatHistory.addMessage(received);
        assertEquals("Jade's chat history:" + System.lineSeparator()
                + "[2026-11-12 09:30:00] Jade -> Kyle, Connor: Hi Kyle and Connor!" + System.lineSeparator()
                + "[2026-11-12 09:31:00] Kyle -> Jade: Hey Jade!", chatHistory.toString());
    }

    @Test
    public void testToStringEmpty() {
        assertEquals("Jade's chat history is empty", chatHistory.toString());
    }

    @Test
    public void testChatHistoryIsIterableByUser() {
        assertInstanceOf(IterableByUser.class, chatHistory);
    }

    @Test
    public void testIterator() {
        Message fromConnor = new Message(connor, List.of(jade), "Hello Jade!", timestamp.plusMinutes(2));
        chatHistory.addMessage(sent);
        chatHistory.addMessage(received);
        chatHistory.addMessage(fromConnor);
        Iterator<Message> messages = chatHistory.iterator(connor);
        assertInstanceOf(SearchMessagesByUser.class, messages);
        assertSame(sent, messages.next());
        assertSame(fromConnor, messages.next());
        assertFalse(messages.hasNext());
    }
}
