package io.github.shiryu.commands.api.models;

import io.github.shiryu.commands.api.CommandHandler;
import io.github.shiryu.commands.api.CommandManager;
import io.github.shiryu.commands.api.sender.SimpleSender;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;

@Getter
@RequiredArgsConstructor
public abstract class CommandExecutable {

    protected final CommandHandler base;
    protected final String[] names;
    protected final String permission;
    protected final List<SimpleParameter> parameters;
    protected final Method method;

    public boolean canAccess(@NotNull final SimpleSender sender){
        return permission.isEmpty() ? true : sender.hasPermission(permission);
    }

    @NotNull
    public String getName(){
        return names[0];
    }

    @NotNull
    public String getUsageString() {
        return getUsageString(getName());
    }

    @NotNull
    public String getUsageString(@NotNull final String aliasUsed) {
        StringBuilder stringBuilder = new StringBuilder();

        for (SimpleParameter parameter : getParameters()) {
            boolean needed = parameter.getDefaultValue().isEmpty();
            stringBuilder.append(needed ? "<" : "[").append(parameter.getName());
            stringBuilder.append(needed ? ">" : "]").append(" ");
        }

        return "/" + aliasUsed.toLowerCase(Locale.ENGLISH) + " " + stringBuilder.toString().trim().toLowerCase(Locale.ENGLISH);
    }

    public abstract void execute(@NotNull final CommandManager commandManager, @NotNull final SimpleSender sender, @NotNull final String[] args);
}
