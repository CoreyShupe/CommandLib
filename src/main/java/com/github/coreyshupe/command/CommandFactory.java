package com.github.coreyshupe.command;

import com.github.coreyshupe.command.annotations.Command;
import com.github.coreyshupe.command.annotations.CommandSet;
import com.github.coreyshupe.command.annotations.Default;
import com.github.coreyshupe.command.annotations.Description;
import com.github.coreyshupe.command.annotations.DoNotInit;
import com.github.coreyshupe.command.annotations.IgnoreAuthor;
import com.github.coreyshupe.command.annotations.IgnoreExtraArgs;
import com.github.coreyshupe.command.annotations.Optional;
import com.github.coreyshupe.command.annotations.ResetIfAbsent;
import com.github.coreyshupe.command.exceptions.CommandNotFoundException;
import com.github.coreyshupe.command.parse.ClassParseContext;
import com.github.coreyshupe.command.parse.ClassParser;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * The factory which handles and distributes commands.
 *
 * @param <I> The author typing.
 * @author CoreyShupe, created on 2018/08/01
 */
public class CommandFactory<I> {
  private final ClassParser<I> classParser;
  private final Set<AbstractCommand<I>> commands;
  private Predicate<String> validCommandChecker;
  private Function<String, Pair<String, String>> contentParser;

  public CommandFactory() {
    this.classParser = new ClassParser<>();
    this.commands = new HashSet<>();
  }

  public ClassParser<I> getClassParser() {
    return classParser;
  }

  public Set<AbstractCommand<I>> getCommands() {
    return commands;
  }

  public java.util.Optional<AbstractCommand<I>> getCommand(String commandName) {
    return commands
        .stream()
        .filter(abstractCommand -> abstractCommand.matchesCommand(commandName))
        .findFirst();
  }

  public void setValidCommandChecker(Predicate<String> validCommandChecker) {
    this.validCommandChecker = validCommandChecker;
  }

  public void setContentParser(Function<String, Pair<String, String>> contentParser) {
    this.contentParser = contentParser;
  }

  public void publishRawContent(I author, String content) {
    if (validCommandChecker == null) {
      throw new IllegalStateException(
          "Valid command checker not currently implemented. See CommandFactory#setValidCommandChecker.");
    }
    if (validCommandChecker.test(content)) {
      publishRawCommand(author, content);
    }
  }

  public void publishRawCommand(I author, String content) {
    if (contentParser == null) {
      throw new IllegalStateException(
          "Content parser not currently setup. See CommandFactory#setContentParser.");
    }
    var commandInformation = contentParser.apply(content);
    runCommand(author, commandInformation.getKey(), commandInformation.getValue());
  }

  public void runCommand(I author, String command, String content) {
    getCommand(command)
        .ifPresentOrElse(
            iAbstractCommand -> runCommand(author, iAbstractCommand, content),
            () -> {
              throw new CommandNotFoundException(
                  "Command `%s` is not currently registered.", command);
            });
  }

  public void runCommand(I author, AbstractCommand<I> command, String content) {
    command.accept(author, content);
  }

  public void registerCommands(Class<?>[] classes) {
    for (var clazz : classes) {
      registerCommands(clazz);
    }
  }

  public void registerCommands(Class<?> clazz) {
    for (var field : clazz.getDeclaredFields()) {
      if (field.isAnnotationPresent(CommandSet.class)) {
        var fieldType = field.getType();
        try {
          var constructor = fieldType.getConstructor();
          registerCommands(constructor.newInstance());
        } catch (NoSuchMethodException ex) {
          // do nothing, we only care if it has an empty constructor.
        } catch (ReflectiveOperationException ex2) {
          throw new SecurityException(
              "Cannot access the class " + clazz.getName() + "'s empty constructor.", ex2);
        }
      }
    }
    if (clazz.isAnnotationPresent(DoNotInit.class)) {
      return;
    }
    try {
      var constructor = clazz.getConstructor();
      registerCommands(constructor.newInstance());
    } catch (NoSuchMethodException ex) {
      // do nothing, we only care if it has an empty constructor.
    } catch (ReflectiveOperationException ex2) {
      throw new SecurityException(
          "Cannot access the class " + clazz.getName() + "'s empty constructor.", ex2);
    }
  }

  public void registerCommands(Object... classInstances) {
    for (var classInstance : classInstances) {
      registerCommands(classInstance);
    }
  }

