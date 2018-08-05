package com.github.coreyshupe.command.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Forces the context queue to reset to its last position if the argument was not found.
 *
 * @author CoreyShupe, created on 2018/08/01
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ResetIfAbsent {}
