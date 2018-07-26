package com.github.coreyshupe.commandlib.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Command {
  String value();

  String description() default "No valid description provided.";

  String[] aliases() default {};

  Class<?>[] optionalClassDefinitions() default {};

  boolean ignoreAuthor() default false;

  boolean assumeProceedingPresent() default true;
}
