package com.github.coreyshupe.command.exceptions;

/**
 * A {@link RuntimeException} which is used for a parameter which cannot be parsed.
 *
 * @author CoreyShupe, created on 2018/08/01
 * @see RuntimeException
 */
public class InvalidParameterException extends RuntimeException {
  public InvalidParameterException(String message, Object... args) {
    super(String.format(message, args));
  }
}
