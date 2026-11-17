package Iterator;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import Mediator.Message;
import Mediator.User;
import Memento.ChatHistory;

public class SearchMessagesByUser implements Iterator<Message> {
    private List<Message> messages;
    private User userToSearchWith;
    private int indexInChatHistory;

    public SearchMessagesByUser(ChatHistory chatHistory, User userToSearchWith) {
        this.messages = chatHistory.getMessages();
        this.userToSearchWith = userToSearchWith;
        this.indexInChatHistory = 0;
    }

    public User getUserToSearchWith() {
        return userToSearchWith;
    }

    @Override
    public boolean hasNext() {
        while (indexInChatHistory < messages.size()) {
            Message message = messages.get(indexInChatHistory);
            if (message.getSender() == userToSearchWith || message.getRecipients().contains(userToSearchWith)) {
                return true;
            }
            indexInChatHistory++;
        }
        return false;
    }

    @Override
    public Message next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more messages with " + userToSearchWith.getName());
        }
        return messages.get(indexInChatHistory++);
    }
}
