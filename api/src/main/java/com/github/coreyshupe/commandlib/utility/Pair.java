package com.github.coreyshupe.commandlib.utility;

import org.immutables.value.Value;

/**
 * Simple Key/Value container class.
 *
 * @author CoreyShupe, created on 2018/08/05
 */
@Tuple
@Value.Immutable
public interface Pair<I, O> {
  /** @return The contained key. */
  I getKey();

  /** @return The contained value. */
  O getValue();
}
