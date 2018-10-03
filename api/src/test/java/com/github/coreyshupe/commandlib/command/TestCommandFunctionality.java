package com.github.coreyshupe.commandlib.command;

import com.github.coreyshupe.commandlib.CommandHandler;
import com.github.coreyshupe.commandlib.utility.Pair;
import com.github.coreyshupe.commandlib.utility.PairTuple;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Queue;
import org.assertj.core.api.Assertions;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

public class TestCommandFunctionality {

  public static final String HELLO_COMMAND_STATE_1 = "Hello %s"; // 1 miss
  public static final String HELLO_COMMAND_STATE_2 = HELLO_COMMAND_STATE_1 + " %s"; // 2 miss
  public static final String HELLO_COMMAND_STATE_3 = HELLO_COMMAND_STATE_2 + " %s"; // all present
  public static final String RUN_COMMAND_STATE = "Found:%s Extra:%s";
  private CommandHandler<String> commandHandler;
  private Queue<Pair<String, String>> commandInfo;
  private Queue<String> commandResponses;

  @BeforeMethod
  public void setup() {
    commandHandler = CommandHandler.of("!");
    commandHandler
        .getParser()
        .applyParser(
            UtilityPairedFoundation.class,
            (context) ->
                context
                    .pollNext()
                    .map(
                        first ->
                            context
                                .pollNext()
                                .map(second -> new UtilityPairedFoundation(first, second))
                                .orElse(null))
                    .orElse(null));
    commandInfo = new ArrayDeque<>();
    commandResponses = new ArrayDeque<>();
    try (BufferedReader reader =
        new BufferedReader(
            new InputStreamReader(getClass().getResourceAsStream("/command_expectations")))) {
      String line;
      while ((line = reader.readLine()) != null && !line.isEmpty()) {
        String[] split = line.split("\\|");
        commandInfo.offer(PairTuple.of(split[0], split[1]));
        commandResponses.offer(split[2]);
      }
    } catch (IOException ex) {
      throw new SecurityException(ex);
    }
  }

  @Test
  public void testConstructedCommandExpectations() {
    commandHandler.registerCommand(new ConstructedEchoCommand(this));
    commandHandler.registerCommand(new ConstructedHelloCommand(this));
    commandHandler.registerCommand(new ConstructedRunCommand(this));
    testRegisteredCommands();
  }

  @Test
  public void testConstructiveCommandExpectations() {
    commandHandler.registerCommand(new ConstructiveEchoCommand(this));
    commandHandler.registerCommand(new ConstructiveHelloCommand(this));
    commandHandler.registerCommand(new ConstructiveRunCommand(this));
    testRegisteredCommands();
  }

  @Test
  public void testAnnotatedCommandExpectations() {
    commandHandler.registerCommands(new AnnotatedCommands(this));
    testRegisteredCommands();
  }

  @Ignore
  public void testRegisteredCommands() {
    while (commandInfo.size() > 0) {
      var polled = commandInfo.poll();
      commandHandler.acceptContent(polled.getKey(), polled.getValue());
    }
    if (commandResponses.size() > 0) {
      Assertions.fail("Failed to run through all responses.");
    }
  }

  @Ignore
  public void take(String str) {
    Assertions.assertThat(str).isEqualToIgnoringCase(commandResponses.poll());
  }
}