  public void registerCommands(Object classInstance) {
    for (var method : classInstance.getClass().getMethods()) {
      registerCommand(classInstance, method);
    }
  }

  public void registerCommand(Object classInstance, Method method) {
    if (!method.isAnnotationPresent(Command.class)) {
      return;
    }
    var command = method.getAnnotationsByType(Command.class)[0];
    var ignoreAuthor = method.isAnnotationPresent(IgnoreAuthor.class);
    var ignoreExtra = method.isAnnotationPresent(IgnoreExtraArgs.class);
    var parameterInfo = generateParameters(method, ignoreAuthor, ignoreExtra);
    var parameters = parameterInfo.getKey();
    var parameterDescriptors = parameterInfo.getValue();
    var abstractCommand =
        new AbstractCommand<I>(
            command.value(), command.aliases(), command.description(), parameterDescriptors) {
          @Override
          public void accept(I author, String content) {
            var context =
                new ClassParseContext<>(
                    author, new ArrayDeque<>(Arrays.asList(content.trim().split(" "))));
            var hasMore = content.length() > 0;
            var objects = new Object[method.getParameterCount()];
            if (!ignoreAuthor) {
              objects[0] = author;
            }
            var position = ignoreAuthor ? 0 : 1;
            for (var parameter : parameters) {
              var result = parameter.parse(context, classParser);
              objects[position] = result.getKey();
              context.setContent(result.getValue());
              position++;
            }
            if (!ignoreExtra) {
              var extraContent = context.getContent();
              var extra = new String[extraContent.size()];
              for (int i = 0; i < extra.length; i++) {
                extra[i] = extraContent.poll();
              }
              objects[objects.length - 1] = extra;
            }
            try {
              method.invoke(classInstance, objects);
            } catch (ReflectiveOperationException ex) {
              throw new SecurityException(
                  String.format(
                      "Failed to invoke the method: %s#%s as a command",
                      classInstance.getClass().getName(), method.getName()),
                  ex);
            }
          }
        };
    registerCommand(abstractCommand);
  }

  public Pair<List<CommandParameter<?>>, String[]> generateParameters(
      Method method, boolean ignoreAuthor, boolean ignoreExtra) {
    var parameters = new ArrayList<CommandParameter<?>>();
    var paramTypes = method.getParameterTypes();
    var paramAnnotations = method.getParameterAnnotations();
    var paramDescriptors = new ArrayList<String>();
    for (var i = ignoreAuthor ? 0 : 1; i < paramTypes.length - (ignoreExtra ? 0 : 1); i++) {
      var type = paramTypes[i];
      boolean optional = false;
      boolean reset = false;
      String[] defaultInfo = new String[] {};
      String description = null;
      for (var annotation : paramAnnotations[i]) {
        var annotationType = annotation.annotationType();
        if (annotationType == Default.class) {
          defaultInfo = ((Default) annotation).value().split(" ");
        } else if (annotationType == Optional.class) {
          optional = true;
        } else if (annotationType == ResetIfAbsent.class) {
          reset = true;
        } else if (annotationType == Description.class) {
          description = ((Description) annotation).value();
        }
      }
      paramDescriptors.add(description == null ? "N/A" : description);
      var parameter = new CommandParameter<>(type, reset, optional, i, defaultInfo);
      parameters.add(parameter);
    }
    return Pair.of(parameters, paramDescriptors.toArray(new String[] {}));
  }

  public void registerCommand(AbstractCommand<I> abstractCommand) {
    commands.add(abstractCommand);
  }

  public void setupDefaultParsingInstructions(String prefix) {
    setValidCommandChecker(generateDefaultValidCommandChecker(prefix));
    setContentParser(generateDefaultCommandParser(prefix));
  }

  public static <T> CommandFactory<T> of() {
    return new CommandFactory<>();
  }

  public static Predicate<String> generateDefaultValidCommandChecker(String prefix) {
    return (str) ->
        str.startsWith(prefix)
            && str.length() > prefix.length() + 1
            && str.charAt(prefix.length()) != ' ';
  }

  public static Function<String, Pair<String, String>> generateDefaultCommandParser(String prefix) {
    return (str) -> {
      String[] split = str.substring(prefix.length()).split(" ");
      return Pair.of(
          split[0], str.length() == split[0].length() ? "" : str.substring(split[0].length() + 1));
    };
  }
}
