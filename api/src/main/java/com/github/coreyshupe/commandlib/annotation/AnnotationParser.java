package com.github.coreyshupe.commandlib.annotation;

import com.github.coreyshupe.commandlib.command.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class AnnotationParser<I> {

  private final Object object;
  private final Method method;

  public AnnotationParser(Object object, Method method) {
    this.object = object;
    this.method = method;
  }

  public Command<I> generateCommand(CommandBody bodyAnnotation) {
    final Set<Integer> authorAreas = new HashSet<>();
    final Set<Integer> extraAreas = new HashSet<>();
    final List<CommandParameter<?>> parameters = new ArrayList<>();
    final Parameter[] params = method.getParameters();
    for (int i = 0; i < params.length; i++) {
      Parameter parameter = params[i];
      if (parameter.getAnnotation(CommandBody.Author.class) != null) {
        authorAreas.add(i);
      } else if (parameter.getAnnotation(CommandBody.Extra.class) != null) {
        extraAreas.add(i);
      } else {
        CommandBody.Param expectedDoc = parameter.getAnnotation(CommandBody.Param.class);
        if (expectedDoc == null) {
          parameters.add(CommandParameter.ofBuilder(parameter.getType()).build());
        } else {
          parameters.add(parseParameter(parameter.getType(), expectedDoc));
        }
      }
    }
    String command = bodyAnnotation.value();
    final CommandInformation<I> information =
        ImmutableCommandInformation.<I>builder()
            .name(command)
            .addAliases(bodyAnnotation.aliases())
            .description(bodyAnnotation.description())
            .putAlternateInformation("example", bodyAnnotation.example())
            .permissionPredicate(receivePermissionPredicate(command))
            .noPermissionConsumer(receivePermissionConsumer(command))
            .parameters(parameters)
            .build();
    return new Command<>() {
      @Override
      public CommandInformation<I> getInformation() {
        return information;
      }

      @Override
      public void accept(CommandEvent<I> event) {
        I author = event.getAuthor();
        List<String> extra = event.getExtra();
        int listIndex = 0;
        Object[] paramInstances = new Object[params.length];
        for (int i = 0; i < params.length; i++) {
          if (authorAreas.contains(i)) {
            paramInstances[i] = author;
          } else if (extraAreas.contains(i)) {
            paramInstances[i] = extra;
          } else {
            paramInstances[i] =
                parameters.get(listIndex).getOptional().orElse(false)
                    ? event.expectOptional(params[i].getType(), listIndex).orElse(null)
                    : event.expectNonOptional(params[i].getType(), listIndex);
            listIndex++;
          }
        }
        try {
          method.invoke(object, paramInstances);
        } catch (IllegalAccessException | InvocationTargetException e) {
          e.printStackTrace();
        }
      }
    };
  }

  @SuppressWarnings("unchecked")
  private Optional<Predicate<I>> receivePermissionPredicate(String command) {
    try {
      Field field = object.getClass().getField(command + "NoPermissionPredicate");
      if (field.getType() == Predicate.class) {
        return Optional.of((Predicate<I>) field.get(object));
      }
      return Optional.empty();
    } catch (NoSuchFieldException | IllegalAccessException e) {
      return Optional.empty();
    }
  }

  @SuppressWarnings("unchecked")
  private Optional<Consumer<I>> receivePermissionConsumer(String command) {
    try {
      Field field = object.getClass().getField(command + "NoPermissionConsumer");
      if (field.getType() == Consumer.class) {
        return Optional.of((Consumer<I>) field.get(object));
      }
      return Optional.empty();
    } catch (NoSuchFieldException | IllegalAccessException e) {
      return Optional.empty();
    }
  }

  public CommandParameter<?> parseParameter(Class<?> type, CommandBody.Param paramAnnotation) {
    return CommandParameter.ofBuilder(type)
        .defaultValue(paramAnnotation.defaultValue())
        .description(paramAnnotation.description())
        .optional(paramAnnotation.optional())
        .resetIfAbsent(paramAnnotation.resetIfAbsent())
        .build();
  }
}
