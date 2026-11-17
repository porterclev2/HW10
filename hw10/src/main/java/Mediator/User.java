package Mediator;

import java.util.Arrays;

import Memento.ChatHistory;
import Memento.MessageMemento;

public class User {
    private String name;
    private ChatServer chatServer;
    private ChatHistory chatHistory;
    private MessageMemento lastSentMessage;

    public User(String name) {
        this.name = name;
        this.chatHistory = new ChatHistory(this);
    }

    public String getName() {
        return name;
    }

    public ChatServer getChatServer() {
        return chatServer;
    }

    void setChatServer(ChatServer chatServer) {
        this.chatServer = chatServer;
    }

    public ChatHistory getChatHistory() {
        return chatHistory;
    }

    public MessageMemento getLastSentMessage() {
        return lastSentMessage;
    }

    public Message sendMessage(String content, User... recipients) {
        requireChatServer();
        Message message = new Message(this, Arrays.asList(recipients), content);
        chatHistory.addMessage(message);
        lastSentMessage = message.createMemento();
        chatServer.sendMessage(message);
        return message;
    }

    public void receiveMessage(Message message) {
        chatHistory.addMessage(message);
    }

    public void viewChatHistory() {
        System.out.println(chatHistory);
    }

    private void requireChatServer() {
        if (chatServer == null) {
            throw new IllegalStateException(name + " is not registered with a chat server");
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
