package example;

import com.github.commandlib.javacord.JCordCommandRegistry;
import com.github.commandlib.javacord.JCordCommandUtil;
import com.github.commandlib.javacord.JCordConstructiveCommand;
import com.github.coreyshupe.commandlib.command.CommandEvent;
import com.github.coreyshupe.commandlib.command.CommandParameter;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

public class BanCommand extends JCordConstructiveCommand {

  public BanCommand() {
    super("ban");
  }

  @Override
  public void init() {
    addParams(CommandParameter.ofBuilder(User.class).optional(false).resetIfAbsent(false));
    setNoPermissionConsumer(
        event -> event.getChannel().sendMessage("You may not use this command"));
    setNoPermissionPredicate(
        JCordCommandUtil.createDiscordPermissionPredicate(PermissionType.ADMINISTRATOR));
  }

  @Override
  public void accept(CommandEvent<MessageCreateEvent> event) {
    User target = event.expectNonOptional(User.class, 0);
    event.getAuthor().getServer().map(server -> server.banUser(target));
  }

  public static void main(String[] args) {
    DiscordApi api = new DiscordApiBuilder().setToken("Your token").login().join();
    JCordCommandRegistry registry = new JCordCommandRegistry();
    registry.registerCommand(new BanCommand());
    registry.registerWith(api, "!"); // (!ban <@userId> || !ban userId) will ban the user
  }
}
