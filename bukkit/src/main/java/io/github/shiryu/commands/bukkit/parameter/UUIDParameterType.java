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
import java.util.UUID;
import java.util.stream.Collectors;

public class UUIDParameterType implements ParameterType<UUID> {

    @NotNull
    @Override
    public UUID transform(@NotNull final SimpleSender sender, @NotNull final String value) {
        if (sender instanceof BukkitSender && (value.equalsIgnoreCase("self") || value.isEmpty())){
            final BukkitSender bukkitSender = (BukkitSender) sender;

            if (bukkitSender.getSender() instanceof Player)
                return ((Player)bukkitSender.getSender()).getUniqueId();
        }

        try{
            return UUID.fromString(value);
        }catch (Exception e){
            sender.sendMessage(org.apache.commons.lang3.StringUtils.replace(CommandLocale.NOT_FOUND, "%s", value));

            return null;
        }
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull final SimpleSender sender, @NotNull final Set<String> flags, @NotNull final String value) {
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(s -> StringUtils.startsWithIgnoreCase(s, value))
                .collect(Collectors.toList());
    }
}
