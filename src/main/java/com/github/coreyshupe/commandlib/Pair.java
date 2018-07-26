package com.github.coreyshupe.commandlib;

public class Pair<I, O> {

  private final I key;
  private final O value;

  private Pair(I key, O value) {
    this.key = key;
    this.value = value;
  }

  public I getKey() {
    return key;
  }

  public O getValue() {
    return value;
  }

  public static <K, V> Pair<K, V> of(K key, V value) {
    return new Pair<>(key, value);
  }
}
