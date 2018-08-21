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
                            functionality.take(
                                String.format("hello %s %s %s", str, event.getAuthor(), str2)),
                        () ->
                            functionality.take(
                                String.format("hello %s %s", str, event.getAuthor()))),
            () -> functionality.take("hello " + event.getAuthor()));
  }
}
