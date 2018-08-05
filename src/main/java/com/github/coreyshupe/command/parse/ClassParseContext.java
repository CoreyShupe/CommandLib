package com.github.coreyshupe.command.parse;

import java.util.ArrayDeque;
import java.util.Optional;

/**
 * The context which is used to parse. Contains a {@link ArrayDeque} of existing arguments and an
 * {@link I} author.
 *
 * @param <I> The type of author.
 * @author CoreyShupe, created on 2018/08/01
 */
public class ClassParseContext<I> {

  private I author;
  private ArrayDeque<String> content;

  public ClassParseContext(I author, ArrayDeque<String> content) {
    this.author = author;
    this.content = content;
  }

  public void setContent(ArrayDeque<String> newContent) {
    this.content = newContent;
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

  public ArrayDeque<String> getContent() {
    return content;
  }
}
