package com.github.coreyshupe.commandlib.command;

import org.testng.annotations.Ignore;

@Ignore
public class ConstructedEchoCommand extends ConstructedCommand<String> {
  private final TestCommandFunctionality functionality;

  public ConstructedEchoCommand(TestCommandFunctionality functionality) {
    super(ImmutableCommandInformation.<String>builder().name("echo").addAliases("respond").build());
    this.functionality = functionality;
  }

  @Override
  public void accept(CommandEvent<String> event) {
    functionality.take(String.join(" ", event.getExtra()));
  }
}
