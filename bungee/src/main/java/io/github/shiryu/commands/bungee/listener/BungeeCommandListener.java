package io.github.shiryu.commands.bungee.listener;

import io.github.shiryu.commands.bungee.BungeeCommandManager;
import io.github.shiryu.commands.bungee.BungeeSender;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.command.ConsoleCommandSender;
import net.md_5.bungee.event.EventHandler;

@RequiredArgsConstructor
public class BungeeCommandListener implements Listener {

    private final BungeeCommandManager commandManager;

    @EventHandler
    public void commandUsage(final ChatEvent event){
        if (!event.isCommand() || (!(event.getSender() instanceof ProxiedPlayer))) return;

        commandManager.evalCommand(
                new BungeeSender(((ProxiedPlayer)event.getSender())),
                event.getMessage().replaceAll("/", "")
        );

        event.setCancelled(true);
    }
}
