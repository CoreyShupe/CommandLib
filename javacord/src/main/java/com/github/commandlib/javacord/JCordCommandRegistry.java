package com.github.commandlib.javacord;

import com.github.coreyshupe.commandlib.CommandHandler;
import com.github.coreyshupe.commandlib.ContentParser;
import com.github.coreyshupe.commandlib.command.Command;
import com.github.coreyshupe.commandlib.parse.ClassParser;
import com.github.coreyshupe.commandlib.parse.CommandParseContext;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ChannelCategory;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.emoji.KnownCustomEmoji;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

public class JCordCommandRegistry {

  // All DiscordEntityParser(s) parse the actual snowflake along with the mention / used emoji.
  // e.g.
  // User will be parsed using `SNOWFLAKE`(raw) or `<@SNOWFLAKE>`(mention) or
  // `<@!SNOWFLAKE>`(username w mention)
  private static final String SNOWFLAKE_PATTERN = "[0-9]+>";
  private static final DiscordEntityParser<User> USER_PARSER =
      DiscordEntityParser.of(
          s -> s.substring(s.startsWith("<!") ? 3 : 2, s.length() - 1),
          (server, s) -> server.getMemberById(s),
          Pattern.compile("<@(!?)" + SNOWFLAKE_PATTERN));
  private static final DiscordEntityParser<ServerChannel> CHANNEL_PARSER =
      DiscordEntityParser.of(
          s -> s.substring(2, s.length() - 1),
          (server, s) -> server.getChannelById(s),
          Pattern.compile("<#" + SNOWFLAKE_PATTERN));
  private static final DiscordEntityParser<ChannelCategory> CHANNEL_CATEGORY_PARSER =
      DiscordEntityParser.of(
          s -> s.substring(2, s.length() - 1),
          (server, s) -> server.getChannelCategoryById(s),
          Pattern.compile("<#" + SNOWFLAKE_PATTERN));
  private static final DiscordEntityParser<Role> ROLE_PARSER =
      DiscordEntityParser.of(
          s -> s.substring(3, s.length() - 1),
          (server, s) -> server.getRoleById(s),
          Pattern.compile("<@&" + SNOWFLAKE_PATTERN));
  private static final DiscordEntityParser<KnownCustomEmoji> EMOJI_PARSER =
      DiscordEntityParser.of(
          s -> s.substring(0, s.length() - 1).split(":")[2],
          (server, s) -> server.getCustomEmojiById(s),
          Pattern.compile("<(a?):[A-Za-z]+:" + SNOWFLAKE_PATTERN));

  private final Set<ParserCache<?>> parsers = new HashSet<>();
  private final Set<Command<MessageCreateEvent>> commandSet = new HashSet<>();

  public JCordCommandRegistry registerCommand(Command<MessageCreateEvent> command) {
    commandSet.add(command);
    return this;
  }

  public <T> JCordCommandRegistry registerParser(
      Class<T> clazz, Function<CommandParseContext<MessageCreateEvent>, T> function) {
    parsers.add(new ParserCache<>(clazz, function));
    return this;
  }

  public void registerWith(DiscordApi api, ContentParser parser) {
    registerWith(api, CommandHandler.of(parser));
  }

  public void registerWith(DiscordApi api, String prefix) {
    registerWith(api, CommandHandler.of(prefix));
  }

  public void registerWith(DiscordApi api, CommandHandler<MessageCreateEvent> commandHandler) {
    commandSet.forEach(commandHandler::registerCommand);
    ClassParser<CommandParseContext<MessageCreateEvent>> parser = commandHandler.getParser();
    parsers.forEach(p -> p.applyToParser(parser));
    parser.applyParser(User.class, USER_PARSER);
    api.addMessageCreateListener(
        event -> commandHandler.acceptContent(event, event.getMessage().getContent()));
  }

  private static final class ParserCache<T> {
    private final Class<T> clazz;
    private final Function<CommandParseContext<MessageCreateEvent>, T> function;

    private ParserCache(
        Class<T> clazz, Function<CommandParseContext<MessageCreateEvent>, T> function) {
      this.clazz = clazz;
      this.function = function;
    }

    private void applyToParser(ClassParser<CommandParseContext<MessageCreateEvent>> parser) {
      parser.applyParser(clazz, function);
    }
  }

  private static final class DiscordEntityParser<T>
      implements Function<CommandParseContext<MessageCreateEvent>, T> {

    private final Function<String, String> regexParser;
    private final BiFunction<Server, String, Optional<T>> stringParser;
    private final Pattern pattern;

    private DiscordEntityParser(
        Function<String, String> regexParser,
        BiFunction<Server, String, Optional<T>> stringParser,
        Pattern pattern) {
      this.regexParser = regexParser;
      this.stringParser = stringParser;
      this.pattern = pattern;
    }

    @Override
    public T apply(CommandParseContext<MessageCreateEvent> context) {
      String next = context.get();
      return context
          .getAuthor()
          .getServer()
          .flatMap(
              server -> {
                if (next.matches("[0-9]+")) {
                  return stringParser.apply(server, next);
                } else if (pattern.matcher(next).matches()) {
                  return stringParser.apply(server, regexParser.apply(next));
                }
                return Optional.empty();
              })
          .orElse(null);
    }

    private static <T> DiscordEntityParser<T> of(
        Function<String, String> regexParser,
        BiFunction<Server, String, Optional<T>> stringParser,
        Pattern pattern) {
      return new DiscordEntityParser<>(regexParser, stringParser, pattern);
    }
  }
}
