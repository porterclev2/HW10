import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import Mediator.UserTest;
import Mediator.MessageTest;
import Memento.MessageMementoTest;
import Memento.ChatHistoryTest;
import Mediator.ChatServerTest;
import Iterator.SearchMessagesByUserTest;

@Suite
@SelectClasses({UserTest.class, MessageTest.class, MessageMementoTest.class, ChatHistoryTest.class, ChatServerTest.class, SearchMessagesByUserTest.class})
public class CompleteUnitTest {

}
