package com.github.coreyshupe.commandlib;

import com.github.coreyshupe.commandlib.utility.Pair;
import com.github.coreyshupe.commandlib.utility.PairTuple;
import java.util.function.Function;
import java.util.function.Predicate;
import org.immutables.value.Value;

/**
 * An {@link interface} which takes in content and parses it into a command and distributes it to
 * the command handler.
 *
 * @author CoreyShupe, created on 2018/08/06
 */
@Value.Immutable
public interface ContentDistributor<I> {
  Predicate<String> getContentChecker();

  Function<String, Pair<String, String>> getContentParser();

  CommandHandler<I> getCommandHandler();

  default void handleContent(I author, String content) {
    if (getContentChecker().test(content)) {
      var information = getContentParser().apply(content);
      getCommandHandler().runCommand(author, information.getKey(), information.getValue().trim());
    }
  }

  static <I> ContentDistributor<I> defaultPrefixDistributor(
      CommandHandler<I> handler, String prefix) {
    return ImmutableContentDistributor.<I>builder()
        .contentChecker(s -> s.startsWith(prefix) && s.length() > prefix.length())
        .contentParser(
            s -> {
              String command = s.substring(prefix.length()).split(" ")[0];
              return PairTuple.of(command, s.substring(command.length()).trim());
            })
        .build();
  }
}
