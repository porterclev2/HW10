package Mediator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatServer {
    private List<User> users;

    public ChatServer() {
        this.users = new ArrayList<>();
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
            if (recipient != sender && isRegistered(recipient)) {
                recipient.receiveMessage(message);
            }
        }
    }

    private void requireRegistered(User user) {
        if (!isRegistered(user)) {
            throw new IllegalArgumentException(user.getName() + " is not registered with this chat server");
        }
    }
}
