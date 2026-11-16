package Mediator;

import Memento.ChatHistory;

public class User {
    private String name;
    private ChatHistory chatHistory;

    public User(String name) {
        this.name = name;
        this.chatHistory = new ChatHistory(this);
    }

    public String getName() {
        return name;
    }

    public ChatHistory getChatHistory() {
        return chatHistory;
    }

    @Override
    public String toString() {
        return name;
    }
}
