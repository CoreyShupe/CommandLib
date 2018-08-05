package com.github.coreyshupe.command.exceptions;

/**
 * A {@link RuntimeException} which is used for a parameter which couldn't be found.
 *
 * @author CoreyShupe, created on 2018/08/04
 * @see RuntimeException
 */
public class ParameterNotFoundException extends RuntimeException {
  public ParameterNotFoundException(String message, Object... args) {
    super(String.format(message, args));
  }
}
