import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.plugins.MockMaker;

@Suite
@SelectClasses({TestUser.class, TestLoginController.class})
public class TestLoginSuite {
}
