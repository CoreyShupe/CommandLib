package com.github.coreyshupe.commandlib.command;

import org.testng.annotations.Ignore;

@Ignore
public class HelloCommand extends Command<String> {
  private final TestCommandFunctionality functionality;

  public HelloCommand(TestCommandFunctionality functionality) {
    super(
        ImmutableCommandInformation.<String>builder()
            .name("hello")
            .addParameters(
                ImmutableCommandParameter.<String>builder()
                    .type(String.class)
                    .optional(true)
                    .resetIfAbsent(false)
                    .build(),
                ImmutableCommandParameter.<String>builder()
                    .type(String.class)
                    .optional(true)
                    .resetIfAbsent(false)
                    .build())
            .build());
    this.functionality = functionality;
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
