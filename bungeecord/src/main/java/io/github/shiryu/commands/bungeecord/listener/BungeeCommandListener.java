package io.github.shiryu.commands.bungeecord.listener;

import io.github.shiryu.commands.bungeecord.BungeeSender;
import io.github.shiryu.commands.bungeecord.BungeecordCommandManager;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

@RequiredArgsConstructor
public class BungeeCommandListener implements Listener {

    private final BungeecordCommandManager commandManager;

    @EventHandler
    public void chat(final ChatEvent event){
        if (!(event.getSender() instanceof ProxiedPlayer)) return;
        if (!event.isCommand()) return;

        final ProxiedPlayer player = (ProxiedPlayer) event.getSender();

        commandManager.evalCommand(
                new BungeeSender(player),
                event.getMessage().replaceAll("/", "")
        );

        event.setCancelled(true);
    }
}
