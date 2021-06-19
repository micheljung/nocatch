package com.github.nocatch;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;

import static com.github.nocatch.NoCatch.noCatch;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

public class NoCatchTest {

  public static class WrapperException extends RuntimeException {

    public WrapperException(Throwable cause) {
      super(cause);
    }
  }

  public static class InvalidWrapperException extends RuntimeException {
    // Because it has no 'cause' constructor
  }

  @Test
  public void testNoCatchRunnableRunsFine() {
    noCatch(() -> System.out.println(new URL("http://www.github.com")));
  }

  @Test
  public void testNoCatchCallableRunsFine() {
    URL url = noCatch(() -> new URL("http://www.github.com"));
    assertThat(url.toString(), is("http://www.github.com"));
  }

  @Test
  public void testNoCatchRunnableDoesntWrapRuntimeException() {
    assertThrows(IllegalStateException.class, () -> noCatch((NoCatch.NoCatchRunnable) () -> {
      throw new IllegalStateException();
    }));
  }

  @Test
  public void testNoCatchCallableDoesntWrapRuntimeException() {
    assertThrows(IllegalStateException.class, () -> noCatch(() -> {
      throw new IllegalStateException();
    }));
  }

  @Test
  public void testNoCatchRunnableWithCustomWrapperRunsFine() {
    noCatch(() -> System.out.println(new URL("http://www.github.com")), WrapperException.class);
  }

  @Test
  public void testNoCatchCallableCustomWrapperRunsFine() {
    URL url = noCatch(() -> new URL("http://www.github.com"), WrapperException.class);
    assertThat(url.toString(), is("http://www.github.com"));
  }

  @Test
  public void testNoCatchRunnable() {
    RuntimeException exception = assertThrows(
      RuntimeException.class,
      () -> noCatch(() -> System.out.println(new URL(";")))
    );
    assertThat(exception.getCause(), instanceOf(MalformedURLException.class));
  }

  @Test
  public void testNoCatchRunnableWrapperException() {
    WrapperException exception = assertThrows(
      WrapperException.class,
      () -> noCatch(() -> System.out.println(new URL(";")), WrapperException.class)
    );
    assertThat(exception.getCause(), instanceOf(MalformedURLException.class));
  }

  @Test
  public void testNoCatchRunnableInvalidWrapperException() {
    NoCatchException exception = assertThrows(
      NoCatchException.class,
      () -> noCatch(() -> System.out.println(new URL(";")), InvalidWrapperException.class)
    );
    assertThat(exception.getCause(), instanceOf(MalformedURLException.class));
  }

  @Test
  public void testNoCatchCallable() {
    RuntimeException exception = assertThrows(
      RuntimeException.class,
      () -> noCatch(() -> new URL(";"))
    );
    assertThat(exception.getCause(), instanceOf(MalformedURLException.class));
  }

  @Test
  public void testNoCatchCallableWrapperException() {
    WrapperException exception = assertThrows(
      WrapperException.class,
      () -> noCatch(() -> new URL(";"), WrapperException.class)
    );
    assertThat(exception.getCause(), instanceOf(MalformedURLException.class));
  }

  @Test
  public void testNoCatchCallableCustomWrapperException() {
    NoCatch.setDefaultWrapperException(RuntimeException.class);
    RuntimeException exception = assertThrows(
      RuntimeException.class,
      () -> noCatch(() -> new URL(";"))
    );
    assertThat(exception.getCause(), instanceOf(MalformedURLException.class));
    NoCatch.setDefaultWrapperException(NoCatchException.class);
  }

  @Test
  public void testNoCatchCallableInvalidWrapperException() {
    NoCatchException exception = assertThrows(
      NoCatchException.class,
      () -> noCatch(() -> new URL(";"), InvalidWrapperException.class)
    );
    assertThat(exception.getCause(), instanceOf(MalformedURLException.class));
  }

  @Test
  public void testIsUtilityClass() {
    assertTrue(Modifier.isFinal(NoCatch.class.getModifiers()));

    Constructor<?>[] constructors = NoCatch.class.getDeclaredConstructors();
    assertEquals(constructors.length, 1);

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
