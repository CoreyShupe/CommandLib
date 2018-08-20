package example;

import com.github.coreyshupe.commandlib.CommandHandler;
import com.github.coreyshupe.commandlib.parse.DefaultParser;

public class CommandBase {

  private final CommandHandler<String> commandHandler;

  public CommandBase() {
    this.commandHandler = CommandHandler.of("!");
    this.commandHandler
        .getParser()
        .applyParser(User.class, DefaultParser.generateParser(s -> new User()));
    this.commandHandler.setInvalidCommandConsumer(
        (author, command) ->
            System.out.printf("Sorry %s, %s is an invalid command.\n", author, command));
    this.commandHandler.registerCommand(new BanCommand());
    reviewMessage("Admin", "!ban user");
  }

  public void reviewMessage(String commandUser, String content) {
    this.commandHandler.acceptContent(commandUser, content);
  }
}
