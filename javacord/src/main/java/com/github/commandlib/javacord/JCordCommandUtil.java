package com.github.commandlib.javacord;

import java.util.Optional;
import java.util.function.Predicate;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.permission.PermissionState;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

public final class JCordCommandUtil {

  public static Predicate<MessageCreateEvent> createDiscordPermissionPredicate(
      PermissionType type) {
    return event -> hasPermission(event.getMessage().getAuthor(), event.getServer(), type);
  }

  public static boolean hasPermission(
      MessageAuthor author, Optional<Server> server, PermissionType type) {
    return hasPermission(author.asUser(), server, type);
  }

  public static boolean hasPermission(
      Optional<User> user, Optional<Server> server, PermissionType type) {
    if (user.isPresent() && server.isPresent()) {
      return server.get().getPermissions(user.get()).getState(type) == PermissionState.ALLOWED;
    }
    return false;
  }

  private JCordCommandUtil() {}
}
