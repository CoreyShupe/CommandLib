package com.github.coreyshupe.command.parse;

import java.util.Optional;
import java.util.function.BiFunction;

/**
 * Function which takes in {@link String} content and {@link ClassParseContext} and results in an
 * {@link Optional} item.
 *
 * @param <I> The type of author.
 * @param <O> The type of returning item.
 * @author CoreyShupe, created on 2018/08/01
 */
@FunctionalInterface
public interface ClassParseFunction<I, O>
    extends BiFunction<String, ClassParseContext<I>, Optional<O>> {}
