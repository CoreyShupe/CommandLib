package com.github.coreyshupe.commandlib.parse;

import com.github.coreyshupe.commandlib.command.CommandParameter;
import com.github.coreyshupe.commandlib.utility.Pair;
import com.github.coreyshupe.commandlib.utility.PairTuple;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * {@link ClassParser} for commands using a {@link CommandParseContext} to parse.
 *
 * @param <I> The author typing.
 * @author CoreyShupe, created on 2018/08/05
 * @see ClassParser
 */
public class CommandParameterParser<I> implements ClassParser<CommandParseContext<I>> {
  private final Map<Class<?>, Function<CommandParseContext<I>, ?>> parserMap;

  public CommandParameterParser() {
    this.parserMap = new IdentityHashMap<>();
    applyParser(String.class, DefaultParser.generateParser(DefaultParser.STRING_PARSER));
    applyParser(Integer.class, DefaultParser.generateParser(DefaultParser.INTEGER_PARSER));
    applyParser(Double.class, DefaultParser.generateParser(DefaultParser.DOUBLE_PARSER));
    applyParser(Long.class, DefaultParser.generateParser(DefaultParser.LONG_PARSER));
  }

  /** {@inheritDoc} */
  @Override
  public <T> void applyParser(Class<T> clazz, Function<CommandParseContext<I>, T> parsingFunction) {
    parserMap.put(clazz, parsingFunction);
  }

  /** {@inheritDoc} */
  @Override
  public <T> Optional<T> parse(Class<T> clazz, CommandParseContext<I> context) {
    return getParser(clazz).flatMap(parser -> Optional.ofNullable(parser.apply(context)));
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  @Override
  public <T> Optional<Function<CommandParseContext<I>, T>> getParser(Class<T> clazz) {
    Function<CommandParseContext<I>, ?> wildcardFunction = parserMap.get(clazz);
    if (wildcardFunction == null) {
      return Optional.empty();
    }
    return Optional.of((Function<CommandParseContext<I>, T>) wildcardFunction);
  }

  public Pair<List<Optional<?>>, List<String>> parseParameters(
      List<CommandParameter<?>> parameters, CommandParseContext<I> context) {
    List<Optional<?>> paramOutput = new ArrayList<>();
    int index = 0;
    for (var parameter : parameters) {
      paramOutput.add(parseParameter(index, parameter, context));
      index++;
    }
    List<String> extra = new ArrayList<>();
    context.retrieveRest().forEachRemaining(extra::add);
    return PairTuple.of(paramOutput, extra);
  }

  public <T> Optional<T> parseParameter(
      int index, CommandParameter<T> parameter, CommandParseContext<I> context) {
    if (!context.hasMore()) {
      return retrieveDefault(index, parameter, context.getAuthor());
    }
    Optional<T> result = parse(parameter.getType(), context);
    if (!result.isPresent()) {
      result = retrieveDefault(index, parameter, context.getAuthor());
    }
    return result;
  }

  public <T> Optional<T> retrieveDefault(int index, CommandParameter<T> parameter, I author) {
    Optional<T> result =
        parameter
            .getDefaultValue()
            .flatMap(def -> parse(parameter.getType(), new CommandParseContext<>(author, def)));
    if (!result.isPresent() && !parameter.getOptional()) {
      throw new IllegalArgumentException(
          String.format(
              "Failed to parse parameter{`%s`} of type{`%s`}",
              index, parameter.getType().getName()));
    }
    return result;
  }
}
