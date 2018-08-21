package com.github.coreyshupe.commandlib.command;

import java.util.function.Consumer;

public interface Command<I> extends Consumer<CommandEvent<I>> {
  CommandInformation<I> getInformation();
}
