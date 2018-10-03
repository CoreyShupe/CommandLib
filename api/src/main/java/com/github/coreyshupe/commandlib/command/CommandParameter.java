package com.github.coreyshupe.commandlib.command;

import java.util.List;
import java.util.Optional;
import org.immutables.value.Value;

/**
 * A single parameter for a {@link Command}.
 *
 * @author CoreyShupe, created on 2018/08/05
 */
@Value.Immutable
public interface CommandParameter<I> {
  /** @return The {@link Class} type of the parameter. */
  Class<I> getType();

  /** @return The {@link Optional} wrapping the {@link String} description of the parameter. */
  Optional<String> getDescription();

  /** @return The {@link List} of {@link String}s which parse into the default value. */
  Optional<String> getDefaultValue();

  /** @return The optional state of the parameter. */
  Optional<Boolean> getOptional();

  /** @return The state to determine if the content should be reset if absent. */
  Optional<Boolean> getResetIfAbsent();

  static <T> ImmutableCommandParameter.Builder<T> ofBuilder(Class<T> clazz) {
    return ImmutableCommandParameter.<T>builder().type(clazz);
  }
}
