package io.github.shiryu.commands.bungee.listener;

import de.exceptionflug.protocolize.api.event.PacketReceiveEvent;
import de.exceptionflug.protocolize.api.handler.PacketAdapter;
import de.exceptionflug.protocolize.api.protocol.Stream;
import io.github.shiryu.commands.api.models.CommandExecutable;
import io.github.shiryu.commands.api.models.SimpleParameter;
import io.github.shiryu.commands.bungee.BungeeCommandManager;
import io.github.shiryu.commands.bungee.BungeeSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.packet.TabCompleteResponse;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BungeePacketListener extends PacketAdapter<TabCompleteResponse> {

    private final BungeeCommandManager commandManager;

    public BungeePacketListener(@NotNull final BungeeCommandManager commandManager) {
        super(Stream.DOWNSTREAM, TabCompleteResponse.class);

        this.commandManager = commandManager;
    }

    @Override
    public void receive(@NotNull final PacketReceiveEvent<TabCompleteResponse> event) {
        if (event.getPlayer() == null) return;

        final TabCompleteResponse packet = event.getPacket();

        packet.setCommands(
                commandManager.tabCompletions(
                        new BungeeSender(event.getPlayer()),
                        packet.getCommands()
                )
        );

        event.markForRewrite();
    }
}
