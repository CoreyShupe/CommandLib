package com.github.coreyshupe.commandlib;

import com.github.coreyshupe.commandlib.command.Command;
import com.github.coreyshupe.commandlib.command.CommandEvent;
import com.github.coreyshupe.commandlib.command.CommandInformation;
import com.github.coreyshupe.commandlib.command.ImmutableCommandEvent;
import com.github.coreyshupe.commandlib.parse.CommandParameterParser;
import com.github.coreyshupe.commandlib.parse.CommandParseContext;
import com.github.coreyshupe.commandlib.utility.Pair;
import com.github.coreyshupe.commandlib.utility.PairTuple;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
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
  private Consumer<I> invalidCommandConsumer;

  public CommandHandler() {
    this.commandSet = new HashSet<>();
    this.parser = new CommandParameterParser<>();
  }

  public void setInvalidCommandConsumer(Consumer<I> invalidCommandConsumer) {
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

  public void runCommand(I author, String command, String content) {
    getCommand(command)
        .ifPresentOrElse(
            (paired) -> {
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
            },
            () -> {
              if (invalidCommandConsumer == null) {
                throw new IllegalStateException("Failed to understand command " + command + ".");
              }
              invalidCommandConsumer.accept(author);
            });
  }

  public static <T> CommandHandler<T> of() {
    return new CommandHandler<>();
  }
}