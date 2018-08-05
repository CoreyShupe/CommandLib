package com.github.coreyshupe.command.parse;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Parses classes with a given author and context using a {@link ClassParseFunction}.
 *
 * @param <I> The type of author.
 * @author CoreyShupe, created on 2018/08/01
 */
public class ClassParser<I> {

  private static final Function<String, Optional<String>> STRING_PARSER = (s) -> Optional.of(s);
  private static final Function<String, Optional<Integer>> INTEGER_PARSER =
      (s) -> {
        if (!s.matches("\\d+")) {
          return Optional.empty();
        }
        try {
          return Optional.of(Integer.parseInt(s));
        } catch (NumberFormatException ex) {
          return Optional.empty();
        }
      };
  private static final Function<String, Optional<Double>> DOUBLE_PARSER =
      (s) -> {
        if (!s.matches("(-?)([0-9]*)(\\.[0-9]+)?$")) {
          return Optional.empty();
        }
        return Optional.of(Double.parseDouble(s));
      };
  private static final Function<String, Optional<Long>> LONG_PARSER =
      (s) -> {
        if (!s.matches("\\d+")) {
          return Optional.empty();
        }
        try {
          return Optional.of(Long.parseLong(s));
        } catch (NumberFormatException ex) {
          return Optional.empty();
        }
      };

  private final Map<Class<?>, ClassParseFunction<I, ?>> parseFunctionMap = new IdentityHashMap<>();

  public ClassParser() {
    applyParser(String.class, (content, context) -> STRING_PARSER.apply(content));
    applyParser(Integer.class, (content, context) -> INTEGER_PARSER.apply(content));
    applyParser(Double.class, (content, context) -> DOUBLE_PARSER.apply(content));
    applyParser(Long.class, (content, context) -> LONG_PARSER.apply(content));
  }

  public <T> void applyParser(Class<T> clazz, ClassParseFunction<I, T> function) {
    parseFunctionMap.put(clazz, function);
  }

  public <T> Optional<T> parse(Class<T> clazz, ClassParseContext<I> context) {
    return retrieveParser(clazz)
        .flatMap(parser -> context.nextPiece().flatMap(content -> parser.apply(content, context)));
  }

  @SuppressWarnings("unchecked")
  public <T> Optional<ClassParseFunction<I, T>> retrieveParser(Class<T> clazz) {
    ClassParseFunction<I, ?> unknownFunction = parseFunctionMap.get(clazz);
    if (unknownFunction == null) {
      return Optional.empty();
    }
    return Optional.of((ClassParseFunction<I, T>) unknownFunction);
  }
}
