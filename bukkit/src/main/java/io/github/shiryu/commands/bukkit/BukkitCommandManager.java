package io.github.shiryu.commands.bukkit;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.github.shiryu.commands.api.CommandHandler;
import io.github.shiryu.commands.api.CommandManager;
import io.github.shiryu.commands.api.annotations.Command;
import io.github.shiryu.commands.api.annotations.Parameter;
import io.github.shiryu.commands.api.locale.CommandLocale;
import io.github.shiryu.commands.api.models.CommandExecutable;
import io.github.shiryu.commands.api.models.SimpleParameter;
import io.github.shiryu.commands.api.parameter.ParameterType;
import io.github.shiryu.commands.api.sender.SimpleSender;
import io.github.shiryu.commands.bukkit.command.BukkitExecutable;
import io.github.shiryu.commands.bukkit.listener.BukkitCommandListener;
import lombok.Getter;
import org.bukkit.Bukkit;
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
public class BukkitCommandManager implements CommandManager<Plugin> {

    private final List<CommandExecutable> commands = Lists.newArrayList();
    private final Map<Class<?>, ParameterType> parameterTypes = Maps.newConcurrentMap();

    private Plugin plugin;
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

    @Override
    public <X> void registerParameterType(@NotNull final Class<X> clazz, @NotNull final ParameterType<X> parameterType) {
        this.parameterTypes.put(clazz, parameterType);
    }

    @Override
    public @NotNull Object transformParameter(@NotNull final SimpleSender sender, @NotNull final String value, @NotNull final Class<?> clazz) {
        if (clazz.equals(String.class))
            return value;

        return parameterTypes.get(clazz).transform(sender, value);
    }

    @Override
    public @NotNull CommandExecutable evalCommand(@NotNull final SimpleSender sender, @NotNull final String command) {
        String[] args = new String[]{};
        CommandExecutable found = null;

        CommandLoop:
        for (CommandExecutable commandData : commands) {
            for (String alias : commandData.getNames()) {
                String messageString = command.toLowerCase() + " ";
                String aliasString = alias.toLowerCase() + " ";

                if (messageString.startsWith(aliasString)) {
                    found = commandData;

                    if (messageString.length() > aliasString.length() && found.getParameters().size() == 0)
                        continue;



                    if (command.length() > alias.length() + 1)
                        args = (command.substring(alias.length() + 1)).split(" ");


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

    public void executeCommand(@NotNull final SimpleSender sender, @NotNull final String command){
        if (sender instanceof BukkitSender){
            final BukkitSender bukkitSender = (BukkitSender) sender;

            Bukkit.getPluginManager().callEvent(
                    new PlayerCommandPreprocessEvent(
                            (Player) bukkitSender.getSender(),
                            "/" + command
                    )
            );
        }

    }
}
