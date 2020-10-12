package io.github.shiryu.commands.bukkit;

import io.github.shiryu.commands.api.AbstractCommandManager;
import io.github.shiryu.commands.api.CommandHandler;
import io.github.shiryu.commands.api.annotations.Command;
import io.github.shiryu.commands.api.annotations.Parameter;
import io.github.shiryu.commands.api.models.SimpleParameter;
import io.github.shiryu.commands.api.parameter.defaults.*;
import io.github.shiryu.commands.bukkit.command.BukkitExecutable;
import io.github.shiryu.commands.bukkit.listener.BukkitCommandListener;
import io.github.shiryu.commands.bukkit.parameter.GameModeParameterType;
import io.github.shiryu.commands.bukkit.parameter.PlayerParameterType;
import io.github.shiryu.commands.bukkit.parameter.UUIDParameterType;
import io.github.shiryu.commands.bukkit.parameter.WorldParameterType;
import io.github.shiryu.commands.bukkit.util.ReflectionUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

@Getter
public class BukkitCommandManager extends AbstractCommandManager<Plugin> {

    private BukkitCommandMap commandMap;

    @Override
    public void handle(@NotNull final Plugin plugin) {
        this.plugin = plugin;
        this.commandMap = new BukkitCommandMap(plugin.getServer(), this);

        Bukkit.getPluginManager().registerEvents(
                new BukkitCommandListener(this),
                plugin
        );

        final Field commandMapField = ReflectionUtil.findField(
                plugin.getServer().getClass(),
                "commandMap"
        );

        final Field knownCommandsField = ReflectionUtil.findField(
                SimpleCommandMap.class,
                "knownCommands"
        );

        final Object oldCommandMap = ReflectionUtil.getField(commandMapField, plugin.getServer());

        ReflectionUtil.setField(
                knownCommandsField,
                ReflectionUtil.getField(
                        knownCommandsField,
                        oldCommandMap
                )
        );

        ReflectionUtil.setField(
                commandMapField,
                commandMap
        );
        
        registerParameterType(Boolean.class, new BooleanParameterType());
        registerParameterType(Double.class, new DoubleParameterType());
        registerParameterType(Float.class, new FloatParameterType());
        registerParameterType(Integer.class, new IntegerParameterType());
        registerParameterType(Long.class, new LongParameterType());
        registerParameterType(GameMode.class, new GameModeParameterType());
        registerParameterType(Player.class, new PlayerParameterType());
        registerParameterType(UUID.class, new UUIDParameterType());
        registerParameterType(World.class, new WorldParameterType());
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
                    new BukkitExecutable(
                            command,
                            commandAnnotation.names(),
                            commandAnnotation.permission(),
                            parameters,
                            method
                    )
            );

            commands.sort(Comparator.comparingInt(o -> o.getName().length()));
        }
    }

}
