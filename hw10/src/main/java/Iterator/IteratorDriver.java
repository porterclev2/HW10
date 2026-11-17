package Iterator;

import java.util.Iterator;

import Mediator.ChatServer;
import Mediator.Message;
import Mediator.User;

public class IteratorDriver {
    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        User jade = new User("Jade");
        User kyle = new User("Kyle");
        User connor = new User("Connor");
        server.registerUser(jade);
        server.registerUser(kyle);
        server.registerUser(connor);

        jade.sendMessage("Hi Kyle and Connor!", kyle, connor);
        kyle.sendMessage("Hey Jade!", jade);
        connor.sendMessage("Hello everyone!", jade, kyle);
        jade.sendMessage("Connor, are you coming to class?", connor);
        connor.sendMessage("Yes, see you there", jade);
        jade.viewChatHistory();

        System.out.println("\nIterating over Jade's messages with Kyle\n");
        Iterator<Message> messagesWithKyle = jade.iterator(kyle);
        while (messagesWithKyle.hasNext()) {
            System.out.println(messagesWithKyle.next());
        }

        System.out.println("\nIterating over Jade's messages with Connor\n");
        Iterator<Message> messagesWithConnor = jade.iterator(connor);
        while (messagesWithConnor.hasNext()) {
            System.out.println(messagesWithConnor.next());
        }

        System.out.println();
        kyle.viewChatHistoryWith(connor);
    }
}
