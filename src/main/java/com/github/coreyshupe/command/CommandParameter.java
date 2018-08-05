package com.github.coreyshupe.command;

import com.github.coreyshupe.command.exceptions.InvalidParameterException;
import com.github.coreyshupe.command.exceptions.ParameterNotFoundException;
import com.github.coreyshupe.command.parse.ClassParseContext;
import com.github.coreyshupe.command.parse.ClassParser;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Objects;

/**
 * A data-class for command parameters.
 *
 * @author CoreyShupe, created on 2018/08/01
 * @param <I> The typing of the parsed class.
 */
public class CommandParameter<I> {
  private final Class<I> type;
  private final boolean resetIfAbsent;
  private final boolean optional;
  private final int position;
  private final String[] defaultValue;

  public CommandParameter(
      Class<I> type,
      boolean resetIfAbsent,
      boolean optional,
      int position,
      String... defaultValue) {
    this.type = type;
    this.resetIfAbsent = resetIfAbsent;
    this.optional = optional;
    this.position = position;
    this.defaultValue = defaultValue;
  }

  public int getPosition() {
    return position;
  }

  public boolean isOptional() {
    return optional;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CommandParameter<?> parameter = (CommandParameter<?>) o;
    return resetIfAbsent == parameter.resetIfAbsent
        && optional == parameter.optional
        && position == parameter.position
        && Objects.equals(type, parameter.type)
        && Arrays.equals(defaultValue, parameter.defaultValue);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(type, resetIfAbsent, optional, position);
    result = 31 * result + Arrays.hashCode(defaultValue);
    return result;
  }

  @Override
  public String toString() {
    return "CommandParameter{"
        + "type="
        + type
        + ", resetIfAbsent="
        + resetIfAbsent
        + ", optional="
        + optional
        + ", position="
        + position
        + ", defaultValue="
        + (defaultValue == null ? null : Arrays.asList(defaultValue))
        + '}';
  }

  /**
   * Attempts to parse the parameter with the given context.
   *
   * @param context The context to be parsed.
   * @param parser The parser to parse the class.
   * @param <T> The type of author.
   * @return The item along with the existing {@link ArrayDeque}.
   */
  public <T> Pair<I, ArrayDeque<String>> parse(
      ClassParseContext<T> context, ClassParser<T> parser) {
    var copy = new ArrayDeque<String>();
    context.getContent().iterator().forEachRemaining(copy::addLast);
    if (parser.retrieveParser(type).isPresent()) {
      if (!context.hasMore()) {
        if (defaultValue == null || defaultValue.length == 0) {
          if (optional) {
            return Pair.of(null, new ArrayDeque<>());
          } else {
            throw new ParameterNotFoundException(
                "Parameter in position `%s` not found.", position + 1);
          }
        }
        return Pair.of(
            generatePossibleDefaultItem(context.getAuthor(), parser), new ArrayDeque<>());
      }
      var result = parser.parse(type, context);
      return result
          .map(x -> Pair.of(x, context.getContent()))
          .orElseGet(
              () ->
                  Pair.of(
                      generateAbsentItem(context.getAuthor(), parser, optional, copy),
                      resetIfAbsent ? copy : context.getContent()));
    } else {
      throw new InvalidParameterException(
          "Failed to parse `%s` as there's no valid parser for it.", type.getName());
    }
  }

  private <T> I generateAbsentItem(
      T author, ClassParser<T> parser, boolean optional, ArrayDeque<String> copy) {
    if (defaultValue != null && defaultValue.length != 0) {
      return generatePossibleDefaultItem(author, parser);
    }
    if (optional) {
      return null;
    } else {
      throw new InvalidParameterException(
          "Failed to parse `%s` in position `%s` with `%s`.",
          type.getName(), position + 1, String.join(" ", copy));
    }
  }

  private <T> I generatePossibleDefaultItem(T author, ClassParser<T> parser) {
    var defaultQueue = new ArrayDeque<>(Arrays.asList(defaultValue));
    var result = parser.parse(type, new ClassParseContext<>(author, defaultQueue));
    if (!result.isPresent()) {
      throw new IllegalStateException(
          String.format(
              "Default value `%s` failed to produce a valid object.",
              String.join(" ", defaultValue)));
    }
    return result.get();
  }
}
