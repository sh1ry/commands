package io.github.shiryu.commands.bukkit.command;

import io.github.shiryu.commands.api.CommandHandler;
import io.github.shiryu.commands.api.CommandManager;
import io.github.shiryu.commands.api.locale.CommandLocale;
import io.github.shiryu.commands.api.models.CommandExecutable;
import io.github.shiryu.commands.api.models.SimpleParameter;
import io.github.shiryu.commands.api.sender.SimpleSender;
import io.github.shiryu.commands.bukkit.BukkitSender;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class BukkitExecutable extends CommandExecutable {

    public BukkitExecutable(@NotNull final CommandHandler base, @NotNull final String[] names,
                            @NotNull final String permission,
                            @NotNull final List<SimpleParameter> parameters,
                            @NotNull final Method method) {
        super(base, names, permission, parameters, method);
    }

    @Override
    public void execute(@NotNull final CommandManager commandManager, @NotNull final SimpleSender sender, @NotNull final String[] args) {
        final List<Object> transformedParameters = new ArrayList<>();

        if (sender instanceof BukkitSender) transformedParameters.add(((BukkitSender)sender).getSender());

        for (int parameterIndex = 0; parameterIndex < getParameters().size(); parameterIndex++) {
            SimpleParameter parameter = getParameters().get(parameterIndex);
            String passedParameter = (parameterIndex < args.length ? args[parameterIndex] : parameter.getDefaultValue()).trim();

            if (parameterIndex >= args.length && (parameter.getDefaultValue() == null || parameter.getDefaultValue().isEmpty())) {
                sender.sendMessage(String.format(CommandLocale.USAGE, getUsageString()));
                return;
            }

            Object result = commandManager.transformParameter(sender, passedParameter, parameter.getParameterClass());

            if (result == null) {
                return;
            }

            transformedParameters.add(result);
        }

        try {
            method.invoke(base, transformedParameters.toArray());
        } catch (Exception e) {
            sender.sendMessage(CommandLocale.ERROR);
            e.printStackTrace();
        }
    }
}
