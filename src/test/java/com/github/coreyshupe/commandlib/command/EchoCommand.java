package com.github.coreyshupe.commandlib.command;

import org.testng.annotations.Ignore;

@Ignore
public class EchoCommand extends Command<String> {
  private final TestCommandFunctionality functionality;

  public EchoCommand(TestCommandFunctionality functionality) {
    super(ImmutableCommandInformation.<String>builder().name("echo").build());
    this.functionality = functionality;
  }

  @Override
  public void accept(CommandEvent<String> event) {
    functionality.take(String.join(" ", event.getExtra()));
  }
}
