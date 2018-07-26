package com.github.coreyshupe.commandlib.parse;

import java.util.Optional;
import java.util.function.BiFunction;

@FunctionalInterface
public interface ClassParseFunction<I, O>
    extends BiFunction<String, ClassParseContext<I>, Optional<O>> {}
