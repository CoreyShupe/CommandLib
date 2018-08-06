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
public interface CommandParameter<T> {
  /** @return The {@link Class} type of the parameter. */
  Class<T> getType();

  /** @return The {@link Optional} wrapping the {@link String} description of the parameter. */
  Optional<String> getDescription();

  /** @return The {@link List} of {@link String}s which parse into the default value. */
  Optional<String> getDefaultValue();

  /** @return The optional state of the parameter. */
  boolean getOptional();

  /** @return The state to determine if the content should be reset if absent. */
  boolean getResetIfAbsent();
}
