package Mediator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class Message {
    private static DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private LocalDateTime timestamp;

    private User sender;
    private List<User> recipients;
    
    private String content;

    public Message(User sender, List<User> recipients, String content) {
        this(sender, recipients, content, LocalDateTime.now());
    }

    public Message(User sender, List<User> recipients, String content, LocalDateTime timestamp) {
        if (recipients.isEmpty()) {
            throw new IllegalArgumentException("A message needs at least one recipient");
        }
        this.sender = sender;
        this.recipients = new ArrayList<>(recipients);
        this.content = content;
        this.timestamp = timestamp;
    }

    public User getSender() {
        return sender;
    }

    public List<User> getRecipients() {
        return Collections.unmodifiableList(recipients);
    }
    
    public String getContent() {
        return content;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public static String formatTimestamp(LocalDateTime timestamp) {
        return timestamp.format(TIMESTAMP_FORMAT);
    }

    @Override
    public String toString() {
        List<String> recipientNames = new ArrayList<>();
        for (User recipient : recipients) {
            recipientNames.add(recipient.getName());
        }
        return "[" + formatTimestamp(timestamp) + "] " + sender.getName() + " -> "
                + String.join(", ", recipientNames) + ": " + content;
    }
}
