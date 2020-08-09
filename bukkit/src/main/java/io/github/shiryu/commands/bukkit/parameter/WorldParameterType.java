package io.github.shiryu.commands.bukkit.parameter;

import io.github.shiryu.commands.api.locale.CommandLocale;
import io.github.shiryu.commands.api.parameter.ParameterType;
import io.github.shiryu.commands.api.sender.SimpleSender;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class WorldParameterType implements ParameterType<World> {

    @NotNull
    @Override
    public World transform(@NotNull final SimpleSender sender, @NotNull final String value) {
        final World world = Bukkit.getWorld(value);

        if (world == null){
            sender.sendMessage(org.apache.commons.lang3.StringUtils.replace(CommandLocale.NOT_FOUND, "%s", value));

            return null;
        }

        return world;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull final SimpleSender sender, @NotNull final Set<String> flags, @NotNull final String value) {
       return Bukkit.getWorlds().stream()
               .map(World::getName)
               .filter(s -> StringUtils.startsWithIgnoreCase(s, value))
               .collect(Collectors.toList());
    }
}
