package com.github.coreyshupe.commandlib.parse;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ClassParser<I> {

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
        if (!s.matches("(-?)(0|([1-9][0-9]*))(\\.[0-9]+)?$")) {
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
  private BiFunction<String, I, Optional<?>> defaultParser;

  public ClassParser() {
    this((content, author) -> Optional.of(content));
  }

  public ClassParser(BiFunction<String, I, Optional<?>> defaultParser) {
    this.defaultParser = defaultParser;
    applyParser(Integer.class, (content, context) -> INTEGER_PARSER.apply(content));
    applyParser(Double.class, (content, context) -> DOUBLE_PARSER.apply(content));
    applyParser(Long.class, (content, context) -> LONG_PARSER.apply(content));
  }

  public <T> void applyParser(Class<T> clazz, ClassParseFunction<I, T> function) {
    parseFunctionMap.put(clazz, function);
  }

  public void setDefaultParser(BiFunction<String, I, Optional<?>> function) {
    defaultParser = function;
  }

  public BiFunction<String, I, Optional<?>> getDefaultParser() {
    return defaultParser;
  }

  public ArrayDeque<Optional<?>> parse(I author, String content, Class<?>[] classes) {
    var contentArray = content.split("\\.");
    var contentQueue = new ArrayDeque<>(Arrays.asList(contentArray));
    var context = new ClassParseContext<>(author, contentQueue);
    var parsed = new ArrayDeque<Optional<?>>();
    for (var clazz : classes) {
      if (contentQueue.isEmpty()) {
        throw new IllegalStateException("Queue emptied before all classes were parsed.");
      }
      if (parseFunctionMap.containsKey(clazz)) {
        parsed.addLast(parseFunctionMap.get(clazz).apply(contentQueue.pollFirst(), context));
        continue;
      }
      throw new IllegalArgumentException("Failed to parse class `" + clazz.getName() + "`.");
    }
    contentQueue
        .iterator()
        .forEachRemaining(s -> parsed.addLast(defaultParser.apply(content, author)));
    return parsed;
  }
}
