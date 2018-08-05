package examples;

import com.github.coreyshupe.command.CommandFactory;
import com.github.coreyshupe.command.annotations.CommandSet;
import com.github.coreyshupe.command.annotations.DoNotInit;

/** @author CoreyShupe, created on 2018/08/01 */
@DoNotInit
public class CommandsContainer {

  private final CommandFactory<Integer> commandFactory;
  @CommandSet private Commands commands;

  public CommandsContainer() {
    this.commandFactory = CommandFactory.of();
    this.commandFactory.registerCommands(CommandsContainer.class);
  }
}
