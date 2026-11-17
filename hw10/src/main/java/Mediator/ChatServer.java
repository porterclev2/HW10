package Mediator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Memento.MessageMemento;

public class ChatServer {
    private List<User> users;
    private Map<User, Set<User>> blockedUsers;

    public ChatServer() {
        this.users = new ArrayList<>();
        this.blockedUsers = new HashMap<>();
    }

    public boolean registerUser(User user) {
        if (isRegistered(user)) {
            return false;
        }
        if (user.getChatServer() != null) {
            user.getChatServer().unregisterUser(user);
        }
        users.add(user);
        user.setChatServer(this);
        return true;
    }

    public boolean unregisterUser(User user) {
        if (!users.remove(user)) {
            return false;
        }
        user.setChatServer(null);
        blockedUsers.remove(user);
        for (Set<User> blocked : blockedUsers.values()) {
            blocked.remove(user);
        }
        return true;
    }

    public boolean isRegistered(User user) {
        return users.contains(user);
    }

    public List<User> getRegisteredUsers() {
        return Collections.unmodifiableList(users);
    }

    public void sendMessage(Message message) {
        User sender = message.getSender();
        requireRegistered(sender);
        for (User recipient : message.getRecipients()) {
            if (recipient != sender && isRegistered(recipient) && !isBlocked(recipient, sender)) {
                recipient.receiveMessage(message);
            }
        }
    }

    public void undoMessage(User sender, MessageMemento memento) {
        requireRegistered(sender);
        for (User user : users) {
            if (user != sender) {
                user.removeMessage(sender, memento);
            }
        }
    }

    public void blockUser(User blocker, User blocked) {
        requireRegistered(blocker);
        requireRegistered(blocked);
        if (blocker == blocked) {
            throw new IllegalArgumentException(blocker.getName() + " cannot block themselves");
        }
        blockedUsers.computeIfAbsent(blocker, user -> new LinkedHashSet<>()).add(blocked);
    }

    public void unblockUser(User blocker, User blocked) {
        Set<User> blockedByBlocker = blockedUsers.get(blocker);
        if (blockedByBlocker != null) {
            blockedByBlocker.remove(blocked);
        }
    }

    public boolean isBlocked(User blocker, User blocked) {
        return blockedUsers.getOrDefault(blocker, Collections.emptySet()).contains(blocked);
    }

    public Set<User> getBlockedUsers(User blocker) {
        return Collections.unmodifiableSet(blockedUsers.getOrDefault(blocker, Collections.emptySet()));
    }

    private void requireRegistered(User user) {
        if (!isRegistered(user)) {
            throw new IllegalArgumentException(user.getName() + " is not registered with this chat server");
        }
    }
}
