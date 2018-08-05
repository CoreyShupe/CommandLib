package com.github.coreyshupe.command;

import com.github.coreyshupe.command.exceptions.InvalidParameterException;
import com.github.coreyshupe.command.exceptions.ParameterNotFoundException;
import com.github.coreyshupe.command.parse.ClassParseContext;
import com.github.coreyshupe.command.parse.ClassParser;
import java.util.ArrayDeque;
import java.util.Arrays;
import org.assertj.core.api.Assertions;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

/** @author CoreyShupe, created on 2018/08/04 */
public class TestCommandParameter {

  private ClassParser<Integer> classParser;

  @BeforeMethod
  @SuppressWarnings("unchecked")
  public void setupParser() {
    classParser = new ClassParser<>();
    classParser.applyParser(
        TestableObject.class, TestableObject.generateParseFunction(classParser));
  }

  @Test
  public void parse_whenOptional_shouldProduceNullAndNoException() {
    var parameter = newCommandParam(false, true);
    var result = parameter.parse(setupContext(), classParser);
    Assertions.assertThat(result.getKey()).isNull();
  }

  @Test
  public void parse_whenNonOptional_shouldProduceException() {
    var parameter = newCommandParam(false, false);
    Assertions.assertThatThrownBy(() -> parameter.parse(setupContext(), classParser))
        .isInstanceOf(ParameterNotFoundException.class)
        .hasMessageContaining("Parameter in position `1` not found.");
  }

  @Test
  public void parse_whenResetIfAbsent_shouldResetContext() {
    var parameter = newCommandParam(true, true);
    var pair = parameter.parse(setupContext("a", "b", "c"), classParser);
    Assertions.assertThat(pair.getKey()).isNull();
    Assertions.assertThat(pair.getValue().size()).isEqualTo(3);
  }

  @Test
  public void parse_whenNotResetIfAbsent_shouldKnockPieces() {
    var parameter = newCommandParam(false, true);
    var pair = parameter.parse(setupContext("a", "b", "c"), classParser);
    Assertions.assertThat(pair.getKey()).isNull();
    Assertions.assertThat(pair.getValue().size()).isNotEqualTo(3);
  }

  @Test
  public void parse_whenValidDefaultValue_shouldParseDefault() {
    var parameter = newCommandParam(false, false, "10", "20");
    var item = parameter.parse(setupContext(), classParser).getKey();
    Assertions.assertThat(item).isNotNull().isEqualTo(new TestableObject(10, 20));
  }

  @Test
  public void parse_whenInvalidDefaultValue_shouldThrowError() {
    var parameter = newCommandParam(false, false, "10", "20a");
    Assertions.assertThatThrownBy(() -> parameter.parse(setupContext(), classParser))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("failed to produce a valid object");
  }

  @Test
  public void parse_whenGivenGoodValues_shouldParseCorrectly() {
    var parameter = newCommandParam(false, false);
    var item = parameter.parse(setupContext("10", "20"), classParser).getKey();
    Assertions.assertThat(item).isEqualTo(new TestableObject(10, 20));
  }

  @Test
  public void parse_whenGivenBadValues_shouldThrowError() {
    var parameter = newCommandParam(false, false);
    Assertions.assertThatThrownBy(() -> parameter.parse(setupContext("x", "y"), classParser))
        .isInstanceOf(InvalidParameterException.class)
        .hasMessageContaining("Failed to parse")
        .hasMessageContaining("` with `");
  }

  @Test
  public void parse_whenNoValidParser_shouldThrowError() {
    var parameter = new CommandParameter<>(Pair.class, false, false, 0);
    Assertions.assertThatThrownBy(() -> parameter.parse(setupContext(), classParser))
        .isInstanceOf(InvalidParameterException.class)
        .hasMessageContaining("no valid parser");
  }

  @Ignore
  private CommandParameter<TestableObject> newCommandParam(
      boolean resetIfAbsent, boolean optional, String... defaultValue) {
    return new CommandParameter<>(TestableObject.class, resetIfAbsent, optional, 0, defaultValue);
  }

  @Ignore
  private ClassParseContext<Integer> setupContext(String... parts) {
    return new ClassParseContext<>(0, new ArrayDeque<>(Arrays.asList(parts)));
  }
}
