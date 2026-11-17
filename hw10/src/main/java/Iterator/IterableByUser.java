package Iterator;

import java.util.Iterator;

import Mediator.Message;
import Mediator.User;

public interface IterableByUser {
    Iterator<Message> iterator(User userToSearchWith);
}
