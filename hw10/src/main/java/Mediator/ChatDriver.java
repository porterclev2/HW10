package Mediator;

public class ChatDriver {
    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        User jade = new User("Jade");
        User kyle = new User("Kyle");
        User connor = new User("Connor");
        server.registerUser(jade);
        server.registerUser(kyle);
        server.registerUser(connor);

        System.out.println("Sending messages through the chat server\n");
        jade.sendMessage("Hi Kyle and Connor!", kyle, connor);
        kyle.sendMessage("Hey Jade!", jade);
        connor.sendMessage("Hello everyone!", jade, kyle);
        jade.viewChatHistory();
        kyle.viewChatHistory();
        connor.viewChatHistory();

        System.out.println("\nJade sends a message by mistake\n");
        jade.sendMessage("This was meant for someone else", kyle, connor);
        kyle.viewChatHistory();
        System.out.println("\nJade undoes the last message sent: " + jade.getLastSentMessage() + "\n");
        jade.undoLastMessage();
        jade.viewChatHistory();
        kyle.viewChatHistory();
        connor.viewChatHistory();

        System.out.println("\nKyle blocks Connor\n");
        kyle.blockUser(connor);
        connor.sendMessage("Kyle, can you see this?", kyle, jade);
        kyle.viewChatHistory();
        jade.viewChatHistory();

        System.out.println("\nKyle unblocks Connor\n");
        kyle.unblockUser(connor);
        connor.sendMessage("Kyle, how about now?", kyle);
        kyle.viewChatHistory();
    }
}
