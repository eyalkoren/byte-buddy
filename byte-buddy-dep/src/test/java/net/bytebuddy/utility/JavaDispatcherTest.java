package net.bytebuddy.utility;

import org.junit.Test;

import java.lang.reflect.Method;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class JavaDispatcherTest {

    @Test
    public void testStaticDispatcher() throws Exception {
        assertThat(JavaDispatcher.of(StaticSample.class).run().forName(Object.class.getName()), is((Object) Object.class));
    }

    @Test
    public void testStaticAdjustedDispatcher() throws Exception {
        assertThat(JavaDispatcher.of(StaticAdjustedSample.class).run().forName(Object.class.getName()), is((Object) Object.class));
    }

    @Test(expected = IllegalStateException.class)
    public void testStaticAdjustedIllegalDispatcher() throws Exception {
        assertThat(JavaDispatcher.of(StaticAdjustedIllegalSample.class).run().forName(null), is((Object) Object.class));
    }

    @Test
    public void testNonStaticDispatcher() throws Exception {
        assertThat(JavaDispatcher.of(NonStaticSample.class).run().getName(Object.class), is(Object.class.getName()));
    }

    @Test
    public void testNonStaticAdjustedDispatcher() throws Exception {
        assertThat(JavaDispatcher.of(NonStaticAdjustedSample.class).run().getMethod(Object.class, "equals", new Class<?>[]{Object.class}),
                is(Object.class.getMethod("equals", Object.class)));
    }

    @Test(expected = IllegalStateException.class)
    public void testNonStaticAdjustedIllegalDispatcher() throws Exception {
        JavaDispatcher.of(NonStaticAdjustedIllegalSample.class).run().getMethod(Object.class, "equals", null);
    }

    @Test
    public void testNonStaticRenamedDispatcher() throws Exception {
        assertThat(JavaDispatcher.of(NonStaticRenamedSample.class).run().getNameRenamed(Object.class), is(Object.class.getName()));
    }

    @Test
    public void testIsInstance() throws Exception {
        assertThat(JavaDispatcher.of(IsInstanceSample.class).run().isInstance(Object.class), is(true));
        assertThat(JavaDispatcher.of(IsInstanceSample.class).run().isInstance(new Object()), is(false));
    }

    @Test(expected = IllegalStateException.class)
    public void testIsInstanceIllegal() throws Exception {
        assertThat(JavaDispatcher.of(IsInstanceIllegalSample.class).run().isInstance(null), is(false));
    }

    @Test(expected = IllegalStateException.class)
    public void testNonExistentType() throws Exception {
        JavaDispatcher.of(NonExistentTypeSample.class).run().foo();
    }

    @Test(expected = IllegalStateException.class)
    public void testNonExistentMethod() throws Exception {
        JavaDispatcher.of(NonExistentMethodSample.class).run().foo();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNonInterface() throws Exception {
        JavaDispatcher.of(Object.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNonAnnotated() throws Exception {
        JavaDispatcher.of(Runnable.class);
    }

    @JavaDispatcher.Proxied("java.lang.Class")
    public interface StaticSample {

        @JavaDispatcher.Static
        Class<?> forName(String name);
    }

    @JavaDispatcher.Proxied("java.lang.Class")
    public interface StaticAdjustedSample {

        @JavaDispatcher.Static
        Class<?> forName(@JavaDispatcher.Proxied("java.lang.String") Object name);
    }

    @JavaDispatcher.Proxied("java.lang.Class")
    public interface StaticAdjustedIllegalSample {

        @JavaDispatcher.Static
        Class<?> forName(@JavaDispatcher.Proxied("java.lang.String") Void name);
    }

    @JavaDispatcher.Proxied("java.lang.Class")
    public interface NonStaticSample {

        String getName(Class<?> target);
    }

    @JavaDispatcher.Proxied("java.lang.Class")
    public interface NonStaticAdjustedSample {

        Method getMethod(Class<?> target,
                         @JavaDispatcher.Proxied("java.lang.String") Object name,
                         @JavaDispatcher.Proxied("java.lang.Class") Object[] argument);
    }

    @JavaDispatcher.Proxied("java.lang.Class")
    public interface NonStaticAdjustedIllegalSample {

        Method getMethod(Class<?> target,
                         @JavaDispatcher.Proxied("java.lang.String") Object name,
                         @JavaDispatcher.Proxied("java.lang.Class") Void[] argument);
    }

    @JavaDispatcher.Proxied("java.lang.Class")
    public interface NonStaticRenamedSample {

        @JavaDispatcher.Proxied("getName")
        String getNameRenamed(Class<?> target);
    }

    @JavaDispatcher.Proxied("java.lang.Class")
    public interface IsInstanceSample {

        @JavaDispatcher.Instance
        boolean isInstance(Object target);
    }

    @JavaDispatcher.Proxied("java.lang.Class")
    public interface IsInstanceIllegalSample {

        @JavaDispatcher.Instance
        boolean isInstance(Void target);
    }

    @JavaDispatcher.Proxied("does.not.Exist")
    public interface NonExistentTypeSample {

        void foo();
    }

    @JavaDispatcher.Proxied("java.lang.Object")
    public interface NonExistentMethodSample {

        void foo();
    }
}