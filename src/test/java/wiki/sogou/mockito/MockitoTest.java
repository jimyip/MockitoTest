package wiki.sogou.mockito;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.InOrder;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;


class MockitoTest {

    /**
     * verify 是否用在调用过什么方法，行为
     */
    @Test
    void testVerifyBehaviour() {
        //mock creation
        List mockedList = mock(List.class);

        //using mock object
        mockedList.add("one");
        mockedList.clear();

        //verification
        verify(mockedList).add("one");
        verify(mockedList).clear();
    }


    @Test
    void testStubbing() {
        //You can mock concrete classes, not just interfaces
        LinkedList mockedList = mock(LinkedList.class);

        //stubbing
        when(mockedList.get(0)).thenReturn("first");
        when(mockedList.get(1)).thenThrow(new RuntimeException());

        //following prints "first"
        System.out.println(mockedList.get(0));

        //following throws runtime exception
        System.out.println(mockedList.get(1));

        //following prints "null" because get(999) was not stubbed
        System.out.println(mockedList.get(999));

        //Although it is possible to verify a stubbed invocation, usually it's just redundant
        //If your code cares what get(0) returns, then something else breaks (often even before verify() gets executed).
        //If your code doesn't care what get(0) returns, then it should not be stubbed.
        verify(mockedList).get(0);
    }

    /**
     * 验证时可以使用 ArgumentMatcher 去匹配特定的条件，返回一个参数
     */
    @Test
    public void testArgumentMatchers() {
        LinkedList<String> mockedList = mock(LinkedList.class);
        //stubbing using built-in anyInt() argument matcher
        when(mockedList.get(anyInt())).thenReturn("element");

        //stubbing using custom matcher (let's say isValid() returns your own matcher implementation):
        when(mockedList.contains(argThat(isValid()))).thenReturn(true);

        //following prints "element"
        System.out.println(mockedList.get(999));

        //you can also verify using an argument matcher
        verify(mockedList).get(anyInt());

        //argument matchers can also be written as Java 8 Lambdas
        verify(mockedList).add(argThat(someString -> someString.length() > 5));


//        verify(mock).someMethod(anyInt(), anyString(), eq("third argument"));
        //above is correct - eq() is also an argument matcher

//        verify(mock).someMethod(anyInt(), anyString(), "third argument");
        //above is incorrect - exception will be thrown because third argument is given without an argument matcher.

    }

    private ArgumentMatcher<String> isValid() {
        return s -> false;
    }

    /**
     * 验证调用的次数
     */
    @Test
    public void testVerifyNumber() {
        LinkedList<String> mockedList = mock(LinkedList.class);
        //using mock
        mockedList.add("once");

        mockedList.add("twice");
        mockedList.add("twice");

        mockedList.add("three times");
        mockedList.add("three times");
        mockedList.add("three times");

        //following two verifications work exactly the same - times(1) is used by default
        verify(mockedList).add("once");
        verify(mockedList, times(1)).add("once");

        //exact number of invocations verification
        verify(mockedList, times(2)).add("twice");
        verify(mockedList, times(3)).add("three times");

        //verification using never(). never() is an alias to times(0)
        verify(mockedList, never()).add("never happened");

        //verification using atLeast()/atMost()
        verify(mockedList, atMostOnce()).add("once");
        verify(mockedList, atLeastOnce()).add("three times");
        verify(mockedList, atLeast(2)).add("three times");
        verify(mockedList, atMost(5)).add("three times");
    }


    /**
     * 1. 使用存根的方法，执行抛出异常，当 mock 对象执行对用的方法的行为如 clear() 时。
     * 2. 执行真正对应的方法如 clear() 时
     */
    @Test
    public void testStubbingWithExceptions() {
        LinkedList<String> mockList = mock(LinkedList.class);
        doThrow(new RuntimeException()).when(mockList).clear();
        mockList.clear();
    }

    /**
     * InOrder 既可以针对对象的方法，也可以针对对象，必须按照顺序执行
     * 不一定执行所有的方法或者行为，可以根据特定的顺序去验证
     */
    @Test
    public void testVerifyOrder() {
        // A. Single mock whose methods must be invoked in a particular order
        List singleMock = mock(List.class);

        //using a single mock
        singleMock.add("was added first");
        singleMock.add("was added second");

        //create an inOrder verifier for a single mock
        InOrder inOrder = inOrder(singleMock);

        //following will make sure that add is first called with "was added first", then with "was added second"
        inOrder.verify(singleMock).add("was added first");
        inOrder.verify(singleMock).add("was added second");

        // B. Multiple mocks that must be used in a particular order
        List firstMock = mock(List.class);
        List secondMock = mock(List.class);

        //using mocks
        firstMock.add("was called first");
        secondMock.add("was called second");

        //create inOrder object passing any mocks that need to be verified in order
        InOrder inOrder2 = inOrder(firstMock, secondMock);

        //following will make sure that firstMock was called before secondMock
        inOrder2.verify(firstMock).add("was called first");
        inOrder2.verify(secondMock).add("was called second");

        // Oh, and A + B can be mixed together at will
    }


    /**
     * 确保某种行为在 mock 验证时从未发生
     */
    @Test
    public void testNever() {
        List mockOne = mock(List.class);
        //using mocks - only mockOne is interacted
        mockOne.add("one");

        //ordinary verification
        verify(mockOne).add("one");

        //verify that method was never called on a mock
        verify(mockOne, never()).add("two");

    }

    /**
     * 当验证完其中一个方法后，使用 verifyNoMoreInteractions 后，后面的方法都会失败
     */
    @Test
    public void testVerifyNoMoreInteractions() {
        List mockedList = mock(List.class);
        //using mocks
        mockedList.add("one");
        mockedList.add("two");

        verify(mockedList).add("one");

        //following verification will fail
        verifyNoMoreInteractions(mockedList);
    }

    @Test
    public void test() {
        Map<String, String> mockedList = mock(Map.class);
        when(mockedList.get("some arg"))
                .thenThrow(new RuntimeException())
                .thenReturn("foo");

        //First call: throws runtime exception:
        mockedList.get("some arg");

        //Second call: prints "foo"
        System.out.println(mockedList.get("some arg"));

        //Any consecutive call: prints "foo" as well (last stubbing wins).
        System.out.println(mockedList.get("some arg"));
    }


    /**
     * 链式调用
     */
    @Test
    public void testConsecutiveCall() {
        Map<String, String> mock = mock(Map.class);

        when(mock.get("some arg"))
                .thenThrow(new RuntimeException())
                .thenReturn("foo");

        //First call: throws runtime exception:
        try {
            mock.get("some arg");
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        //Second call: prints "foo"
        System.out.println(mock.get("some arg"));

        //Any consecutive call: prints "foo" as well (last stubbing wins).
        System.out.println(mock.get("some arg"));


        when(mock.get("some arg"))
                .thenReturn ("one", "two", "three");

        //All mock.someMethod("some arg") calls will return "two"
        when(mock.get("some arg"))
                .thenReturn("one");
        when(mock.get("some arg"))
                .thenReturn("two");
        System.out.println(mock.get("some arg"));
    }


    public void testCallback() {

    }
}