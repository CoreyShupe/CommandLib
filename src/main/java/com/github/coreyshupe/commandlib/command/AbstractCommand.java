package com.github.coreyshupe.commandlib.command;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public abstract class AbstractCommand<I> implements BiConsumer<I, String> {

  private final String command;
  private final String description;
  private final Predicate<I> permissionChecker;
  private final Set<String> aliases;

  public AbstractCommand(String command, String description, Set<String> aliases) {
    this(command, description, (x) -> true, aliases);
  }

  public AbstractCommand(
      String command, String description, Predicate<I> permissionChecker, Set<String> aliases) {
    this.command = command;
    this.description = description;
    this.permissionChecker = permissionChecker;
    this.aliases = aliases;
  }

  public boolean hasPermission(I author) {
    return permissionChecker.test(author);
  }

  public String getCommand() {
    return command;
  }

  public String getDescription() {
    return description;
  }

  public Set<String> getAliases() {
    return aliases;
  }

  public boolean checkCommand(String command) {
    return this.command.equalsIgnoreCase(command)
        || aliases.stream().anyMatch(command::equalsIgnoreCase);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AbstractCommand<?> that = (AbstractCommand<?>) o;
    return Objects.equals(command, that.command)
        && Objects.equals(description, that.description)
        && Objects.equals(permissionChecker, that.permissionChecker)
        && Objects.equals(aliases, that.aliases);
  }

  @Override
  public int hashCode() {
    return Objects.hash(command, description, permissionChecker, aliases);
  }

  public static <T> AbstractCommand<T> newCommand(
      Class<T> authorClass,
      Command command,
      Predicate<T> permissionChecker,
      BiConsumer<T, String> actionConsumer) {
    return new AbstractCommand<>(
        command.value(),
        command.description(),
        permissionChecker,
        new HashSet<>(Arrays.asList(command.aliases()))) {
      @Override
      public void accept(T author, String content) {
        actionConsumer.accept(author, content);
      }
    };
  }
}
