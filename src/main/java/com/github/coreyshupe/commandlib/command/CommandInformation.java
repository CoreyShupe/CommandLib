package com.github.coreyshupe.commandlib.command;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.immutables.value.Value;

/**
 * The information which a {@link Command} can hold.
 *
 * @author CoreyShupe, created on 2018/08/05
 * @param <I> The author typing of the information.
 */
@Value.Immutable
public interface CommandInformation<I> {
  /** @return The {@link String} name of the command. */
  String getName();

  /** @return The {@link Optional} wrapping the {@link String} description of the command. */
  Optional<String> getDescription();

  /** @return The {@link Set} of aliases for the command. */
  Set<String> getAliases();

  /**
   * An {@link ImmutableMap} of alternate information added by the user to contain things like
   * example usages.
   *
   * @return The {@link ImmutableMap} of alternate information.
   */
  ImmutableMap<String, String> alternateInformation();

  /**
   * An {@link List} holding all of the {@link CommandParameter}s for the command.
   *
   * @return The {@link List} of {@link CommandParameter}s.
   */
  List<CommandParameter<?>> getParameters();

  /**
   * @return The {@link Optional} wrapped {@link Predicate} to determine if the {@link I} author has
   *     permission for the command.
   */
  Optional<Predicate<I>> getPermissionPredicate();

  /**
   * @return The {@link Optional} wrapped {@link Consumer} to call if the {@link I} author does not
   *     have permission for the command.
   */
  Optional<Consumer<I>> getNoPermissionConsumer();

  /**
   * Attempts to retrieve information from {@link CommandInformation#alternateInformation()}.
   *
   * @param key The key to retrieve information.
   * @return The {@link Optional} wrapping the information retrieved from the key.
   */
  default Optional<String> requestInformation(String key) {
    Preconditions.checkNotNull(key, "The key for the information cannot be null.");
    return Optional.ofNullable(alternateInformation().get(key));
  }

  /**
   * Checks if a given command matches this command or any aliases.
   *
   * @param command The command to match against.
   * @return Whether or not the given command matches this.
   */
  default boolean checkAgainstCommand(String command) {
    Preconditions.checkNotNull(command, "A passed command cannot be null.");
    return getName().equalsIgnoreCase(command)
        || getAliases().stream().anyMatch(command::equalsIgnoreCase);
  }
}
