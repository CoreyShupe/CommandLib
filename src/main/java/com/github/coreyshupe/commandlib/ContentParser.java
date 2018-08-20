package com.github.coreyshupe.commandlib;

import com.github.coreyshupe.commandlib.utility.Pair;
import com.github.coreyshupe.commandlib.utility.PairTuple;
import java.util.function.Function;
import java.util.function.Predicate;
import org.immutables.value.Value;

/**
 * An {@code interface} which takes in content and parses it into a command and distributes it to
 * the command handler.
 *
 * @author CoreyShupe, created on 2018/08/06
 */
@Value.Immutable
public interface ContentParser {
  Predicate<String> getContentChecker();

  Function<String, Pair<String, String>> getContentParser();

  static ContentParser defaultPrefixDistributor(String prefix) {
    return ImmutableContentParser.builder()
        .contentChecker(s -> s.startsWith(prefix) && s.length() > prefix.length())
        .contentParser(
            s -> {
              String command = s.substring(prefix.length()).split(" ")[0];
              return PairTuple.of(command, s.substring(command.length()).trim());
            })
        .build();
  }
}
