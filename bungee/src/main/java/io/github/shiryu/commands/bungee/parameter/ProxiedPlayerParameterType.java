package io.github.shiryu.commands.bungee.parameter;

import io.github.shiryu.commands.api.locale.CommandLocale;
import io.github.shiryu.commands.api.parameter.ParameterType;
import io.github.shiryu.commands.api.sender.SimpleSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ProxiedPlayerParameterType implements ParameterType<ProxiedPlayer> {

    @NotNull
    @Override
    public ProxiedPlayer transform(@NotNull final SimpleSender sender, @NotNull final String value) {
        final ProxiedPlayer player = ProxyServer.getInstance().getPlayer(value);

        if (player == null){
            sender.sendMessage(StringUtils.replace(CommandLocale.NOT_FOUND, "%s", value));

            return null;
        }

        return player;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull final SimpleSender sender, @NotNull final Set<String> flags, @NotNull final String value) {
        return ProxyServer.getInstance().getPlayers().stream()
                .map(ProxiedPlayer::getName)
                .filter(s -> StringUtils.startsWithIgnoreCase(s, value))
                .collect(Collectors.toList());
    }
}
