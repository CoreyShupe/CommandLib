package com.github.coreyshupe.commandlib.command;

import org.testng.annotations.Ignore;

@Ignore
public class UtilityPairedFoundation {
  private final String first;
  private final String second;

  public UtilityPairedFoundation(String first, String second) {
    this.first = first;
    this.second = second;
  }

  @Override
  public String toString() {
    return String.format("{%s %s}", first, second);
  }
}
