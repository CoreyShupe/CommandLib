package com.github.coreyshupe.command;

import java.util.Objects;

/**
 * A key-value pair.
 *
 * @param <I> The key type.
 * @param <O> The value type.
 * @author CoreyShupe, created on 2018/08/01
 */
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Pair<?, ?> pair = (Pair<?, ?>) o;
    return Objects.equals(key, pair.key) && Objects.equals(value, pair.value);
  }

  @Override
  public int hashCode() {
    return (key.hashCode() << value.hashCode()) ^ Objects.hash(key, value);
  }

  public static <K, V> Pair<K, V> of(K key, V value) {
    return new Pair<>(key, value);
  }
}
