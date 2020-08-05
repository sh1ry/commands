package io.github.shiryu.commands.bukkit;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sun.org.apache.xpath.internal.operations.Bool;
import io.github.shiryu.commands.api.AbstractCommandManager;
import io.github.shiryu.commands.api.CommandHandler;
import io.github.shiryu.commands.api.CommandManager;
import io.github.shiryu.commands.api.annotations.Command;
import io.github.shiryu.commands.api.annotations.Parameter;
import io.github.shiryu.commands.api.locale.CommandLocale;
import io.github.shiryu.commands.api.models.CommandExecutable;
import io.github.shiryu.commands.api.models.SimpleParameter;
import io.github.shiryu.commands.api.parameter.ParameterType;
import io.github.shiryu.commands.api.parameter.defaults.*;
import io.github.shiryu.commands.api.sender.SimpleSender;
import io.github.shiryu.commands.bukkit.command.BukkitExecutable;
import io.github.shiryu.commands.bukkit.listener.BukkitCommandListener;
import io.github.shiryu.commands.bukkit.parameter.GameModeParameterType;
import io.github.shiryu.commands.bukkit.parameter.PlayerParameterType;
import io.github.shiryu.commands.bukkit.parameter.UUIDParameterType;
import io.github.shiryu.commands.bukkit.parameter.WorldParameterType;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

@Getter
public class BukkitCommandManager extends AbstractCommandManager<Plugin> {

    private BukkitCommandMap commandMap;

    @Override
    public void handle(@NotNull final Plugin plugin) {
        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(
                new BukkitCommandListener(this),
                plugin
        );

        try {
            final Field commandMapField = plugin.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);

            final Object oldCommandMap = commandMapField.get(plugin.getServer());

            this.commandMap = new BukkitCommandMap(plugin.getServer(), this);

            final Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);

            final Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(knownCommandsField, knownCommandsField.getModifiers() & ~Modifier.FINAL);

            knownCommandsField.set(this.commandMap, knownCommandsField.get(oldCommandMap));
            commandMapField.set(plugin.getServer(), this.commandMap);
        } catch (Exception e) {
            e.printStackTrace();
        }

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

            Collections.sort(commands, ((o1, o2) -> o1.getName().length() - o2.getName().length()));
        }
    }
}
