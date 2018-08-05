package com.github.coreyshupe.command;

import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;

/** @author CoreyShupe, created on 2018/08/04 */
public class TestSanity {
  @Test
  public void testSanity() {
    Assertions.assertThat(true).isTrue();
  }
}
