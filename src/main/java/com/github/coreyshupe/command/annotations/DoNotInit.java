package com.github.coreyshupe.command.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to stop the {@link com.github.coreyshupe.command.CommandFactory} from
 * initializing the class when registering commands. Should only be used when only {@link
 * CommandSet}s are defined in the class.
 *
 * @author CoreyShupe, created on 2018/08/04
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DoNotInit {}
