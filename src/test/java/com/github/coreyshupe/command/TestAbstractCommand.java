package com.github.coreyshupe.command;

import org.assertj.core.api.Assertions;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/** @author CoreyShupe, created on 2018/08/04 */
public class TestAbstractCommand {
  private AbstractCommand<Integer> command;

  @BeforeMethod
  public void setupCommand() {
    command =
        new AbstractCommand<>("hello", new String[] {"world"}, "N/A") {
          @Override
          public void accept(Integer author, String string) {}
        };
  }

  @Test
  public void matchesCommand_whenGivenNameMatchingString_shouldMatch() {
    Assertions.assertThat(command.matchesCommand("hello")).isTrue();
  }

  @Test
  public void matchesCommand_whenGivenAliasMatchingString_shouldMatch() {
    Assertions.assertThat(command.matchesCommand("world")).isTrue();
  }

  @Test
  public void matchesCommand_whenGivenNonMatchingString_shouldNotMatch() {
    Assertions.assertThat(command.matchesCommand("null")).isFalse();
  }
}
