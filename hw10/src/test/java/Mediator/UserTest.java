package Mediator;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
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
import Memento.MessageMemento;

public class UserTest {

    ChatServer server = new ChatServer();
    User jade = new User("Jade");
    User kyle = new User("Kyle");
    User connor = new User("Connor");
    LocalDateTime timestamp = LocalDateTime.of(2026, 11, 12, 9, 30);

    @BeforeEach
    public void registerUsers() {
        server.registerUser(jade);
        server.registerUser(kyle);
        server.registerUser(connor);
    }

    private String captureOutput(Runnable action) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
        try {
            action.run();
        } finally {
            System.setOut(originalOut);
        }
        return output.toString();
    }

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

    @Test
    public void testSetChatServer() {
        User porter = new User("Porter");
        porter.setChatServer(server);
        assertSame(server, porter.getChatServer());
        porter.setChatServer(null);
        assertNull(porter.getChatServer());
    }

    @Test
    public void testGetChatServerIsNullBeforeRegistering() {
        assertNull(new User("Porter").getChatServer());
    }

    @Test
    public void testGetChatServerAfterRegistering() {
        assertSame(server, jade.getChatServer());
    }

    @Test
    public void testSendMessageToOneUser() {
        Message message = jade.sendMessage("Hi Kyle!", kyle);
        assertSame(jade, message.getSender());
        assertEquals(List.of(kyle), message.getRecipients());
        assertEquals("Hi Kyle!", message.getContent());
        assertEquals(List.of(message), jade.getChatHistory().getMessages());
        assertEquals(List.of(message), kyle.getChatHistory().getMessages());
        assertTrue(connor.getChatHistory().getMessages().isEmpty());
    }

    @Test
    public void testSendMessageToMultipleUsers() {
        Message message = jade.sendMessage("Hi Kyle and Connor!", kyle, connor);
        assertEquals(List.of(kyle, connor), message.getRecipients());
        assertEquals(List.of(message), jade.getChatHistory().getMessages());
        assertEquals(List.of(message), kyle.getChatHistory().getMessages());
        assertEquals(List.of(message), connor.getChatHistory().getMessages());
    }

    @Test
    public void testSendMessageGoesThroughChatServer() {
        server.unregisterUser(connor);
        jade.sendMessage("Hi Kyle and Connor!", kyle, connor);
        assertEquals(1, kyle.getChatHistory().getMessages().size());
        assertTrue(connor.getChatHistory().getMessages().isEmpty());
    }

    @Test
    public void testSendMessageWithoutRecipientsThrows() {
        assertThrows(IllegalArgumentException.class, () -> jade.sendMessage("Hello?"));
        assertTrue(jade.getChatHistory().getMessages().isEmpty());
    }

    @Test
    public void testSendMessageWhenNotRegisteredThrows() {
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            new User("Porter").sendMessage("Hi Jade!", jade);
        });
        assertEquals("Porter is not registered with a chat server", exception.getMessage());
        assertTrue(jade.getChatHistory().getMessages().isEmpty());
    }

    @Test
    public void testReceiveMessage() {
        Message message = new Message(kyle, List.of(jade), "Hey Jade!", timestamp);
        jade.receiveMessage(message);
        assertEquals(List.of(message), jade.getChatHistory().getMessages());
    }

    @Test
    public void testGetLastSentMessage() {
        assertNull(jade.getLastSentMessage());
        Message message = jade.sendMessage("Hi Kyle!", kyle);
        MessageMemento memento = jade.getLastSentMessage();
        assertEquals("Hi Kyle!", memento.getContent());
        assertEquals(message.getTimestamp(), memento.getTimestamp());
    }

    @Test
    public void testGetLastSentMessageIgnoresReceivedMessages() {
        kyle.sendMessage("Hey Jade!", jade);
        assertNull(jade.getLastSentMessage());
    }

    @Test
    public void testViewChatHistory() {
        jade.receiveMessage(new Message(kyle, List.of(jade), "Hey Jade!", timestamp));
        assertEquals("Jade's chat history:" + System.lineSeparator()
                + "[2026-11-12 09:30:00] Kyle -> Jade: Hey Jade!" + System.lineSeparator(),
                captureOutput(jade::viewChatHistory));
    }

    @Test
    public void testViewEmptyChatHistory() {
        assertEquals("Jade's chat history is empty" + System.lineSeparator(),
                captureOutput(jade::viewChatHistory));
    }

    @Test
    public void testBlockUser() {
        kyle.blockUser(connor);
        assertTrue(server.isBlocked(kyle, connor));
        Message message = connor.sendMessage("Kyle, can you see this?", kyle, jade);
        assertTrue(kyle.getChatHistory().getMessages().isEmpty());
        assertEquals(List.of(message), jade.getChatHistory().getMessages());
    }

    @Test
    public void testUnblockUser() {
        kyle.blockUser(connor);
        kyle.unblockUser(connor);
        assertFalse(server.isBlocked(kyle, connor));
        Message message = connor.sendMessage("Kyle, how about now?", kyle);
        assertEquals(List.of(message), kyle.getChatHistory().getMessages());
    }

    @Test
    public void testBlockUserWhenNotRegisteredThrows() {
        User porter = new User("Porter");
        assertThrows(IllegalStateException.class, () -> porter.blockUser(jade));
        assertThrows(IllegalStateException.class, () -> porter.unblockUser(jade));
    }

    @Test
    public void testRemoveMessage() {
        Message message = new Message(kyle, List.of(jade), "Hey Jade!", timestamp);
        jade.receiveMessage(message);
        assertTrue(jade.removeMessage(kyle, message.createMemento()));
        assertTrue(jade.getChatHistory().getMessages().isEmpty());
        assertFalse(jade.removeMessage(kyle, message.createMemento()));
    }

    @Test
    public void testUndoLastMessage() {
        jade.sendMessage("This was meant for someone else", kyle, connor);
        assertTrue(jade.undoLastMessage());
        assertTrue(jade.getChatHistory().getMessages().isEmpty());
        assertTrue(kyle.getChatHistory().getMessages().isEmpty());
        assertTrue(connor.getChatHistory().getMessages().isEmpty());
        assertNull(jade.getLastSentMessage());
    }

    @Test
    public void testUndoLastMessageKeepsOtherMessages() {
        Message received = kyle.sendMessage("Hey Jade!", jade);
        jade.sendMessage("Oops", kyle);
        jade.undoLastMessage();
        assertEquals(List.of(received), jade.getChatHistory().getMessages());
        assertEquals(List.of(received), kyle.getChatHistory().getMessages());
    }

    @Test
    public void testUndoLastMessageRestoresPreviousSentMessage() {
        Message first = jade.sendMessage("First", kyle);
        jade.sendMessage("Second", kyle);
        assertTrue(jade.undoLastMessage());
        assertEquals("First", jade.getLastSentMessage().getContent());
        assertEquals(first.getTimestamp(), jade.getLastSentMessage().getTimestamp());
        assertEquals(List.of(first), kyle.getChatHistory().getMessages());
        assertTrue(jade.undoLastMessage());
        assertTrue(kyle.getChatHistory().getMessages().isEmpty());
        assertFalse(jade.undoLastMessage());
    }

    @Test
    public void testUndoLastMessageWithNothingSentReturnsFalse() {
        Message received = kyle.sendMessage("Hey Jade!", jade);
        assertFalse(jade.undoLastMessage());
        assertEquals(List.of(received), jade.getChatHistory().getMessages());
    }

    @Test
    public void testUndoLastMessageWhenNotRegisteredThrows() {
        assertThrows(IllegalStateException.class, () -> new User("Porter").undoLastMessage());
    }

    @Test
    public void testUserIsIterableByUser() {
        assertInstanceOf(IterableByUser.class, jade);
    }

    @Test
    public void testIterator() {
        Message toKyle = jade.sendMessage("Hi Kyle!", kyle);
        jade.sendMessage("Hi Connor!", connor);
        Message fromKyle = kyle.sendMessage("Hey Jade!", jade);
        Iterator<Message> messages = jade.iterator(kyle);
        assertInstanceOf(SearchMessagesByUser.class, messages);
        assertSame(toKyle, messages.next());
        assertSame(fromKyle, messages.next());
        assertFalse(messages.hasNext());
    }

    @Test
    public void testViewChatHistoryWith() {
        jade.receiveMessage(new Message(kyle, List.of(jade), "Hey Jade!", timestamp));
        jade.receiveMessage(new Message(connor, List.of(jade), "Hello Jade!", timestamp));
        assertEquals("Jade's messages with Kyle:" + System.lineSeparator()
                + "[2026-11-12 09:30:00] Kyle -> Jade: Hey Jade!" + System.lineSeparator(),
                captureOutput(() -> jade.viewChatHistoryWith(kyle)));
    }

    @Test
    public void testViewChatHistoryWithNoMessages() {
        jade.receiveMessage(new Message(connor, List.of(jade), "Hello Jade!", timestamp));
        assertEquals("Jade's messages with Kyle:" + System.lineSeparator()
                + "No messages" + System.lineSeparator(),
                captureOutput(() -> jade.viewChatHistoryWith(kyle)));
    }
}
