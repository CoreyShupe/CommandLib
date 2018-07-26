package com.github.coreyshupe.commandlib;

import com.github.coreyshupe.commandlib.command.AbstractCommand;
import com.github.coreyshupe.commandlib.command.Command;
import com.github.coreyshupe.commandlib.parse.ClassParser;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class CommandFactory<I> {

  private final Class<I> authorClass;
  private final ClassParser<I> classParser;
  private Predicate<String> validCommandChecker;
  private Function<String, Pair<String, String>> contentParser;
  private final Set<AbstractCommand<I>> commands;

  public CommandFactory(Class<I> authorClass) {
    this.authorClass = authorClass;
    this.classParser = new ClassParser<>();
    this.commands = new HashSet<>();
  }

  public ClassParser<I> getClassParser() {
    return classParser;
  }

  public void setValidCommandChecker(Predicate<String> validCommandChecker) {
    this.validCommandChecker = validCommandChecker;
  }

  public void setContentParser(Function<String, Pair<String, String>> contentParser) {
    this.contentParser = contentParser;
  }

  public void pushRawContent(I author, String content) {
    if (validCommandChecker == null) {
      throw new IllegalStateException("The CommandFactory must contain a valid command checker.");
    }
    if (validCommandChecker.test(content)) {
      pushRawCommand(author, content);
    }
  }

  public void pushRawCommand(I author, String content) {
    if (contentParser == null) {
      throw new IllegalStateException(
          "The CommandFactory must contain a content parser for commands.");
    }
    var context = contentParser.apply(content);
    applyCommand(author, context.getKey(), context.getValue());
  }

  public void applyCommand(I author, String commandName, String commandContent) {
    commands
        .stream()
        .filter(command -> command.checkCommand(commandName))
        .findFirst()
        .ifPresent(command -> command.accept(author, commandContent));
  }

  public void registerCommands(Object classInstance) {
    registerCommands(classInstance, (x) -> true);
  }

  public void registerCommand(Object classInstance, Method method) {
    registerCommand(classInstance, method, (x) -> true);
  }

  public void registerCommands(Object classInstance, Predicate<I> permissionChecker) {
    for (Method method : classInstance.getClass().getMethods()) {
      Command[] annotations = method.getAnnotationsByType(Command.class);
      if (annotations.length > 0) {
        registerCommand(classInstance, method, permissionChecker, annotations[0]);
      }
    }
  }

  public void registerCommand(Object classInstance, Method method, Predicate<I> permissionChecker) {
    Command[] annotations = method.getAnnotationsByType(Command.class);
    if (annotations.length == 0) {
      throw new IllegalArgumentException("Method is not annotated with `@Command`.");
    }
    registerCommand(classInstance, method, permissionChecker, annotations[0]);
  }

  private void registerCommand(
      Object classInstance, Method method, Predicate<I> permissionChecker, Command annotation) {
    Class<?>[] params = method.getParameterTypes();
    if (params[0] != authorClass && !annotation.ignoreAuthor()) {
      throw new IllegalStateException(
          "The command method must have the author as the first parameter.");
    }
    Set<Integer> optionalParams = new HashSet<>();
    Class<?>[] listening = new Class<?>[params.length - (annotation.ignoreAuthor() ? 1 : 2)];
    for (int i = (annotation.ignoreAuthor() ? 0 : 1), x = 0, z = 0;
        i < params.length - 1;
        i++, x++) {
      Class<?> clazz = params[i];
      if (clazz == Optional.class) {
        optionalParams.add(x);
        clazz = annotation.optionalClassDefinitions()[z++];
      }
      listening[x] = clazz;
    }
    registerCommand(
        AbstractCommand.newCommand(
            authorClass,
            annotation,
            permissionChecker,
            (author, content) -> {
              ArrayDeque<Optional<?>> result = classParser.parse(author, content, listening);
              int objectIndex = 0;
              Object[] objects = new Object[params.length];
              if (!annotation.ignoreAuthor()) {
                objects[objectIndex++] = author;
              }
              for (int i = 0; i < listening.length; i++) {
                Optional<?> optional = result.pollFirst();
                boolean isOptionalParam = optionalParams.contains(i);
                if (optional == null || !optional.isPresent()) {
                  if (isOptionalParam) {
                    objects[objectIndex++] = Optional.empty();
                    continue;
                  }
                  // TODO throw error
                  break;
                }
                objects[objectIndex++] = optional.get();
              }
              if (annotation.assumeProceedingPresent()) {
                List<Object> proceeding = new ArrayList<>();
                result.iterator().forEachRemaining(x -> proceeding.add(x.get()));
                objects[objectIndex] = proceeding;
              } else {
                List<Optional<?>> proceeding = new ArrayList<>();
                result.iterator().forEachRemaining(proceeding::add);
                objects[objectIndex] = proceeding;
              }
              try {
                method.invoke(classInstance, objects);
              } catch (ReflectiveOperationException ex) {
                throw new SecurityException(ex);
              }
            }));
  }

  public void registerCommand(AbstractCommand<I> command) {
    commands.add(command);
  }

  public static <T> CommandFactory<T> of(Class<T> author) {
    return new CommandFactory<>(author);
  }
}
