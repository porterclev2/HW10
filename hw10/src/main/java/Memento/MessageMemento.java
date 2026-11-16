package Memento;

import java.time.LocalDateTime;

import Mediator.Message;

public class MessageMemento {
    private String content;
    private LocalDateTime timestamp;

    public MessageMemento(String content, LocalDateTime timestamp) {
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "[" + Message.formatTimestamp(timestamp) + "] " + content;
    }
}
