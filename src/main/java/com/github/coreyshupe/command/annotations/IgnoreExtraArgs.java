package com.github.coreyshupe.command.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines that the command doesn't care about the rest of the parameters entered.
 *
 * @author CoreyShupe, created on 2018/08/04
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface IgnoreExtraArgs {}
