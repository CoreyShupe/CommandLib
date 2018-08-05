package com.github.coreyshupe.command.exceptions;

/**
 * A {@link RuntimeException} which is used for displaying when a command could not be found.
 *
 * @author CoreyShupe, created on 2018/08/04
 * @see RuntimeException
 */
public class CommandNotFoundException extends RuntimeException {
  public CommandNotFoundException(String message, Object... args) {
    super(String.format(message, args));
  }
}
