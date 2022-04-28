package wiki.sogou.mockito;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.verify;


public class MockTest {
    @Mock
    private List mockList;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void test() {
        mockList.add("test");
        verify(mockList).add("test");
    }
}
