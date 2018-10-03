package com.github.coreyshupe.commandlib.parse;

import java.util.function.Function;
import java.util.function.Supplier;

/** @author CoreyShupe, created on 2018/08/05 */
public final class DefaultParser {
  public static final Function<String, String> STRING_PARSER = (s) -> s;
  public static final Function<String, Integer> INTEGER_PARSER =
      (s) -> {
        if (!s.matches("\\d+")) {
          return null;
        }
        try {
          return Integer.parseInt(s);
        } catch (NumberFormatException ex) {
          return null;
        }
      };
  public static final Function<String, Double> DOUBLE_PARSER =
      (s) -> {
        if (!s.matches("(-?)([0-9]*)(\\.[0-9]+)?$")) {
          return null;
        }
        return Double.parseDouble(s);
      };
  public static final Function<String, Long> LONG_PARSER =
      (s) -> {
        if (!s.matches("\\d+")) {
          return null;
        }
        try {
          return Long.parseLong(s);
        } catch (NumberFormatException ex) {
          return null;
        }
      };

  public static <K extends Supplier<String>, V> Function<K, V> generateParser(
      Function<String, V> function) {
    return (k) -> function.apply(k.get());
  }

  private DefaultParser() {}
}
