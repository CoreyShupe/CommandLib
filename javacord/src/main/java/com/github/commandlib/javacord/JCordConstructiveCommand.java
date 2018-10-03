package com.github.commandlib.javacord;

import com.github.coreyshupe.commandlib.command.ConstructiveCommand;
import org.javacord.api.event.message.MessageCreateEvent;

public abstract class JCordConstructiveCommand extends ConstructiveCommand<MessageCreateEvent> {
  public JCordConstructiveCommand(String name) {
    super(name);
  }
}
