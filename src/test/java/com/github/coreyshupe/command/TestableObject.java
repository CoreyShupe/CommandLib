package com.github.coreyshupe.command;

import com.github.coreyshupe.command.parse.ClassParseFunction;
import com.github.coreyshupe.command.parse.ClassParser;
import java.util.Objects;
import org.testng.annotations.Ignore;

@Ignore
public class TestableObject {
  private final int first;
  private final int second;

  public TestableObject(int first, int second) {
    this.first = first;
    this.second = second;
  }

  public int getFirst() {
    return first;
  }

  public int getSecond() {
    return second;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TestableObject that = (TestableObject) o;
    return first == that.first && second == that.second;
  }

  @Override
  public int hashCode() {
    return Objects.hash(first, second);
  }

  public static ClassParseFunction<Integer, TestableObject> generateParseFunction(
      ClassParser<Integer> parser) {
    return (content, context) -> {
      context.insertFirst(content);
      return parser
          .parse(Integer.class, context)
          .flatMap(
              first ->
                  parser
                      .parse(Integer.class, context)
                      .map(second -> new TestableObject(first, second)));
    };
  }
}
