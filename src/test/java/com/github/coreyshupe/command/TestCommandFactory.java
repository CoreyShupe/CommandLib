package com.github.coreyshupe.command;

import com.github.coreyshupe.command.annotations.Command;
import com.github.coreyshupe.command.annotations.Default;
import com.github.coreyshupe.command.annotations.IgnoreAuthor;
import com.github.coreyshupe.command.annotations.IgnoreExtraArgs;
import com.github.coreyshupe.command.annotations.Optional;
import com.github.coreyshupe.command.annotations.ResetIfAbsent;
import com.github.coreyshupe.command.exceptions.CommandNotFoundException;
import java.lang.reflect.Method;
import org.assertj.core.api.Assertions;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

/** @author CoreyShupe, created on 2018/08/04 */
public class TestCommandFactory {
  private CommandFactory<Integer> factory;

  @BeforeMethod
  public void setupFactory() {
    factory = CommandFactory.of();
  }

  @Test
  public void publishRawContent_whenNothingSetup_shouldThrowError() {
    Assertions.assertThatThrownBy(() -> factory.publishRawContent(0, "!process"))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage(
            "Valid command checker not currently implemented. See CommandFactory#setValidCommandChecker.");
  }

  @Test
  public void publishRawContent_whenCheckerSetup_shouldThrowError() {
    factory.setValidCommandChecker(CommandFactory.generateDefaultValidCommandChecker("!"));
    Assertions.assertThatThrownBy(() -> factory.publishRawContent(0, "!process"))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Content parser not currently setup. See CommandFactory#setContentParser.");
  }

  @Test
  public void publishRawContent_whenCheckerAndContentParserSetup_shouldReachCommandChecking() {
    factory.setupDefaultParsingInstructions("!");
    Assertions.assertThatThrownBy(() -> factory.publishRawContent(0, "!process"))
        .isInstanceOf(CommandNotFoundException.class)
        .hasMessage("Command `process` is not currently registered.");
  }

  @Test
  public void registerCommands_whenRegistering_shouldRegisterAllCommand() {
    factory.registerCommands(TestCommandFactory.class);
    Assertions.assertThat(factory.getCommands())
        .hasSize(2)
        .contains(generateFakeCommand("hello"), generateFakeCommand("world", "wrld", "wold"));
  }

  @Test
  public void generateParameters_whenGenerating_shouldOnlyHaveCommandParams() {
    Method method = null;
    for (var tMethod : TestCommandFactory.class.getMethods()) {
      if (tMethod.getName().equalsIgnoreCase("exampleHelloCommand")) {
        method = tMethod;
        break;
      }
    }
    if (method == null) {
      Assertions.fail("Failed to get method exampleHelloCommand(...)");
    }
    var params = factory.generateParameters(method, false, false);
    Assertions.assertThat(params)
        .hasSize(1)
        .containsExactly(new CommandParameter<>(Integer.class, false, true, 1));
  }

  @Ignore
  private AbstractCommand<Integer> generateFakeCommand(String name, String... aliases) {
    return new AbstractCommand<>(name, aliases, "No valid description present.") {
      @Override
      public void accept(Integer integer, String s) {}
    };
  }

  @Ignore
  @Command("hello")
  public void exampleHelloCommand(Integer author, @Optional Integer type, String... rest) {}

  @Ignore
  @Command(
    value = "world",
    aliases = {"wrld", "wold"}
  )
  @IgnoreAuthor
  @IgnoreExtraArgs
  public void exampleWorldCommand(@Default("17 12") @ResetIfAbsent TestableObject world) {}
}
