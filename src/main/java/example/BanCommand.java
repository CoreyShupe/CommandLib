package example;

import com.github.coreyshupe.commandlib.CommandHandler;
import com.github.coreyshupe.commandlib.ContentDistributor;
import com.github.coreyshupe.commandlib.command.Command;
import com.github.coreyshupe.commandlib.command.CommandEvent;
import com.github.coreyshupe.commandlib.command.ImmutableCommandInformation;
import com.github.coreyshupe.commandlib.command.ImmutableCommandParameter;
import com.github.coreyshupe.commandlib.parse.DefaultParser;
import com.google.common.collect.ImmutableMap;

/** @author CoreyShupe, created on 2018/08/06 */
public class BanCommand extends Command<String> {
  public BanCommand() {
    super(
        ImmutableCommandInformation.<String>builder()
            .name("ban")
            .addAliases("banned", "bans")
            .description("Simple ban command to ban users.")
            .putAllAlternateInformation(
                ImmutableMap.<String, String>builder()
                    .put("Example", "!ban <user> You're banned.")
                    .build())
            .addParameters(
                ImmutableCommandParameter.<User>builder()
                    .type(User.class)
                    .description("The user to be banned.")
                    .optional(false)
                    .resetIfAbsent(false)
                    .build())
            .build());
    CommandHandler<String> handler = CommandHandler.of();
    handler.registerCommand(this);
    handler.setInvalidCommandConsumer(str -> System.out.println("Sorry, " + str + " that's an invalid command!"));
    handler.getParser().applyParser(User.class, DefaultParser.generateParser((s) -> new User()));
    ContentDistributor<String> distributor = ContentDistributor.defaultPrefixDistributor(handler, "!");
    distributor.handleContent("admin", "!ban <user>");
  }

  @Override
  public void accept(CommandEvent<String> commandEvent) {
    commandEvent.expectNonOptional(User.class, 0).ban();
  }
}
