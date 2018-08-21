package com.github.coreyshupe.commandlib.command;

import org.testng.annotations.Ignore;

@Ignore
public class ConstructiveEchoCommand extends ConstructiveCommand<String> {
  private final TestCommandFunctionality functionality;

  public ConstructiveEchoCommand(TestCommandFunctionality functionality) {
    super("echo");
    this.functionality = functionality;
  }

  @Override
  public void init() {
    addAliases("respond");
  }

  @Override
  public void accept(CommandEvent<String> event) {
    functionality.take(String.join(" ", event.getExtra()));
  }
}
