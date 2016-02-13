package com.github.nocatch;

/**
 * Default wrapper exception for any checked exception. Also thrown if the API isn't used correctly.
 */
public class NoCatchException extends RuntimeException {

  public NoCatchException(String message) {
    super(message);
  }

  public NoCatchException(Throwable cause) {
    super(cause);
  }

  public NoCatchException(String message, Throwable cause) {
    super(message, cause);
  }
}
