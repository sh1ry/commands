package io.github.shiryu.commands.api.models;

import io.github.shiryu.commands.api.CommandHandler;
import io.github.shiryu.commands.api.CommandManager;
import io.github.shiryu.commands.api.locale.CommandLocale;
import io.github.shiryu.commands.api.sender.SimpleSender;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class SimpleCommand {

    private final CommandHandler base;
    private final String[] names;
    private final String permission;
    private final List<SimpleParameter> parameters;
    private final Method method;

    public boolean canAccess(@NotNull final SimpleSender sender){
        if (permission.isEmpty()) return true;

        return sender.hasPermission(permission);
    }

    @NotNull
    public String getName(){
        return this.names[0];
    }

    @NotNull
    public String getUsageString() {
        return (getUsageString(getName()));
    }

    @NotNull
    public String getUsageString(@NotNull final String aliasUsed) {
        StringBuilder stringBuilder = new StringBuilder();

        for (SimpleParameter parameter : getParameters()) {
            boolean needed = parameter.getDefaultValue().isEmpty();
            stringBuilder.append(needed ? "<" : "[").append(parameter.getName());
            stringBuilder.append(needed ? ">" : "]").append(" ");
        }

        return ("/" + aliasUsed.toLowerCase() + " " + stringBuilder.toString().trim().toLowerCase());
    }

    public void execute(@NotNull final CommandManager commandManager, @NotNull final SimpleSender sender, @NotNull final String[] args) {
        final List<Object> transformedParameters = new ArrayList<>();

        transformedParameters.add(sender);

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
