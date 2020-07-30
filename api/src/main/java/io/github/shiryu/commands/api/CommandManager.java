package io.github.shiryu.commands.api;

import io.github.shiryu.commands.api.models.CommandExecutable;
import io.github.shiryu.commands.api.parameter.ParameterType;
import io.github.shiryu.commands.api.sender.SimpleSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public interface CommandManager<T> {

    void handle(@NotNull final T plugin);

    void registerCommand(@NotNull final CommandHandler command);

    <X> void registerParameterType(@NotNull final Class<X> clazz, @NotNull final ParameterType<X> parameterType);

    @NotNull
    Object transformParameter(@NotNull final SimpleSender sender, @NotNull final String value, @NotNull final Class<?> clazz);

    @NotNull
    CommandExecutable evalCommand(@NotNull final SimpleSender sender, @NotNull final String command);

    @NotNull
    List<CommandExecutable> getCommands();

    @NotNull
    Map<Class<?>, ParameterType> getParameterTypes();



}
