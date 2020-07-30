package io.github.shiryu.commands.bukkit.listener;

import io.github.shiryu.commands.bukkit.BukkitCommandManager;
import io.github.shiryu.commands.bukkit.BukkitSender;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

@RequiredArgsConstructor
public class BukkitCommandListener implements Listener {

    private final BukkitCommandManager commandManager;

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCommandPreProcess(final PlayerCommandPreprocessEvent event) {
        String command = event.getMessage().substring(1);

        commandManager.getCommandMap().getParameters().put(event.getPlayer().getUniqueId(), command.split(" "));

        if (commandManager.evalCommand(new BukkitSender(event.getPlayer()), command) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onConsoleCommand(final ServerCommandEvent event) {
        if (commandManager.evalCommand(new BukkitSender(event.getSender()), event.getCommand()) != null) {
            event.setCancelled(true);
        }
    }
}
