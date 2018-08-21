package example;

import com.github.coreyshupe.commandlib.command.*;
import com.google.common.collect.ImmutableMap;

/** @author CoreyShupe, created on 2018/08/06 */
public class BanCommand extends ConstructedCommand<String> {
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
  }

  @Override
  public void accept(CommandEvent<String> commandEvent) {
    commandEvent.expectNonOptional(User.class, 0).ban();
  }
}
