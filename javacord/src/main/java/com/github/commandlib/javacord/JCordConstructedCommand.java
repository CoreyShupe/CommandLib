package com.github.commandlib.javacord;

import com.github.coreyshupe.commandlib.command.CommandInformation;
import com.github.coreyshupe.commandlib.command.ConstructedCommand;
import org.javacord.api.event.message.MessageCreateEvent;

public abstract class JCordConstructedCommand extends ConstructedCommand<MessageCreateEvent> {
  public JCordConstructedCommand(CommandInformation<MessageCreateEvent> information) {
    super(information);
  }
}
