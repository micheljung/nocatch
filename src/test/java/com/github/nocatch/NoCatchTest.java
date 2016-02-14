package com.github.nocatch;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;

import static com.github.nocatch.NoCatch.noCatch;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class NoCatchTest {

  public static class WrapperException extends RuntimeException {

    public WrapperException(Throwable cause) {
      super(cause);
    }
  }

  public static class InvalidWrapperException extends RuntimeException {
    // Because it has no 'cause' constructor
  }

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void testNoCatchRunnableRunsFine() throws Exception {
    noCatch(() -> System.out.println(new URL("http://www.github.com")));
  }

  @Test
  public void testNoCatchCallableRunsFine() throws Exception {
    URL url = noCatch(() -> new URL("http://www.github.com"));
    assertThat(url.toString(), is("http://www.github.com"));
  }

  @Test
  public void testNoCatchRunnableDoesntWrapRuntimeException() {
    expectedException.expect(IllegalStateException.class);

    noCatch((NoCatch.NoCatchRunnable) () -> {
      throw new IllegalStateException();
    });
  }

  @Test
  public void testNoCatchCallableDoesntWrapRuntimeException() {
    expectedException.expect(IllegalStateException.class);

    noCatch(() -> {
      throw new IllegalStateException();
    });
  }

  @Test
  public void testNoCatchRunnableWithCustomWrapperRunsFine() throws Exception {
    noCatch(() -> System.out.println(new URL("http://www.github.com")), WrapperException.class);
  }

  @Test
  public void testNoCatchCallableCustomWrapperRunsFine() throws Exception {
    URL url = noCatch(() -> new URL("http://www.github.com"), WrapperException.class);
    assertThat(url.toString(), is("http://www.github.com"));
  }

  @Test
  public void testNoCatchRunnable() throws Exception {
    expectedException.expect(RuntimeException.class);
    expectedException.expectCause(isA(MalformedURLException.class));

    noCatch(() -> System.out.println(new URL(";")));
  }

  @Test
  public void testNoCatchRunnableWrapperException() throws Exception {
    expectedException.expect(WrapperException.class);
    expectedException.expectCause(isA(MalformedURLException.class));

    noCatch(() -> System.out.println(new URL(";")), WrapperException.class);
  }

  @Test
  public void testNoCatchRunnableInvalidWrapperException() throws Exception {
    expectedException.expect(NoCatchException.class);
    expectedException.expectCause(isA(MalformedURLException.class));

    noCatch(() -> System.out.println(new URL(";")), InvalidWrapperException.class);
  }

  @Test
  public void testNoCatchCallable() throws Exception {
    expectedException.expect(RuntimeException.class);
    expectedException.expectCause(isA(MalformedURLException.class));

    noCatch(() -> new URL(";"));
  }

  @Test
  public void testNoCatchCallableWrapperException() throws Exception {
    expectedException.expect(WrapperException.class);
    expectedException.expectCause(isA(MalformedURLException.class));

    noCatch(() -> new URL(";"), WrapperException.class);
  }

  @Test
  public void testNoCatchCallableCustomWrapperException() throws Exception {
    NoCatch.setDefaultWrapperException(RuntimeException.class);
    expectedException.expect(RuntimeException.class);
    expectedException.expectCause(isA(MalformedURLException.class));

    noCatch(() -> new URL(";"));
    NoCatch.setDefaultWrapperException(NoCatchException.class);
  }

  @Test
  public void testNoCatchCallableInvalidWrapperException() throws Exception {
    expectedException.expect(NoCatchException.class);
    expectedException.expectCause(isA(MalformedURLException.class));

    noCatch(() -> new URL(";"), InvalidWrapperException.class);
  }

  @Test
  public void testIsUtilityClass() throws Exception {
    assertTrue(Modifier.isFinal(NoCatch.class.getModifiers()));

    Constructor<?>[] constructors = NoCatch.class.getDeclaredConstructors();
    assertTrue(constructors.length == 1);

    Constructor<?> constructor = constructors[0];
    try {
      constructor.setAccessible(true);
      constructor.newInstance();
    } catch (ReflectiveOperationException e) {
      // expected
      return;
    }
    fail("Constructor did not throw an exception");
  }
}
