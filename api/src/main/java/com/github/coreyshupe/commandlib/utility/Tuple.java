package com.github.coreyshupe.commandlib.utility;

import org.immutables.value.Value;

/** @author CoreyShupe, created on 2018/08/05 */
@Value.Style(
  allParameters = true,
  typeImmutable = "*Tuple",
  defaults = @Value.Immutable(builder = false)
)
public @interface Tuple {}
