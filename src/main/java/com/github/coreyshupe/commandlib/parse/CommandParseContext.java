package com.github.coreyshupe.commandlib.parse;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * The context which a command uses to parse commands.
 *
 * @author CoreyShupe, created on 2018/08/05
 * @param <I> The author typing.
 */
public class CommandParseContext<I> implements Supplier<String> {
  private final I author;
  private final ArrayDeque<String> currentlyAvailable;
  private final ArrayDeque<String> currentlyUsed;

  public CommandParseContext(I author, String content) {
    this.author = author;
    this.currentlyAvailable = new ArrayDeque<>(Arrays.asList(content.split(" ")));
    this.currentlyUsed = new ArrayDeque<>();
  }

  public I getAuthor() {
    return author;
  }

  public Optional<String> peekAtNext() {
    return Optional.ofNullable(currentlyAvailable.getFirst());
  }

  public Optional<String> pollNext() {
    if (!hasMore()) {
      return Optional.empty();
    }
    String next = currentlyAvailable.poll();
    if (next == null) {
      return Optional.empty();
    }
    currentlyUsed.offerFirst(next);
    return Optional.of(next);
  }

  @Override
  public String get() {
    return pollNext()
        .orElseThrow(() -> new IllegalStateException("Failed to retrieve next string."));
  }

  public boolean hasMore() {
    return !currentlyAvailable.isEmpty();
  }

  @CanIgnoreReturnValue
  public boolean insert(String str) {
    return currentlyAvailable.offerFirst(str);
  }

  public void resetUsed() {
    String next;
    while ((next = currentlyUsed.poll()) != null) {
      currentlyAvailable.offerFirst(next);
    }
    clearUsed();
  }

  public void clearUsed() {
    currentlyUsed.clear();
  }

  public Iterator<String> retrieveRest() {
    return currentlyAvailable.iterator();
  }
}
