package io.github.shiryu.commands.bukkit.parameter;

import io.github.shiryu.commands.api.locale.CommandLocale;
import io.github.shiryu.commands.api.parameter.ParameterType;
import io.github.shiryu.commands.api.sender.SimpleSender;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class OfflinePlayerParameterType implements ParameterType<OfflinePlayer> {

    @NotNull
    @Override
    public OfflinePlayer transform(@NotNull final SimpleSender sender, @NotNull final String value) {
        final OfflinePlayer player = Bukkit.getOfflinePlayer(value);

        if (player == null){
            sender.sendMessage(StringUtils.replace(CommandLocale.NOT_FOUND, "%s", value));

            return null;
        }

        return player;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull final SimpleSender sender, @NotNull final Set<String> flags, @NotNull final String value) {
        return Arrays.stream(Bukkit.getOfflinePlayers())
                .map(OfflinePlayer::getName)
                .filter(s -> StringUtils.startsWithIgnoreCase(s, value))
                .collect(Collectors.toList());
    }
}
