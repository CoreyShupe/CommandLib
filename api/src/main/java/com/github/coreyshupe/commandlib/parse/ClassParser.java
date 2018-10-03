package com.github.coreyshupe.commandlib.parse;

import java.util.Optional;
import java.util.function.Function;

/**
 * An {@code interface} for parsing classes.
 *
 * @author CoreyShupe, created on 2018/08/05
 * @param <I> Context type to parse with.
 */
public interface ClassParser<I> {
  <T> void applyParser(Class<T> clazz, Function<I, T> parsingFunction);

  <T> Optional<T> parse(Class<T> clazz, I context);

  <T> Optional<Function<I, T>> getParser(Class<T> clazz);
}
