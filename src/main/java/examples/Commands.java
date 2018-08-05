package examples;

import com.github.coreyshupe.command.annotations.Command;
import com.github.coreyshupe.command.annotations.Default;
import com.github.coreyshupe.command.annotations.IgnoreAuthor;

/**
 * Example class to demonstrate a simple command set.
 *
 * @author CoreyShupe, created on 2018/08/01
 */
public class Commands {
  @IgnoreAuthor
  @Command(value = "help", description = "Displays a help page for commands.")
  public void helpExecute(@Default("1") Integer page, String... leftover) {
    System.out.println(String.format("Found page %s of help.", page));
  }
}
