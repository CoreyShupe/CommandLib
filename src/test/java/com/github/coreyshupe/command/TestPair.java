package com.github.coreyshupe.command;

import org.assertj.core.api.Assertions;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/** @author CoreyShupe, created on 2018/08/04 */
public class TestPair {
  private Pair<Integer, Integer> leftSkewedPair;
  private Pair<Integer, Integer> rightSkewedPair;

  @BeforeMethod
  public void setupPairs() {
    leftSkewedPair = Pair.of(5, 0);
    rightSkewedPair = Pair.of(0, 5);
  }

  @Test
  public void getKey_whenParamInsertedInLeftPosition_shouldBeKey() {
    Assertions.assertThat(leftSkewedPair.getKey()).isEqualTo(5);
  }

  @Test
  public void getValue_whenParamInsertedInRightPosition_shouldBeValue() {
    Assertions.assertThat(rightSkewedPair.getValue()).isEqualTo(5);
  }

  @Test
  public void equals_whenPairsHaveSameContent_shouldBeEqual() {
    Assertions.assertThat(leftSkewedPair.equals(Pair.of(5, 0))).isTrue();
  }

  @Test
  public void equals_whenPairsHaveDifferentContent_shouldNotBeEqual() {
    Assertions.assertThat(leftSkewedPair.equals(rightSkewedPair)).isFalse();
  }

  @Test
  public void hashcode_whenPairsHaveSameContent_shouldHaveSameHashcode() {
    Assertions.assertThat(leftSkewedPair.hashCode()).isEqualTo(Pair.of(5, 0).hashCode());
  }

  @Test
  public void hashcode_whenPairsHaveDifferentContent_shouldHaveDifferentHashcode() {
    Assertions.assertThat(leftSkewedPair.hashCode()).isNotEqualTo(rightSkewedPair.hashCode());
  }
}
