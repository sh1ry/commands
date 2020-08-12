package io.github.shiryu.commands.api;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import io.github.shiryu.commands.api.locale.CommandLocale;
import io.github.shiryu.commands.api.models.CommandExecutable;
import io.github.shiryu.commands.api.models.SimpleParameter;
import io.github.shiryu.commands.api.parameter.ParameterType;
import io.github.shiryu.commands.api.sender.SimpleSender;
import org.apache.commons.lang3.StringUtils;
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

    @NotNull
    public List<String> tabCompletions(@NotNull final SimpleSender sender, @NotNull final List<String> vanillaCompletions){
        final List<String> completions = new ArrayList<>(vanillaCompletions);

        for (CommandExecutable command : this.commands){
            String cmdLine = command.getName();
            int spaceIndex = cmdLine.indexOf(' ');

            if (!command.canAccess(sender))
                continue;

            for (String alias : command.getNames()) {
                String split = alias.split(" ")[0];

                if (spaceIndex != -1)
                    split = alias;


                if (StringUtils.startsWithIgnoreCase(split.trim(), cmdLine.trim()) || StringUtils.startsWithIgnoreCase(cmdLine.trim(), split.trim())) {
                    if (spaceIndex == -1 && cmdLine.length() <= alias.length()) {
                        completions.add("/" + split.toLowerCase());
                    } else if (cmdLine.toLowerCase().startsWith(alias.toLowerCase() + " ") && command.getParameters().size() > 0) {
                        int paramIndex = (cmdLine.split(" ").length - alias.split(" ").length);

                        if (paramIndex == command.getParameters().size() || !cmdLine.endsWith(" "))
                            paramIndex = paramIndex - 1;


                        if (paramIndex < 0)
                            paramIndex = 0;


                        SimpleParameter paramData = command.getParameters().get(paramIndex);
                        String[] params = cmdLine.split(" ");

                        for (String completion : tabCompleteParameter(sender, cmdLine.endsWith(" ") ? "" : params[params.length - 1], paramData.getParameterClass(), paramData.getTabCompleteFlags()))
                            completions.add(completion);

                        break;
                    } else {
                        String halfSplitString = split.toLowerCase().replaceFirst(alias.split(" ")[0].toLowerCase(), "").trim();
                        String[] splitString = halfSplitString.split(" ");

                        String fixedAlias = splitString[splitString.length - 1].trim();
                        String lastArg = cmdLine.endsWith(" ") ? "" : cmdLine.split(" ")[cmdLine.split(" ").length - 1];

                        if (fixedAlias.length() >= lastArg.length()) {
                            completions.add(fixedAlias);
                        }
                    }
                }
            }
        }

        Collections.sort(completions, (o1, o2) -> (o2.length() - o1.length()));

        return completions;
    }
}
