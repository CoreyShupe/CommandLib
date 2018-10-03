package com.github.coreyshupe.commandlib;

import com.github.coreyshupe.commandlib.annotation.AnnotationParser;
import com.github.coreyshupe.commandlib.annotation.CommandBody;
import com.github.coreyshupe.commandlib.command.Command;
import com.github.coreyshupe.commandlib.command.CommandEvent;
import com.github.coreyshupe.commandlib.command.CommandInformation;
import com.github.coreyshupe.commandlib.command.ImmutableCommandEvent;
import com.github.coreyshupe.commandlib.parse.CommandParameterParser;
import com.github.coreyshupe.commandlib.parse.CommandParseContext;
import com.github.coreyshupe.commandlib.utility.Pair;
import com.github.coreyshupe.commandlib.utility.PairTuple;
import com.google.common.base.Preconditions;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Base handler for commands, caching and distributing.
 *
 * @param <I> Author typing.
 * @author CoreyShupe, created on 2018/08/06
 */
public class CommandHandler<I> {
  private final Set<Pair<CommandInformation<I>, Consumer<CommandEvent<I>>>> commandSet;
  private final CommandParameterParser<I> parser;
  private ContentParser contentParser;
  private BiConsumer<I, String> invalidCommandConsumer;

  private CommandHandler() {
    this.commandSet = new HashSet<>();
    this.parser = new CommandParameterParser<>();
  }

  private CommandHandler(String prefix) {
    this(ContentParser.defaultPrefixDistributor(prefix));
  }

  private CommandHandler(ContentParser parser) {
    this();
    this.contentParser = parser;
  }

  public void setContentParser(ContentParser contentParser) {
    this.contentParser = contentParser;
  }

  public void setInvalidCommandConsumer(BiConsumer<I, String> invalidCommandConsumer) {
    this.invalidCommandConsumer = invalidCommandConsumer;
  }

  public CommandParameterParser<I> getParser() {
    return parser;
  }

  public Set<CommandInformation<I>> getCommandsInformation() {
    return commandSet.stream().map(Pair::getKey).collect(Collectors.toSet());
  }

  public Optional<Pair<CommandInformation<I>, Consumer<CommandEvent<I>>>> getCommand(
      String commandName) {
    return commandSet.stream().filter(x -> x.getKey().checkAgainstCommand(commandName)).findFirst();
  }

  public void registerCommand(Command<I> command) {
    registerCommand(command.getInformation(), command);
  }

  public void registerCommand(CommandInformation<I> information, Consumer<CommandEvent<I>> event) {
    commandSet.add(PairTuple.of(information, event));
  }

  public void registerCommands(Object instance) {
    Method[] methods = instance.getClass().getMethods();
    for (Method method : methods) {
      CommandBody body;
      if ((body = method.getAnnotation(CommandBody.class)) != null) {
        AnnotationParser<I> parser = new AnnotationParser<>(instance, method);
        registerCommand(parser.generateCommand(body));
      }
    }
  }

  public void acceptContent(I author, String content) {
    Preconditions.checkState(
        contentParser != null, "A content parser must be implemented to use this method.");
    if (contentParser.getContentChecker().test(content)) {
      var commandInfo = contentParser.getContentParser().apply(content);
      runCommand(author, commandInfo.getKey(), commandInfo.getValue());
    }
  }

  public void runCommand(I author, String command, String content) {
    getCommand(command)
        .ifPresentOrElse(
            (paired) ->
                paired
                    .getKey()
                    .getPermissionPredicate()
                    .ifPresentOrElse(
                        predicate -> {
                          if (predicate.test(author)) {
                            execute(author, paired, content);
                          } else {
                            paired
                                .getKey()
                                .getNoPermissionConsumer()
                                .ifPresentOrElse(
                                    consumer -> consumer.accept(author),
                                    () -> {
                                      throw new IllegalStateException(
                                          "Permission dropped for "
                                              + author.toString()
                                              + " during command "
                                              + command
                                              + ", with no valid consumer.");
                                    });
                          }
                        },
                        () -> execute(author, paired, content)),
            () -> {
              if (invalidCommandConsumer == null) {
                throw new IllegalStateException("Failed to understand command " + command + ".");
              }
              invalidCommandConsumer.accept(author, command);
            });
  }

  private void execute(
      I author, Pair<CommandInformation<I>, Consumer<CommandEvent<I>>> paired, String content) {
    var result =
        parser.parseParameters(
            paired.getKey().getParameters(), new CommandParseContext<>(author, content));
    var event =
        ImmutableCommandEvent.<I>builder()
            .author(author)
            .parameterObjects(result.getKey())
            .extra(result.getValue())
            .build();
    paired.getValue().accept(event);
  }

  public static <T> CommandHandler<T> of() {
    return new CommandHandler<>();
  }

  public static <T> CommandHandler<T> of(String prefix) {
    Preconditions.checkArgument(
        prefix != null && !prefix.isEmpty(), "The prefix must not be null or empty.");
    return new CommandHandler<>(prefix);
  }

  public static <T> CommandHandler<T> of(ContentParser parser) {
    Preconditions.checkArgument(parser != null, "The parser must not be null.");
    return new CommandHandler<>(parser);
  }
}
