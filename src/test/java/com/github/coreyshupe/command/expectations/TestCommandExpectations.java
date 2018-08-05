package com.github.coreyshupe.command.expectations;

import com.github.coreyshupe.command.CommandFactory;
import com.github.coreyshupe.command.TestableObject;
import com.github.coreyshupe.command.annotations.Command;
import com.github.coreyshupe.command.annotations.CommandSet;
import com.github.coreyshupe.command.annotations.Default;
import com.github.coreyshupe.command.annotations.IgnoreAuthor;
import com.github.coreyshupe.command.annotations.IgnoreExtraArgs;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import java.util.Arrays;

/** @author CoreyShupe, created on 2018/08/05 */
public class TestCommandExpectations {
    private static final CommandAnalyzer ANALYZER = new CommandAnalyzer();
    @CommandSet private InnerCommandSet commandSet;

  @Test
  public void analyzeCommandProcess() {
      var commandFactory = CommandFactory.<Integer>of();
      commandFactory
              .getClassParser()
              .applyParser(
                      TestableObject.class,
                      TestableObject.generateParseFunction(commandFactory.getClassParser()));
      commandFactory.registerCommands(TestCommandExpectations.class);
      commandFactory.setupDefaultParsingInstructions("!");
      ANALYZER.test(commandFactory);
  }

  @Ignore
  @IgnoreAuthor
  @IgnoreExtraArgs
  @Command("hello")
  public void helloWorldCommand() {
      ANALYZER.analyzeNext("world");
  }

  @Ignore
  public static class InnerCommandSet {
    @IgnoreAuthor
    @Command("process")
    public void processCommand(@Default("1 2") TestableObject from, String[] extra) {
      var result =
          String.format(
              "Retrieved %s,%s. extra:%s", from.getFirst(), from.getSecond(), extra.length == 0 ? "[]" : Arrays.asList(extra));
      ANALYZER.analyzeNext(result);
    }
  }
}
