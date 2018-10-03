package com.github.coreyshupe.commandlib.command;

import org.testng.annotations.Ignore;

@Ignore
public class ConstructiveHelloCommand extends ConstructiveCommand<String> {
  private final TestCommandFunctionality functionality;

  public ConstructiveHelloCommand(TestCommandFunctionality functionality) {
    super("hello");
    this.functionality = functionality;
  }

  @Override
  public void init() {
    var param =
        CommandParameter.ofBuilder(String.class).resetIfAbsent(false).optional(true).build();
    addParams(param, param);
  }

  @Override
  public void accept(CommandEvent<String> event) {
    event
        .expectOptional(String.class, 0)
        .ifPresentOrElse(
            str ->
                event
                    .expectOptional(String.class, 1)
                    .ifPresentOrElse(
                        str2 ->
                            take(
                                TestCommandFunctionality.HELLO_COMMAND_STATE_3,
                                str,
                                event.getAuthor(),
                                str2),
                        () ->
                            take(
                                TestCommandFunctionality.HELLO_COMMAND_STATE_2,
                                str,
                                event.getAuthor())),
            () -> take(TestCommandFunctionality.HELLO_COMMAND_STATE_1, event.getAuthor()));
  }

  private void take(String format, Object... args) {
    functionality.take(String.format(format, args));
  }
}
