package io.github.shiryu.commands.bukkit.parameter;

import io.github.shiryu.commands.api.locale.CommandLocale;
import io.github.shiryu.commands.api.parameter.ParameterType;
import io.github.shiryu.commands.api.sender.SimpleSender;
import io.github.shiryu.commands.bukkit.BukkitSender;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PlayerParameterType implements ParameterType<Player> {

    @NotNull
    @Override
    public Player transform(@NotNull SimpleSender sender, @NotNull String value) {
        if (sender instanceof BukkitSender && (value.equalsIgnoreCase("self") || value.isEmpty())){
            final BukkitSender bukkitSender = (BukkitSender) sender;

            if (bukkitSender.getSender() instanceof Player)
                return (Player) bukkitSender.getSender();
        }

        final Player player = Bukkit.getPlayer(value);

        if (player == null){
            sender.sendMessage(String.format(CommandLocale.NOT_FOUND, value));

            return null;
        }

        return player;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull final SimpleSender sender, @NotNull final Set<String> flags, @NotNull final String value) {
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(s -> StringUtils.startsWithIgnoreCase(s, value))
                .collect(Collectors.toList());
    }
}
