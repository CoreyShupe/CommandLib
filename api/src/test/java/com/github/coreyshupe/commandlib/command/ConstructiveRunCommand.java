package com.github.coreyshupe.commandlib.command;

import org.testng.annotations.Ignore;

@Ignore
public class ConstructiveRunCommand extends ConstructiveCommand<String> {
  private final TestCommandFunctionality functionality;

  public ConstructiveRunCommand(TestCommandFunctionality functionality) {
    super("run");
    this.functionality = functionality;
  }

  @Override
  public void init() {
    addParams(
        CommandParameter.ofBuilder(UtilityPairedFoundation.class)
            .optional(false)
            .resetIfAbsent(true)
            .defaultValue("default result"));
    setNoPermissionConsumer(author -> functionality.take("Cannot run command as non-admin."));
    setNoPermissionPredicate(author -> author.equalsIgnoreCase("admin"));
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
