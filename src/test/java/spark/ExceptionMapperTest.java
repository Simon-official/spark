package spark;

import org.junit.Test;
import org.powermock.reflect.Whitebox;

import java.util.IllegalFormatException;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ExceptionMapperTest {


    @Test
    public void testGetInstance_whenDefaultInstanceIsNull() {
        //given
        ExceptionMapper exceptionMapper = null;
        Whitebox.setInternalState(ExceptionMapper.class, "servletInstance", exceptionMapper);

        //then
        exceptionMapper = ExceptionMapper.getServletInstance();
        assertEquals("Should be equals because ExceptionMapper is a singleton", Whitebox.getInternalState(ExceptionMapper.class, "servletInstance"), exceptionMapper);
    }

    @Test
    public void testGetInstance_whenDefaultInstanceIsNotNull() {
        //given
        ExceptionMapper.getServletInstance(); //initialize Singleton

        //then
        ExceptionMapper exceptionMapper = ExceptionMapper.getServletInstance();
        assertEquals("Should be equals because ExceptionMapper is a singleton", Whitebox.getInternalState(ExceptionMapper.class, "servletInstance"), exceptionMapper);
    }

    @Test
    public void testAddHandler() {
        ExceptionMapper exceptionMapper = ExceptionMapper.getServletInstance();

        ExceptionHandlerImpl handler = new ExceptionHandlerImpl(IllegalArgumentException.class) {
            @Override
            public void handle(Exception exception, Request request, Response response) {
                //unused
            }
        };
        exceptionMapper.map(IllegalArgumentException.class, handler);

        Map<Class<? extends Exception>, ExceptionHandlerImpl> internalMap =
            Whitebox.getInternalState(exceptionMapper, "exceptionMap");

        assertEquals("Should have a size of 1", 1, internalMap.size());
        assertEquals("Only key should be IllegalArgumentException.class",
            IllegalArgumentException.class, internalMap.keySet().iterator().next());
        assertEquals("Value of IllegalArgumentException.class should be the previously defined handler",
            handler, exceptionMapper.getHandler(IllegalArgumentException.class));
    }

    @Test
    public void testAddHandlerWithSuperclasses() {
        ExceptionMapper exceptionMapper = ExceptionMapper.getServletInstance();

        ExceptionHandlerImpl handler = new ExceptionHandlerImpl(RuntimeException.class) {
            @Override
            public void handle(Exception exception, Request request, Response response) {
                //unused
            }
        };
        exceptionMapper.map(RuntimeException.class, handler);

        Map<Class<? extends Exception>, ExceptionHandlerImpl> internalMap =
            Whitebox.getInternalState(exceptionMapper, "exceptionMap");

        assertEquals("Should have a size of 1", 1, internalMap.size());
        assertEquals("Only key should be RuntimeException.class",
            RuntimeException.class, internalMap.keySet().iterator().next());

        assertEquals("Value should be the previously defined handler", handler,
            exceptionMapper.getHandler(RuntimeException.class));
        assertEquals("Value should be the previously defined handler because the given class is a subclass of RuntimeException",
            handler, exceptionMapper.getHandler(IllegalArgumentException.class));
        assertEquals("Value should be the previously defined handler because the given class is a subclass of the subclass",
            handler, exceptionMapper.getHandler(IllegalFormatException.class));
    }

    @Test
    public void testAddAndRemoveHandlerWithSuperclasses() {
        ExceptionMapper exceptionMapper = ExceptionMapper.getServletInstance();

        ExceptionHandlerImpl handler = new ExceptionHandlerImpl(RuntimeException.class) {
            @Override
            public void handle(Exception exception, Request request, Response response) {
                //unused
            }
        };
        exceptionMapper.map(RuntimeException.class, handler);

        Map<Class<? extends Exception>, ExceptionHandlerImpl> internalMap =
            Whitebox.getInternalState(exceptionMapper, "exceptionMap");

        assertEquals("Should have a size of 1", 1, internalMap.size());
        assertEquals("Only key should be RuntimeException.class",
            RuntimeException.class, internalMap.keySet().iterator().next());

        assertEquals("Value should be the previously defined handler", handler,
            exceptionMapper.getHandler(RuntimeException.class));
        assertEquals("Value should be the previously defined handler because the given class is a subclass of RuntimeException",
            handler, exceptionMapper.getHandler(IllegalArgumentException.class));
        assertEquals("Value should be the previously defined handler because the given class is a subclass of the subclass",
            handler, exceptionMapper.getHandler(IllegalFormatException.class));

        exceptionMapper.remove(RuntimeException.class);

        assertEquals("Value should be the previously defined handler because the given class is a subclass of RuntimeException",
            handler, exceptionMapper.getHandler(IllegalArgumentException.class));
        assertEquals("Value should be the previously defined handler because the given class is a subclass of the subclass",
            handler, exceptionMapper.getHandler(IllegalFormatException.class));

        exceptionMapper.remove(IllegalFormatException.class);

        assertEquals("Should have a size of 0 because the lowest class in the hierarchy was removed",
            0, internalMap.size());
    }

    @Test
    public void testClearHandler() {
        ExceptionMapper exceptionMapper = ExceptionMapper.getServletInstance();

        exceptionMapper.map(IllegalArgumentException.class, new ExceptionHandlerImpl(IllegalArgumentException.class) {
            @Override
            public void handle(Exception exception, Request request, Response response) {
                //unused
            }
        });

        exceptionMapper.map(IndexOutOfBoundsException.class, new ExceptionHandlerImpl(IndexOutOfBoundsException.class) {
            @Override
            public void handle(Exception exception, Request request, Response response) {
                //unused
            }
        });
        Map<Class<? extends Exception>, ExceptionHandlerImpl> internalMap =
            Whitebox.getInternalState(exceptionMapper, "exceptionMap");

        assertEquals("Should have a size of 2", 2, internalMap.size());
        exceptionMapper.clear();
        assertEquals("Should have a size of 0", 0, internalMap.size());
    }

    @Test
    public void testRemoveHandler() {
        ExceptionMapper exceptionMapper = ExceptionMapper.getServletInstance();

        ExceptionHandlerImpl handler = new ExceptionHandlerImpl(IllegalArgumentException.class) {
            @Override
            public void handle(Exception exception, Request request, Response response) {
                //unused
            }
        };
        exceptionMapper.map(IllegalArgumentException.class, handler);

        exceptionMapper.map(IndexOutOfBoundsException.class, new ExceptionHandlerImpl(IndexOutOfBoundsException.class) {
            @Override
            public void handle(Exception exception, Request request, Response response) {
                //unused
            }
        });
        Map<Class<? extends Exception>, ExceptionHandlerImpl> internalMap =
            Whitebox.getInternalState(exceptionMapper, "exceptionMap");

        assertEquals("Should have a size of 2", 2, internalMap.size());

        exceptionMapper.remove(IndexOutOfBoundsException.class);

        assertEquals("Should have a size of 1", 1, internalMap.size());
        assertEquals("Only key should be IllegalArgumentException.class",
            IllegalArgumentException.class, internalMap.keySet().iterator().next());
        assertEquals("Value of IllegalArgumentException.class should be the previously defined handler",
            handler, exceptionMapper.getHandler(IllegalArgumentException.class));

        exceptionMapper.remove(IllegalArgumentException.class);

        assertEquals("Should have a size of 0", 0, internalMap.size());
    }

}
