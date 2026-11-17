package Mediator;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ChatServerTest {

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

    @Test
    public void testConstructorHasNoUsers() {
        assertTrue(new ChatServer().getRegisteredUsers().isEmpty());
    }

    @Test
    public void testRegisterUser() {
        assertEquals(List.of(jade, kyle, connor), server.getRegisteredUsers());
        assertSame(server, jade.getChatServer());
        User porter = new User("Porter");
        assertTrue(server.registerUser(porter));
        assertTrue(server.isRegistered(porter));
        assertSame(server, porter.getChatServer());
    }

    @Test
    public void testRegisterUserTwiceReturnsFalse() {
        assertFalse(server.registerUser(jade));
        assertEquals(List.of(jade, kyle, connor), server.getRegisteredUsers());
    }

    @Test
    public void testRegisterUserMovesUserFromOtherServer() {
        ChatServer otherServer = new ChatServer();
        assertTrue(otherServer.registerUser(jade));
        assertFalse(server.isRegistered(jade));
        assertTrue(otherServer.isRegistered(jade));
        assertSame(otherServer, jade.getChatServer());
    }

    @Test
    public void testUnregisterUser() {
        assertTrue(server.unregisterUser(connor));
        assertFalse(server.isRegistered(connor));
        assertNull(connor.getChatServer());
        assertEquals(List.of(jade, kyle), server.getRegisteredUsers());
        assertFalse(server.unregisterUser(connor));
    }

    @Test
    public void testIsRegistered() {
        assertTrue(server.isRegistered(kyle));
        assertFalse(server.isRegistered(new User("Porter")));
    }

    @Test
    public void testGetRegisteredUsersIsUnmodifiable() {
        assertThrows(UnsupportedOperationException.class, () -> server.getRegisteredUsers().add(new User("Porter")));
    }

    @Test
    public void testSendMessage() {
        Message message = new Message(jade, List.of(kyle, connor), "Hi Kyle and Connor!", timestamp);
        server.sendMessage(message);
        assertEquals(List.of(message), kyle.getChatHistory().getMessages());
        assertEquals(List.of(message), connor.getChatHistory().getMessages());
        assertTrue(jade.getChatHistory().getMessages().isEmpty());
    }

    @Test
    public void testSendMessageSkipsUnregisteredRecipients() {
        server.unregisterUser(connor);
        Message message = new Message(jade, List.of(kyle, connor), "Hi Kyle and Connor!", timestamp);
        server.sendMessage(message);
        assertEquals(List.of(message), kyle.getChatHistory().getMessages());
        assertTrue(connor.getChatHistory().getMessages().isEmpty());
    }

    @Test
    public void testSendMessageSkipsSender() {
        server.sendMessage(new Message(jade, List.of(jade, kyle), "Note to self and Kyle", timestamp));
        assertTrue(jade.getChatHistory().getMessages().isEmpty());
        assertEquals(1, kyle.getChatHistory().getMessages().size());
    }

    @Test
    public void testSendMessageFromUnregisteredUserThrows() {
        Message message = new Message(new User("Porter"), List.of(jade), "Hi Jade!", timestamp);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> server.sendMessage(message));
        assertEquals("Porter is not registered with this chat server", exception.getMessage());
        assertTrue(jade.getChatHistory().getMessages().isEmpty());
    }
}
