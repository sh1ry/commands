package io.github.shiryu.commands.bungeecord;

import io.github.shiryu.commands.api.AbstractCommandManager;
import io.github.shiryu.commands.api.CommandHandler;
import io.github.shiryu.commands.api.annotations.Command;
import io.github.shiryu.commands.api.annotations.Parameter;
import io.github.shiryu.commands.api.models.SimpleParameter;
import io.github.shiryu.commands.api.parameter.defaults.*;
import io.github.shiryu.commands.bungeecord.command.BungeeExecutable;
import io.github.shiryu.commands.bungeecord.listener.BungeeCommandListener;
import io.github.shiryu.commands.bungeecord.parameter.ProxiedPlayerParameterType;
import io.github.shiryu.commands.bungeecord.parameter.UUIDParameterType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.protocol.Protocol;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class BungeecordCommandManager extends AbstractCommandManager<Plugin> {

    @Override
    public void handle(@NotNull Plugin plugin) {
        this.plugin = plugin;

        this.plugin.getProxy().getPluginManager().registerListener(this.plugin, new BungeecordCommandMap(this));
        this.plugin.getProxy().getPluginManager().registerListener(this.plugin, new BungeeCommandListener(this));

        registerParameterType(Boolean.class, new BooleanParameterType());
        registerParameterType(Double.class, new DoubleParameterType());
        registerParameterType(Float.class, new FloatParameterType());
        registerParameterType(Integer.class, new IntegerParameterType());
        registerParameterType(Long.class, new LongParameterType());
        registerParameterType(ProxiedPlayer.class, new ProxiedPlayerParameterType());
        registerParameterType(UUID.class, new UUIDParameterType());
    }

    @Override
    public void registerCommand(@NotNull final CommandHandler command) {
        final Class<?> commandClazz = command.getClass();

        for (Method method : commandClazz.getMethods()){
            final Command commandAnnotation = method.getAnnotation(Command.class);

            if (commandAnnotation == null) continue;
            final List<SimpleParameter> parameters = new ArrayList<>();

            for (int parameterIndex = 1; parameterIndex < method.getParameterTypes().length; parameterIndex++) {
                Parameter parameterAnnotation = null;

                for (Annotation annotation : method.getParameterAnnotations()[parameterIndex]) {
                    if (annotation instanceof Parameter) {
                        parameterAnnotation = (Parameter) annotation;
                        break;
                    }
                }

                if (parameterAnnotation == null) return;

                parameters.add(
                        new SimpleParameter(
                                parameterAnnotation.name(),
                                parameterAnnotation.defaultValue(),
                                parameterAnnotation.tabCompleteFlags(),
                                method.getParameterTypes()[parameterIndex]
                        )
                );
            }

            commands.add(
                    new BungeeExecutable(
                            command,
                            commandAnnotation.names(),
                            commandAnnotation.permission(),
                            parameters,
                            method
                    )
            );

            Collections.sort(commands, ((o1, o2) -> o1.getName().length() - o2.getName().length()));
        }
    }


    @NotNull
    public Plugin getPlugin(){
        return this.plugin;
    }
}
