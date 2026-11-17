package Mediator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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

    @Test
    public void testBlockUser() {
        server.blockUser(kyle, connor);
        assertTrue(server.isBlocked(kyle, connor));
        assertFalse(server.isBlocked(connor, kyle));
        assertFalse(server.isBlocked(jade, connor));
    }

    @Test
    public void testBlockedUserMessagesAreNotDelivered() {
        server.blockUser(kyle, connor);
        Message message = new Message(connor, List.of(kyle, jade), "Kyle, can you see this?", timestamp);
        server.sendMessage(message);
        assertTrue(kyle.getChatHistory().getMessages().isEmpty());
        assertEquals(List.of(message), jade.getChatHistory().getMessages());
    }

    @Test
    public void testBlockerCanStillMessageBlockedUser() {
        server.blockUser(kyle, connor);
        Message message = new Message(kyle, List.of(connor), "I blocked you", timestamp);
        server.sendMessage(message);
        assertEquals(List.of(message), connor.getChatHistory().getMessages());
    }

    @Test
    public void testUnblockUser() {
        server.blockUser(kyle, connor);
        server.unblockUser(kyle, connor);
        assertFalse(server.isBlocked(kyle, connor));
        Message message = new Message(connor, List.of(kyle), "Kyle, how about now?", timestamp);
        server.sendMessage(message);
        assertEquals(List.of(message), kyle.getChatHistory().getMessages());
    }

    @Test
    public void testUnblockUserThatWasNotBlocked() {
        server.unblockUser(jade, kyle);
        assertFalse(server.isBlocked(jade, kyle));
    }

    @Test
    public void testGetBlockedUsers() {
        assertTrue(server.getBlockedUsers(kyle).isEmpty());
        server.blockUser(kyle, connor);
        server.blockUser(kyle, jade);
        assertEquals(Set.of(connor, jade), server.getBlockedUsers(kyle));
        assertThrows(UnsupportedOperationException.class, () -> server.getBlockedUsers(kyle).add(kyle));
    }

    @Test
    public void testBlockSelfThrows() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> server.blockUser(kyle, kyle));
        assertEquals("Kyle cannot block themselves", exception.getMessage());
    }

    @Test
    public void testBlockUnregisteredUserThrows() {
        User porter = new User("Porter");
        assertThrows(IllegalArgumentException.class, () -> server.blockUser(kyle, porter));
        assertThrows(IllegalArgumentException.class, () -> server.blockUser(porter, kyle));
    }

    @Test
    public void testUnregisterUserClearsBlocks() {
        server.blockUser(kyle, connor);
        server.blockUser(connor, kyle);
        server.unregisterUser(connor);
        assertTrue(server.getBlockedUsers(kyle).isEmpty());
        assertFalse(server.isBlocked(connor, kyle));
        server.registerUser(connor);
        Message message = new Message(connor, List.of(kyle), "I'm back", timestamp);
        server.sendMessage(message);
        assertEquals(List.of(message), kyle.getChatHistory().getMessages());
    }

    @Test
    public void testUndoMessage() {
        Message message = new Message(jade, List.of(kyle, connor), "Oops", timestamp);
        server.sendMessage(message);
        server.undoMessage(jade, message.createMemento());
        assertTrue(kyle.getChatHistory().getMessages().isEmpty());
        assertTrue(connor.getChatHistory().getMessages().isEmpty());
    }

    @Test
    public void testUndoMessageDoesNotChangeSenderHistory() {
        Message message = new Message(jade, List.of(kyle), "Oops", timestamp);
        jade.receiveMessage(message);
        server.undoMessage(jade, message.createMemento());
        assertEquals(List.of(message), jade.getChatHistory().getMessages());
    }

    @Test
    public void testUndoMessageKeepsOtherMessages() {
        Message keep = new Message(connor, List.of(kyle), "Hi Kyle!", timestamp);
        Message undo = new Message(jade, List.of(kyle), "Oops", timestamp);
        server.sendMessage(keep);
        server.sendMessage(undo);
        server.undoMessage(jade, undo.createMemento());
        assertEquals(List.of(keep), kyle.getChatHistory().getMessages());
    }

    @Test
    public void testUndoMessageFromUnregisteredUserThrows() {
        Message message = new Message(jade, List.of(kyle), "Oops", timestamp);
        assertThrows(IllegalArgumentException.class, () -> {
            server.undoMessage(new User("Porter"), message.createMemento());
        });
    }
}
