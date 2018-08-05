package com.github.coreyshupe.command.parse;

import com.github.coreyshupe.command.TestableObject;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

/** @author CoreyShupe, created on 2018/08/04 */
public class TestClassParser {
  private ClassParser<Integer> classParser;

  @BeforeMethod
  public void setupParser() {
    classParser = new ClassParser<>();
    classParser.applyParser(
        TestableObject.class, TestableObject.generateParseFunction(classParser));
  }

  @Test
  public void parse_whenHasAllPieces_shouldParseItem() {
    Assertions.assertThat(parseWith("10", "20")).isPresent().contains(new TestableObject(10, 20));
  }

  @Test
  public void parse_whenMalformed_shouldReturnEmpty() {
    Assertions.assertThat(parseWith("10", "20a")).isEmpty();
  }

  @Test
  public void parse_whenLackingPieces_shouldReturnEmpty() {
    Assertions.assertThat(parseWith("10")).isEmpty();
  }

  @Test
  public void parse_whenEmpty_shouldReturnEmpty() {
    Assertions.assertThat(parseWith("")).isEmpty();
  }

  @Ignore
  private Optional<TestableObject> parseWith(String... parts) {
    return classParser.parse(TestableObject.class, setupContext(parts));
  }

  @Ignore
  private ClassParseContext<Integer> setupContext(String... parts) {
    return new ClassParseContext<>(0, new ArrayDeque<>(Arrays.asList(parts)));
  }
}
