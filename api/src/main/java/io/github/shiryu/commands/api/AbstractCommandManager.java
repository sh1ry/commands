package io.github.shiryu.commands.api;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import io.github.shiryu.commands.api.locale.CommandLocale;
import io.github.shiryu.commands.api.models.CommandExecutable;
import io.github.shiryu.commands.api.parameter.ParameterType;
import io.github.shiryu.commands.api.sender.SimpleSender;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class AbstractCommandManager<T> implements CommandManager<T> {

    protected final List<CommandExecutable> commands = Lists.newArrayList();
    protected final Map<Class<?>, ParameterType> parameterTypes = new HashMap<>();

    protected T plugin;

    @Override
    public <X> void registerParameterType(@NotNull final Class<X> clazz, @NotNull final ParameterType<X> parameterType) {
        parameterTypes.put(clazz, parameterType);
    }

    @Override
    public @NotNull Object transformParameter(@NotNull final SimpleSender sender, @NotNull final String value, @NotNull final Class<?> clazz) {
        return clazz.equals(String.class) ? value : parameterTypes.get(clazz).transform(sender, value);
    }

    @Override
    public @NotNull CommandExecutable evalCommand(@NotNull final SimpleSender sender, @NotNull final String command) {
        String[] args = {};
        CommandExecutable found = null;

        CommandLoop:
        for (CommandExecutable commandData : commands) {
            for (String alias : commandData.getNames()) {
                String messageString = command.toLowerCase(Locale.ENGLISH) + " ";
                String aliasString = alias.toLowerCase(Locale.ENGLISH) + " ";

                if (messageString.startsWith(aliasString)) {
                    found = commandData;

                    if (messageString.length() > aliasString.length() && found.getParameters().size() == 0)
                        continue;



                    if (command.length() > alias.length() + 1)
                        args = command.substring(alias.length() + 1).split(" ");


                    break CommandLoop;
                }
            }
        }

        if (found == null)
            return null;


        if (!found.canAccess(sender)) {
            sender.sendMessage(CommandLocale.NO_PERMISSION);

            return found;
        }

        found.execute(this, sender, args);


        return found;
    }

    @Override
    public @NotNull List<CommandExecutable> getCommands() {
        return this.commands;
    }

    @Override
    public @NotNull Map<Class<?>, ParameterType> getParameterTypes() {
        return this.parameterTypes;
    }

    @NotNull
    public List<String> tabCompleteParameter(@NotNull final SimpleSender sender, @NotNull final String value,
                                             @NotNull final Class<?> clazz, @NotNull final String[] tabCompleteFlags){
        if (!parameterTypes.containsKey(clazz)) return Lists.newArrayList();

        return parameterTypes.get(clazz).tabComplete(sender, ImmutableSet.copyOf(tabCompleteFlags), value);
    }
}
