package com.github.coreyshupe.commandlib.command;

import java.util.function.Consumer;

/**
 * The {@link Consumer} which consumes {@link CommandEvent}s with the given {@link
 * CommandInformation}.
 *
 * @author CoreyShupe, created on 2018/08/05
 * @param <I> The author typing of the command.
 * @see Consumer
 */
public abstract class Command<I> implements Consumer<CommandEvent<I>> {

  private final CommandInformation<I> information;

  public Command(CommandInformation<I> information) {
    this.information = information;
  }

  /** @return The {@link CommandInformation} linked to the command. */
  public CommandInformation<I> getInformation() {
    return information;
  }
}
