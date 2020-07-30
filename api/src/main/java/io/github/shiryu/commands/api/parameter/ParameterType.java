package io.github.shiryu.commands.api.parameter;

import io.github.shiryu.commands.api.sender.SimpleSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public interface ParameterType<T> {

    @NotNull
    T transform(@NotNull final SimpleSender sender, @NotNull final String value);

    @NotNull
    List<String> tabComplete(@NotNull final SimpleSender sender, @NotNull final Set<String> flags, @NotNull final String value);
}
