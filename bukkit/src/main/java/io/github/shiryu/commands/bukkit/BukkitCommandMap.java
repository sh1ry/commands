package io.github.shiryu.commands.bukkit;

import com.google.common.collect.Lists;
import io.github.shiryu.commands.api.models.CommandExecutable;
import io.github.shiryu.commands.api.models.SimpleParameter;
import lombok.Getter;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Getter
public class BukkitCommandMap extends SimpleCommandMap {

    private final BukkitCommandManager commandManager;
    private final Map<UUID, String[]> parameters = new HashMap<>();

    public BukkitCommandMap(@NotNull final Server server, @NotNull final BukkitCommandManager commandManager) {
        super(server);

        this.commandManager = commandManager;
    }

    @Override
    public List<String> tabComplete(@NotNull final CommandSender sender, @NotNull final String cmdLine) {
        if (!(sender instanceof Player))
            return null;


        final Player player = (Player) sender;

        final List<String> completions = commandManager.tabCompletions(
                new BukkitSender(player),
                super.tabComplete(sender, cmdLine) == null ? new ArrayList<>() : super.tabComplete(sender, cmdLine)
        );

        completions.remove("w");

        return completions;
    }
}
