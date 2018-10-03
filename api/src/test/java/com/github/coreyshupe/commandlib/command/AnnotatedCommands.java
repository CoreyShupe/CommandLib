package com.github.coreyshupe.commandlib.command;

import com.github.coreyshupe.commandlib.annotation.CommandBody;
import com.github.coreyshupe.commandlib.annotation.CommandBody.Author;
import com.github.coreyshupe.commandlib.annotation.CommandBody.Extra;
import com.github.coreyshupe.commandlib.annotation.CommandBody.Param;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.testng.annotations.Ignore;

@Ignore
public class AnnotatedCommands {

  private final TestCommandFunctionality functionality;
  public final Predicate<String> runNoPermissionPredicate;
  public final Consumer<String> runNoPermissionConsumer;

  public AnnotatedCommands(TestCommandFunctionality functionality) {
    this.functionality = functionality;
    this.runNoPermissionPredicate = (author) -> author.equalsIgnoreCase("admin");
    this.runNoPermissionConsumer =
        (author) -> functionality.take("Cannot run command as non-admin.");
  }

  @CommandBody("hello")
  public void runHelloCommand(
      @Author String author,
      @Param(optional = true) String first,
      @Param(optional = true) String second) {
    if (first == null) {
      take(TestCommandFunctionality.HELLO_COMMAND_STATE_1, author);
    } else if (second == null) {
      take(TestCommandFunctionality.HELLO_COMMAND_STATE_2, first, author);
    } else {
      take(TestCommandFunctionality.HELLO_COMMAND_STATE_3, first, author, second);
    }
  }

  @CommandBody("run")
  public void runRunCommand(
      @Param(resetIfAbsent = true, defaultValue = "default result")
          UtilityPairedFoundation foundation,
      @Extra List<String> extra) {
    take(
        TestCommandFunctionality.RUN_COMMAND_STATE, foundation.toString(), String.join(" ", extra));
  }

  @CommandBody(value = "echo", aliases = "respond")
  public void runEchoCommand(@Extra List<String> extra) {
    take(String.join(" ", extra));
  }

  private void take(String format, Object... args) {
    functionality.take(String.format(format, args));
  }
}
