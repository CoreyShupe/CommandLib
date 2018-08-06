package com.github.coreyshupe.commandlib.command;

import com.google.common.base.Preconditions;
import java.util.List;
import java.util.Optional;
import org.immutables.value.Value;

/**
 * The event passed to a {@link Command} when it's executed.
 *
 * @author CoreyShupe, created on 2018/08/05
 */
@Value.Immutable
public interface CommandEvent<I> {
  /** @return The {@link I} author of the command. */
  I getAuthor();

  /** @return The pre-parsed parameters. */
  List<Optional<?>> getParameterObjects();

  /** @return The extra strings presented in the command. */
  String[] getExtra();

  /**
   * Cast the parameter to {@link T} from {@link #getParameterObjects()} based on the {@code int}
   * index given.
   *
   * @param clazz The expected {@link Class} of the parameter.
   * @param index The index of the parameter.
   * @param <T> The typing of the parameter.
   * @return The {@link Optional} wrapped {@link T} item.
   */
  default <T> Optional<T> expectOptional(Class<T> clazz, int index) {
    Preconditions.checkNotNull(clazz, "The casting class cannot be null.");
    return getParameterObjects().get(index).map(object -> clazz.cast(object));
  }

  /**
   * Return an unwrapped version of {@link #expectOptional(Class, int)}.
   *
   * @param clazz The {@link Class} of the parameter.
   * @param index The index of the parameter.
   * @param <T> The typing of the parameter.
   * @return The unwrapped {@link T} item.
   * @throws IllegalStateException When the expected non-optional parameter is not present.
   */
  default <T> T expectNonOptional(Class<T> clazz, int index) {
    return expectOptional(clazz, index)
        .orElseThrow(
            () ->
                new IllegalStateException(
                    String.format(
                        "Expected non-optional parameter for `%s` but was not present.", index)));
  }
}
