package io.github.shiryu.commands.bukkit;

import io.github.shiryu.commands.api.sender.SimpleSender;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@Getter
@RequiredArgsConstructor
public class BukkitSender implements SimpleSender {

    private final CommandSender sender;

    @Override
    public boolean hasPermission(@NotNull final String permission) {
        return this.sender.hasPermission(permission);
    }

    @Override
    public void sendMessage(@NotNull final String message) {
        this.sender.sendMessage(message);
    }
}
