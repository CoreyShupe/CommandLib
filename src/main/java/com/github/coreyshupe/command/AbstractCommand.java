package com.github.coreyshupe.command;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * A data-holding class which consumes a {@link String} command with the {@link I} author.
 *
 * @param <I> The author typing for the command.
 * @author CoreyShupe, created on 2018/08/01
 * @see BiConsumer
 */
public abstract class AbstractCommand<I> implements BiConsumer<I, String> {

  private final String command;
  private final Set<String> aliases;
  private final String description;
  private final String[] paramDescriptors;

  public AbstractCommand(
      String command, String[] aliases, String description, String... paramDescriptors) {
    this(command, Arrays.asList(aliases), description, paramDescriptors);
  }

  public AbstractCommand(
      String command, Collection<String> aliases, String description, String... paramDescriptors) {
    this.command = command;
    this.aliases = new HashSet<>(aliases);
    this.description = description;
    this.paramDescriptors = paramDescriptors;
  }

  public String[] getParamDescriptors() {
    return paramDescriptors;
  }

  /** @return The command name. */
  public String getCommand() {
    return command;
  }

  /** @return The aliases of the command. */
  public Set<String> getAliases() {
    return aliases;
  }

  /** @return The description of the command. */
  public String getDescription() {
    return description;
  }

  /**
   * Checks if a given string matches this command or any of its aliases.
   *
   * @param that The given string to match.
   * @return True if the given string matches the command or any alias.
   */
  public boolean matchesCommand(String that) {
    return this.command.equalsIgnoreCase(that) || aliases.stream().anyMatch(that::equalsIgnoreCase);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!AbstractCommand.class.isInstance(o)) return false;
    var that = (AbstractCommand<?>) o;
    return Objects.equals(command, that.command)
        && Arrays.equals(aliases.toArray(), that.aliases.toArray())
        && Objects.equals(description, that.description);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(command, description);
    result = 31 * result + Arrays.hashCode(aliases.toArray());
    return result;
  }

  @Override
  public String toString() {
    return "AbstractCommand{"
        + "command='"
        + command
        + '\''
        + ", aliases="
        + aliases
        + ", description='"
        + description
        + '\''
        + '}';
  }
}
