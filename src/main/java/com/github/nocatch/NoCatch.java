package com.github.nocatch;

import java.lang.reflect.Constructor;
import java.util.concurrent.Callable;

public final class NoCatch {

  public interface NoCatchRunnable {

    void run() throws Exception;
  }

  /**
   * Specifies the default runtime exception used to wrap checked exceptions.
   */
  private static Class<? extends RuntimeException> DEFAULT_WRAPPER_EXCEPTION = NoCatchException.class;

  private NoCatch() {
    throw new AssertionError("Not instantiatable");
  }

  public static void setDefaultWrapperException(Class<? extends RuntimeException> defaultWrapperException) {
    DEFAULT_WRAPPER_EXCEPTION = defaultWrapperException;
  }

  /**
   * Runs the specified runnable and wraps any checked exception into a RuntimeException.
   *
   * @param runnable the runnable to run
   *
   * @throws RuntimeException if any checked exception is thrown
   */
  public static void noCatch(NoCatchRunnable runnable) {
    noCatch(runnable, DEFAULT_WRAPPER_EXCEPTION);
  }

  /**
   * Runs the specified runnable and wraps any checked exception into a RuntimeException.
   *
   * @param runnable the runnable to run
   * @param wrapperException a RuntimeException to use to wrap any thrown exception in. The wrapper exception
   * <strong>must</strong> provide a constructor that takes a cause as paramter and it <strong>must</strong> be a static
   * class.
   *
   * @throws NoCatchException if any checked exception occurred
   */
  public static void noCatch(NoCatchRunnable runnable, Class<? extends RuntimeException> wrapperException) {
    try {
      runnable.run();
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw wrapException(e, wrapperException);
    }
  }

  private static RuntimeException wrapException(Exception exception, Class<? extends RuntimeException> wrapperException) {
    try {
      Constructor<? extends RuntimeException> constructor = wrapperException.getDeclaredConstructor(Throwable.class);
      constructor.setAccessible(true);
      throw constructor.newInstance(exception);
    } catch (ReflectiveOperationException e) {
      NoCatchException fallbackException = new NoCatchException("WARN: Could not wrap thrown exception using the " +
          "specified wrapper (" + wrapperException.getName() + ". Your wrapper needs to have a constructor that takes a " +
          "cause.", exception);
      fallbackException.addSuppressed(e);
      throw fallbackException;
    }
  }

  /**
   * Calls the specified callable and wraps any checked exception into a RuntimeException.
   *
   * @param callable the callable to call
   * @param <T> the callable's return type
   *
   * @return the result of the callable
   *
   * @throws NoCatchException if any checked exception is thrown
   */
  public static <T> T noCatch(Callable<T> callable) {
    return noCatch(callable, DEFAULT_WRAPPER_EXCEPTION);
  }

  /**
   * Calls the specified callable and wraps any checked exception into a RuntimeException.
   *
   * @param callable the callable to call
   * @param wrapperException a RuntimeException to use to wrap any thrown exception in. The wrapper exception
   * <strong>must</strong> provide a constructor that takes a cause as paramter and it <strong>must</strong> be a static
   * class.
   * @param <T> the callable's return type
   *
   * @return the result of the callable
   *
   * @throws NoCatchException if any checked exception occurred
   */
  public static <T> T noCatch(Callable<T> callable, Class<? extends RuntimeException> wrapperException) {
    try {
      return callable.call();
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw wrapException(e, wrapperException);
    }
  }
}
