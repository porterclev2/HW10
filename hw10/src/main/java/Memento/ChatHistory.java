package Memento;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import Iterator.IterableByUser;
import Iterator.SearchMessagesByUser;
import Mediator.Message;
import Mediator.User;

public class ChatHistory implements IterableByUser {
    private User owner;
    private List<Message> messages;

    public ChatHistory(User owner) {
        this.owner = owner;
        this.messages = new ArrayList<>();
    }

    public User getOwner() {
        return owner;
    }

    public List<Message> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    public void addMessage(Message message) {
        messages.add(message);
    }

    public boolean removeMessage(User sender, MessageMemento memento) {
        for (int i = messages.size() - 1; i >= 0; i--) {
            if (messages.get(i).matches(sender, memento)) {
                messages.remove(i);
                return true;
            }
        }
        return false;
    }

    public Message getLastMessageSent() {
        for (int i = messages.size() - 1; i >= 0; i--) {
            if (messages.get(i).getSender() == owner) {
                return messages.get(i);
            }
        }
        return null;
    }

    @Override
    public Iterator<Message> iterator(User userToSearchWith) {
        return new SearchMessagesByUser(this, userToSearchWith);
    }

    @Override
    public String toString() {
        if (messages.isEmpty()) {
            return owner.getName() + "'s chat history is empty";
        }
        StringBuilder history = new StringBuilder(owner.getName() + "'s chat history:");
        for (Message message : messages) {
            history.append(System.lineSeparator()).append(message);
        }
        return history.toString();
    }
}
