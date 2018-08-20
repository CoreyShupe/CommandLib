package com.github.coreyshupe.commandlib.command;

import org.testng.annotations.Ignore;

@Ignore
public class RunCommand extends Command<String> {
  private final TestCommandFunctionality functionality;

  public RunCommand(TestCommandFunctionality functionality) {
    super(
        ImmutableCommandInformation.<String>builder()
            .name("run")
            .addParameters(
                ImmutableCommandParameter.<UtilityPairedFoundation>builder()
                    .type(UtilityPairedFoundation.class)
                    .optional(false)
                    .resetIfAbsent(true)
                    .defaultValue("default result")
                    .build())
            .build());
    this.functionality = functionality;
  }

  @Override
  public void accept(CommandEvent<String> event) {
    UtilityPairedFoundation foundation = event.expectNonOptional(UtilityPairedFoundation.class, 0);
    functionality.take(
        String.format(
            "Found:%s Extra:%s", foundation.toString(), String.join(" ", event.getExtra())));
  }
}
