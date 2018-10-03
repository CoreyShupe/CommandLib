package com.github.coreyshupe.commandlib.command;

import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class ConstructiveCommand<I> implements Command<I> {

  private final ImmutableCommandInformation.Builder<I> infoBuilder;
  private CommandInformation<I> builtInformation;

  public ConstructiveCommand(String name) {
    this.infoBuilder = ImmutableCommandInformation.<I>builder().name(name);
    init();
    build();
  }

  public abstract void init();

  @CanIgnoreReturnValue
  public final <T> ConstructiveCommand<I> addParams(ImmutableCommandParameter.Builder<T> param) {
    assertUnbuilt();
    addParams(param.build());
    return this;
  }

  @CanIgnoreReturnValue
  @SafeVarargs
  public final <T> ConstructiveCommand<I> addParams(
      ImmutableCommandParameter.Builder<T>... params) {
    for (ImmutableCommandParameter.Builder<T> builder : params) {
      assertUnbuilt();
      addParams(builder);
    }
    return this;
  }

  @CanIgnoreReturnValue
  public final <T> ConstructiveCommand<I> addAllParamBuilders(
      Iterable<ImmutableCommandParameter.Builder<T>> params) {
    assertUnbuilt();
    params.forEach(this::addParams);
    return this;
  }

  @CanIgnoreReturnValue
  public final <T> ConstructiveCommand<I> setParamsFromBuilders(
      Iterable<ImmutableCommandParameter.Builder<T>> params) {
    assertUnbuilt();
    List<CommandParameter<T>> built = new ArrayList<>();
    params.forEach(builder -> built.add(builder.build()));
    setParams(built);
    return this;
  }

  @CanIgnoreReturnValue
  public final <T> ConstructiveCommand<I> addParams(CommandParameter<T> param) {
    assertUnbuilt();
    infoBuilder.addParameters(param);
    return this;
  }

  @CanIgnoreReturnValue
  @SafeVarargs
  public final <T> ConstructiveCommand<I> addParams(CommandParameter<T>... params) {
    assertUnbuilt();
    infoBuilder.addParameters(params);
    return this;
  }

  @CanIgnoreReturnValue
  public final <T> ConstructiveCommand<I> addAllParams(Iterable<CommandParameter<T>> params) {
    assertUnbuilt();
    infoBuilder.addAllParameters(params);
    return this;
  }

  @CanIgnoreReturnValue
  public final <T> ConstructiveCommand<I> setParams(Iterable<CommandParameter<T>> params) {
    assertUnbuilt();
    infoBuilder.parameters(params);
    return this;
  }

  @CanIgnoreReturnValue
  public final ConstructiveCommand<I> setDescription(String description) {
    assertUnbuilt();
    infoBuilder.description(description);
    return this;
  }

  @CanIgnoreReturnValue
  public final ConstructiveCommand<I> addAliases(String alias) {
    assertUnbuilt();
    infoBuilder.addAliases(alias);
    return this;
  }

  @CanIgnoreReturnValue
  public final ConstructiveCommand<I> addAliases(String... aliases) {
    assertUnbuilt();
    infoBuilder.addAliases(aliases);
    return this;
  }

  @CanIgnoreReturnValue
  public final ConstructiveCommand<I> addAllAliases(Iterable<String> aliases) {
    assertUnbuilt();
    infoBuilder.addAllAliases(aliases);
    return this;
  }

  @CanIgnoreReturnValue
  public final ConstructiveCommand<I> setAliases(Iterable<String> aliases) {
    assertUnbuilt();
    infoBuilder.aliases(aliases);
    return this;
  }

  @CanIgnoreReturnValue
  public final ConstructiveCommand<I> addAltInformation(String key, String value) {
    assertUnbuilt();
    infoBuilder.putAlternateInformation(key, value);
    return this;
  }

  @CanIgnoreReturnValue
  public final ConstructiveCommand<I> setNoPermissionConsumer(Consumer<I> consumer) {
    assertUnbuilt();
    infoBuilder.noPermissionConsumer(consumer);
    return this;
  }

  @CanIgnoreReturnValue
  public final ConstructiveCommand<I> setNoPermissionPredicate(Predicate<I> predicate) {
    assertUnbuilt();
    infoBuilder.permissionPredicate(predicate);
    return this;
  }

  private void assertUnbuilt() {
    Preconditions.checkState(
        builtInformation == null,
        "Command already built. Make sure to do all building within the #init method.");
  }

  private void build() {
    builtInformation = infoBuilder.build();
  }

  @Override
  public CommandInformation<I> getInformation() {
    return builtInformation;
  }
}
