package com.github.coreyshupe.commandlib.parse;

import java.util.ArrayDeque;
import java.util.Optional;

public class ClassParseContext<I> {

  private I author;
  private ArrayDeque<String> content;

  public ClassParseContext(I author, ArrayDeque<String> content) {
    this.author = author;
    this.content = content;
  }

  public void insertFirst(String piece) {
    content.addFirst(piece);
  }

  public Optional<String> nextPiece() {
    return Optional.ofNullable(content.poll());
  }

  public Optional<String> lookAtNext() {
    return Optional.ofNullable(content.getFirst());
  }

  public boolean hasMore() {
    return !content.isEmpty();
  }

  public I getAuthor() {
    return author;
  }
}
