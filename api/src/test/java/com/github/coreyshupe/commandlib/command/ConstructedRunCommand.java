package com.github.coreyshupe.commandlib.command;

import org.testng.annotations.Ignore;

@Ignore
public class ConstructedRunCommand extends ConstructedCommand<String> {
  private final TestCommandFunctionality functionality;

  public ConstructedRunCommand(TestCommandFunctionality functionality) {
    super(
        ImmutableCommandInformation.<String>builder()
            .name("run")
            .addParameters(
                CommandParameter.ofBuilder(UtilityPairedFoundation.class)
                    .optional(false)
                    .resetIfAbsent(true)
                    .defaultValue("default result")
                    .build())
            .noPermissionConsumer(author -> functionality.take("Cannot run command as non-admin."))
            .permissionPredicate(author -> author.equalsIgnoreCase("admin"))
            .build());
    this.functionality = functionality;
  }

  @Override
  public void accept(CommandEvent<String> event) {
    UtilityPairedFoundation foundation = event.expectNonOptional(UtilityPairedFoundation.class, 0);
    functionality.take(
        String.format(
            TestCommandFunctionality.RUN_COMMAND_STATE,
            foundation.toString(),
            String.join(" ", event.getExtra())));
  }
}
