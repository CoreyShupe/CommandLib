package com.github.coreyshupe.command.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines that the command method does not care about the author. Removes the required first arg as
 * the author.
 *
 * @author CoreyShupe, created on 2018/08/01
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface IgnoreAuthor {}
