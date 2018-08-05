package com.github.coreyshupe.command.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a class with a set of commands. Can either be shown as a {@link java.lang.reflect.Field}
 * to be set with a blank constructor. Or a blank {@link java.lang.reflect.Method} which constructs
 * a new instance of a class with a set of commands.
 *
 * @author CoreyShupe, created on 2018/08/01
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CommandSet {}
